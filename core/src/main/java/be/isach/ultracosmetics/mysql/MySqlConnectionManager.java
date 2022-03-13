package be.isach.ultracosmetics.mysql;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.log.SmartLogger;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import javax.sql.DataSource;

/**
 * Package: be.isach.ultracosmetics.mysql
 * Created by: sachalewin
 * Date: 5/08/16
 * Project: UltraCosmetics
 */
public class MySqlConnectionManager extends BukkitRunnable {
    private final String tableName;
    /**
     * UltraCosmetics instance.
     */
    private final UltraCosmetics ultraCosmetics;

    /**
     * MySQL Connection & Table.
     */
    private Table table;

    /**
     * Connecting pooling.
     */
    private final HikariDataSource dataSource;
    private final String CREATE_TABLE;
    private final List<Column> columns = new ArrayList<>();
    private final boolean debug;
    private boolean success = true;

    public MySqlConnectionManager(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        ConfigurationSection section = SettingsManager.getConfig().getConfigurationSection("MySQL");
        this.debug = section.getBoolean("debug", false);
        String hostname = section.getString("hostname");
        String port = section.getString("port");
        String database = section.getString("database");
        String username = section.getString("username");
        String password = section.getString("password");
        tableName = section.getString("table");
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        // performance tips from https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("useServerPrepStmts", true);

        dataSource = new HikariDataSource(config);

        // "PRIMARY KEY" implies UNIQUE NOT NULL.
        // String form of UUID is always exactly 36 chars so just store it that way.
        columns.add(new Column("uuid", "CHAR(36) PRIMARY KEY"));
        columns.add(new Column("gadgetsEnabled", "BOOLEAN DEFAULT TRUE NOT NULL"));
        columns.add(new Column("selfmorphview", "BOOLEAN DEFAULT TRUE NOT NULL"));
        columns.add(new Column("treasureNotifications", "BOOLEAN DEFAULT TRUE NOT NULL"));
        columns.add(new Column("filterByOwned", "BOOLEAN DEFAULT FALSE NOT NULL"));
        columns.add(new Column("treasureKeys", "INTEGER DEFAULT 0 NOT NULL"));
        for (GadgetType gadgetType : GadgetType.values()) {
            columns.add(new Column(gadgetType.getConfigName().toLowerCase(), "INTEGER DEFAULT 0 NOT NULL"));
        }
        for (PetType petType : PetType.values()) {
            // Anvil can only hold 50 characters on 1.18, but there's no extra overhead between 50 and 255.
            // This way if they extend the anvil size again we'll be fine.
            columns.add(new Column(petType.getConfigName().toLowerCase(), "VARCHAR(255)"));
        }

        for (Category cat : Category.values()) {
            // it's a varchar anyway so might as well make it 255 for expansion purposes
            if (cat == Category.SUITS) {
                for (ArmorSlot slot : ArmorSlot.values()) {
                    columns.add(new Column(cat.toString().toLowerCase() + "_" + slot.toString().toLowerCase(), "VARCHAR(255)"));
                }
                continue;
            }
            
            columns.add(new Column(cat.toString().toLowerCase(), "VARCHAR(255)"));
        }

        StringJoiner columnJoiner = new StringJoiner(", ", "(", ")");
        for (Column column : columns) {
            columnJoiner.add(column.toString());
        }
        CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + tableName + columnJoiner.toString();
    }

    public void start() {
        runTaskAsynchronously(ultraCosmetics);
    }

    @Override
    public void run() {
        try (Connection co = dataSource.getConnection()) {
            try (PreparedStatement sql = co.prepareStatement(CREATE_TABLE)) {
                sql.executeUpdate();
            }

            fixTable(co);

            table = new Table(dataSource, tableName);
        } catch (SQLException e) {
            reportFailure(e);
            return;
        }
    }

    private void reportFailure(Exception e) {
        success = false;
        SmartLogger log = ultraCosmetics.getSmartLogger();
        log.write(LogLevel.ERROR, "Could not connect to MySQL server!");
        log.write(LogLevel.ERROR, "Error:");
        e.printStackTrace();
    }

    public Table getTable() {
        return table;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean success() {
        return success;
    }

    public void shutdown() {
        dataSource.close();
    }

    /**
     * Based on the field 'columns', adds any missing columns to the database (in order).
     * 
     * @param co Connection to work with.
     */
    private void fixTable(Connection co) throws SQLException {
        DatabaseMetaData md = co.getMetaData();
        boolean upgradeAnnounced = false;
        try (ResultSet rs = md.getColumns(null, null, tableName, "id")) {
            if (rs.next()) {
                ultraCosmetics.getSmartLogger().write("You have an old database. UC will attempt to upgrade it...");
                List<String> commands = new ArrayList<>();
                commands.add("DROP COLUMN id");
                commands.add("DROP COLUMN username");
                commands.add("ADD PRIMARY KEY (uuid)");
                commands.add("MODIFY uuid CHAR(36)");
                for (String command : commands) {
                    alter(co, command);
                }
            }
        }

        for (int i = 0; i < columns.size(); i++) {
            Column col = columns.get(i);
            try (ResultSet rs = md.getColumns(null, null, tableName, col.getName())) {
                if (!rs.next()) {
                    if (!upgradeAnnounced) {
                        ultraCosmetics.getSmartLogger().write("Upgrading database...");
                        upgradeAnnounced = true;
                    }
                    String afterPrevious;
                    if (i == 0) {
                        afterPrevious = "FIRST";
                    } else {
                        afterPrevious = "AFTER " + columns.get(i - 1).getName();
                    }
                    alter(co, "ADD " + col.toString() + " " + afterPrevious);
                }
            }
        }
        if (upgradeAnnounced) {
            ultraCosmetics.getSmartLogger().write("Upgrade finished.");
        }
    }

    private void alter(Connection co, String command) throws SQLException {
        PreparedStatement ps = co.prepareStatement("ALTER TABLE " + tableName + " " + command);
        ps.execute();
        ps.close();
    }
}
