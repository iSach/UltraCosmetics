<<<<<<< HEAD
package be.isach.ultracosmetics.mysql;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.manager.SqlLoader;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Package: be.isach.ultracosmetics.mysql
 * Created by: sachalewin
 * Date: 5/08/16
 * Project: UltraCosmetics
 */
public class MySqlConnectionManager extends BukkitRunnable {

    /**
     * Player Sql Indexs.
     */
    public static final Map<UUID, Integer> INDEXS = new HashMap<>();

    /**
     * UltraCosmetics instance.
     */
    private UltraCosmetics ultraCosmetics;

    /**
     * MySQL Connection & Table.
     */
    public Connection co;
    private Table table;

    /**
     * SQLLoader Manager instance
     */
    private SqlLoader sqlLoader;

    /**
     * MySQL Stuff.
     */
    private MySqlConnection sql;

    /**
     * Sql Utils instance.
     */
    private SqlUtils sqlUtils;

    public MySqlConnectionManager(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        this.sqlUtils = new SqlUtils(this);
    }

    public void start() {
        runTaskTimerAsynchronously(ultraCosmetics, 0, 24000);
    }

    @Override
    public void run() {
        try {
            String hostname = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.MySQL.hostname"));
            String portNumber = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.MySQL.port"));
            String database = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.MySQL.database"));
            String username = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.MySQL.username"));
            String password = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.MySQL.password"));
            sql = new MySqlConnection(hostname, portNumber, database, username, password);
            co = sql.getConnection();
            Bukkit.getConsoleSender().sendMessage("§b§lUltraCosmetics -> Successfully connected to MySQL server! :)");
            PreparedStatement sql = co.prepareStatement("CREATE TABLE IF NOT EXISTS UltraCosmeticsData(" +
                    "id INTEGER not NULL AUTO_INCREMENT," +
                    " uuid VARCHAR(255)," +
                    " username VARCHAR(255),"
                    + " PRIMARY KEY ( id ))");
            sql.executeUpdate();
            for (GadgetType gadgetType : GadgetType.values()) {
                DatabaseMetaData md = co.getMetaData();
                ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", gadgetType.toString().replace("_", "").toLowerCase());
                if (!rs.next()) {
                    PreparedStatement statement = co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD " + gadgetType.toString().replace("_", "").toLowerCase() + " INTEGER DEFAULT 0 not NULL");
                    statement.executeUpdate();
                }
            }
            table = new Table(co, "UltraCosmeticsData");
            DatabaseMetaData md = co.getMetaData();
            ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", "treasureKeys");
            if (!rs.next()) {
                PreparedStatement statement = co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD treasureKeys INTEGER DEFAULT 0 NOT NULL");
                statement.executeUpdate();
            }

            ultraCosmetics.getSmartLogger().write("initial SQLLoader to reduce lag when table is large");
            sqlLoader = new SqlLoader(ultraCosmetics);

            INDEXS.putAll(sqlUtils.getIds());

        } catch (Exception e) {
            Bukkit.getLogger().info("");
            Bukkit.getConsoleSender().sendMessage("§c§lUltra Cosmetics >>> Could not connect to MySQL server!");
            Bukkit.getLogger().info("");
            Bukkit.getConsoleSender().sendMessage("§c§lError:");
            e.printStackTrace();
        }
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
}
=======
package be.isach.ultracosmetics.mysql;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.manager.SqlLoader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Package: be.isach.ultracosmetics.mysql
 * Created by: sachalewin
 * Date: 5/08/16
 * Project: UltraCosmetics
 */
public class MySqlConnectionManager extends BukkitRunnable {

    /**
     * Player Sql Indexs.
     */
    public static final Map<UUID, Integer> INDEXS = new HashMap<>();

    /**
     * UltraCosmetics instance.
     */
    private UltraCosmetics ultraCosmetics;

    /**
     * MySQL Connection & Table.
     */
    public Connection co;
    private Table table;

    /**
     * SQLLoader Manager instance
     */
    private SqlLoader sqlLoader;

    /**
     * MySQL Stuff.
     */
    private MySqlConnection sql;

    /**
     * Sql Utils instance.
     */
    private SqlUtils sqlUtils;

    public MySqlConnectionManager(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        this.sqlUtils = new SqlUtils(this);
    }

    public void start() {
        runTaskTimerAsynchronously(ultraCosmetics, 0, 24000);
    }

    @Override
    public void run() {
        try {
            String hostname = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.MySQL.hostname"));
            String portNumber = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.MySQL.port"));
            String database = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.MySQL.database"));
            String username = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.MySQL.username"));
            String password = String.valueOf(SettingsManager.getConfig().get("Ammo-System-For-Gadgets.MySQL.password"));
            sql = new MySqlConnection(hostname, portNumber, database, username, password);
            co = sql.getConnection();
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "UltraCosmetics -> Successfully connected to MySQL server! :)");
            PreparedStatement sql = co.prepareStatement("CREATE TABLE IF NOT EXISTS UltraCosmeticsData(" +
                    "id INTEGER not NULL AUTO_INCREMENT," +
                    " uuid VARCHAR(255)," +
                    " username VARCHAR(255),"
                    + " PRIMARY KEY ( id ))");
            sql.executeUpdate();
            for (GadgetType gadgetType : GadgetType.values()) {
                DatabaseMetaData md = co.getMetaData();
                ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", gadgetType.toString().replace("_", "").toLowerCase());
                if (!rs.next()) {
                    PreparedStatement statement = co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD " + gadgetType.toString().replace("_", "").toLowerCase() + " INTEGER DEFAULT 0 not NULL");
                    statement.executeUpdate();
                }
            }
            table = new Table(co, "UltraCosmeticsData");
            DatabaseMetaData md = co.getMetaData();
            ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", "treasureKeys");
            if (!rs.next()) {
                PreparedStatement statement = co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD treasureKeys INTEGER DEFAULT 0 NOT NULL");
                statement.executeUpdate();
            }

            ultraCosmetics.getSmartLogger().write("initial SQLLoader to reduce lag when table is large");
            sqlLoader = new SqlLoader(ultraCosmetics);

            INDEXS.putAll(sqlUtils.getIds());

        } catch (Exception e) {
            Bukkit.getLogger().info("");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Ultra Cosmetics >>> Could not connect to MySQL server!");
            Bukkit.getLogger().info("");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Error:");
            e.printStackTrace();
        }
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
}
>>>>>>> refs/remotes/origin/master
