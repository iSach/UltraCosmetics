package be.isach.ultracosmetics.mysql;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
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

        dataSource = new HikariDataSource(config);
        // performance tips from https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        dataSource.addDataSourceProperty("cachePrepStmts", true);
        dataSource.addDataSourceProperty("useServerPrepStmts", true);

        StringJoiner columns = new StringJoiner("(", ", ", ")");
        // "PRIMARY KEY" implies UNIQUE NOT NULL
        columns.add("uuid CHAR(36) PRIMARY KEY");
        columns.add("gadgetsEnabled BOOLEAN DEFAULT TRUE NOT NULL");
        columns.add("selfmorphview BOOLEAN DEFAULT TRUE NOT NULL");
        columns.add("treasureKeys INTEGER DEFAULT 0 NOT NULL");
        CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + columns.toString();
    }

    public void start() {
        runTaskTimerAsynchronously(ultraCosmetics, 0, 24000);
    }

    @Override
    public void run() {
        Connection co;
        try {
            co = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "UltraCosmetics -> Successfully connected to MySQL server! :)");
        try (PreparedStatement sql = co.prepareStatement(CREATE_TABLE)) {
            sql.executeUpdate();
            DatabaseMetaData md = co.getMetaData();
            for (GadgetType gadgetType : GadgetType.values()) {
                String gadgetName = gadgetType.toString().replace("_", "").toLowerCase();
                ResultSet rs = md.getColumns(null, null, TABLE_NAME, gadgetName);
                if (!rs.next()) {
                    PreparedStatement statement = co.prepareStatement("ALTER TABLE " + TABLE_NAME + " ADD " + gadgetName + " INTEGER DEFAULT 0 not NULL");
                    statement.executeUpdate();
                    statement.close();
                }
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table = new Table(dataSource, TABLE_NAME);

        ultraCosmetics.getSmartLogger().write("initial SQLLoader to reduce lag when table is large");
        sqlLoader = new SqlLoader(ultraCosmetics);
        try {
            
        } catch (Exception e) {
            reportFailure(e);
        }
    }

    private void reportFailure(Exception e) {
        Bukkit.getLogger().info("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Ultra Cosmetics >>> Could not connect to MySQL server!");
        Bukkit.getLogger().info("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Error:");
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
}
