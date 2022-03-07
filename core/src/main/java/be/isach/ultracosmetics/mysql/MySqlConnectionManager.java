package be.isach.ultracosmetics.mysql;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.log.SmartLogger;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;
import be.isach.ultracosmetics.manager.SqlLoader;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    public static final String TABLE_NAME = "UltraCosmeticsData";
    /**
     * UltraCosmetics instance.
     */
    private UltraCosmetics ultraCosmetics;

    /**
     * MySQL Connection & Table.
     */
    private Table table;

    /**
     * SQLLoader Manager instance
     */
    private SqlLoader sqlLoader;

    /**
     * Sql Utils instance.
     */
    private SqlUtils sqlUtils;

    /**
     * Connecting pooling.
     */
    private final HikariDataSource dataSource;
    private final String CREATE_TABLE;
    private final List<Column> columns = new ArrayList<>();

    public MySqlConnectionManager(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        this.sqlUtils = new SqlUtils(this);
        String hostname = SettingsManager.getConfig().getString("Ammo-System-For-Gadgets.MySQL.hostname");
        String port = SettingsManager.getConfig().getString("Ammo-System-For-Gadgets.MySQL.port");
        String database = SettingsManager.getConfig().getString("Ammo-System-For-Gadgets.MySQL.database");
        String username = SettingsManager.getConfig().getString("Ammo-System-For-Gadgets.MySQL.username");
        String password = SettingsManager.getConfig().getString("Ammo-System-For-Gadgets.MySQL.password");
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
        columns.add(new Column("treasureKeys", "INTEGER DEFAULT 0 NOT NULL"));
        for (GadgetType gadgetType : GadgetType.values()) {
            columns.add(new Column(gadgetType.getConfigName().toLowerCase(), "INTEGER DEFAULT 0 NOT NULL"));
        }
        for (PetType petType : PetType.values()) {
            // Anvil can only hold 50 characters on 1.18, but there's no extra overhead between 50 and 255.
            // This way if they extend the anvil size again we'll be fine.
            columns.add(new Column(petType.getConfigName().toLowerCase(), "VARCHAR(255)"));
        }

        StringJoiner columnJoiner = new StringJoiner(", ", "(", ")");
        for (Column column : columns) {
            columnJoiner.add(column.toString());
        }
        CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + columnJoiner.toString();
    }

    public void start() {
        runTaskAsynchronously(ultraCosmetics);
    }

    @Override
    public void run() {
        try (Connection co = dataSource.getConnection()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "UltraCosmetics -> Successfully connected to MySQL server! :)");
            try (PreparedStatement sql = co.prepareStatement(CREATE_TABLE)) {
                sql.executeUpdate();
            }

            fixTable(co);

            table = new Table(dataSource, TABLE_NAME);
            sqlLoader = new SqlLoader(ultraCosmetics);
        } catch (SQLException e) {
            reportFailure(e);
            return;
        }
    }

    private void reportFailure(Exception e) {
        SmartLogger log = ultraCosmetics.getSmartLogger();
        log.write(LogLevel.ERROR, "Could not connect to MySQL server!");
        log.write(LogLevel.ERROR, "Error:");
        e.printStackTrace();
    }

    public Table getTable() {
        return table;
    }

    public SqlUtils getSqlUtils() {
        return sqlUtils;
    }

    public SqlLoader getSqlLoader() {
        return sqlLoader;
    }

    public DataSource getDataSource() {
        return dataSource;
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
        try (ResultSet rs = md.getColumns(null, null, TABLE_NAME, "id")) {
            if (rs.next()) {
                ultraCosmetics.getSmartLogger().write("You have an old database. UC will attempt to upgrade it...");
                List<String> commands = new ArrayList<>();
                commands.add("DROP COLUMN id");
                commands.add("DROP COLUMN username");
                commands.add("ADD PRIMARY KEY (uuid)");
                commands.add("MODIFY uuid CHAR(36)");
                for (String command : commands) {
                    co.prepareStatement("ALTER TABLE " + TABLE_NAME + " " + command).execute();
                }
            }
        }
        for (int i = 0; i < columns.size(); i++) {
            Column col = columns.get(i);
            try (ResultSet rs = md.getColumns(null, null, TABLE_NAME, col.getName())) {
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
                    PreparedStatement ps = co.prepareStatement("ALTER TABLE " + TABLE_NAME + " ADD " + col.toString() + " " + afterPrevious);
                    ps.execute();
                    ps.close();
                }
            }
        }
        if (upgradeAnnounced) {
            ultraCosmetics.getSmartLogger().write("Upgrade finished.");
        }
    }
}
