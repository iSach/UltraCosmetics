package be.isach.ultracosmetics.mysql;

import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Package: be.isach.ultracosmetics.mysql
 * Created by: sacha
 * Date: 15/08/15
 * Project: UltraCosmetics
 */
public class SqlUtils {
    private MySqlConnectionManager connectionManager;

    public SqlUtils(MySqlConnectionManager MySqlConnectionManager) {
        this.connectionManager = MySqlConnectionManager;
    }

    public void initStats(UltraPlayer up) {
        Player p = up.getBukkitPlayer();
        connectionManager.getTable().insertIgnore().insert("uuid", p.getUniqueId().toString()).execute();
    }

    public int getAmmo(UUID uuid, String name) {
        return connectionManager.getTable().select(name.replace("_", "")).uuid(uuid).asInt();
    }

    public String getPetName(UUID uuid, String pet) {
        return connectionManager.getTable().select("name" + pet).uuid(uuid).asString();
    }

    public void setName(UUID uuid, String pet, String name) {
        connectionManager.getTable().update().set("name" + pet, name).uuid(uuid).execute();
    }

    public int getKeys(UUID uuid) {
        return connectionManager.getTable().select("treasureKeys").uuid(uuid).asInt();
    }

    public void addKeys(UUID uuid, int amount) {
        connectionManager.getTable().update().set("treasureKeys", "treasureKeys+" + amount).uuid(uuid).execute();
    }

    public void addAmmo(UUID uuid, String name, int amount) {
        String column = name.replace("_", "");
        // fancy logic along the lines of "UPDATE X SET COLUMN = COLUMN+AMOUNT" so that MySQL will check the current amount for us
        connectionManager.getTable().update().set(column, column + "+" + amount).uuid(uuid).execute();
    }

    public void setGadgetsEnabled(UUID uuid, boolean enabled) {
        connectionManager.getTable().update().set("gadgetsEnabled", enabled).uuid(uuid).execute();
    }

    public boolean hasGadgetsEnabled(UUID uuid) {
        return connectionManager.getTable().select("gadgetsEnabled").uuid(uuid).asBool();
    }

    public void setSeeSelfMorph(UUID uuid, boolean enabled) {
        connectionManager.getTable().update().set("selfmorphview", enabled).uuid(uuid).execute();
    }

    public boolean canSeeSelfMorph(UUID uuid) {
        return connectionManager.getTable().select("selfmorphview").uuid(uuid).asBool();
    }
}
