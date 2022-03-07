package be.isach.ultracosmetics.mysql;

import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.cosmetics.type.PetType;
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

    public int getAmmo(UUID uuid, GadgetType gadget) {
        return connectionManager.getTable().select(gadget.toString().toLowerCase().replace("_", "")).uuid(uuid).asInt();
    }

    public String getPetName(UUID uuid, PetType pet) {
        return connectionManager.getTable().select(pet.getConfigName().toLowerCase()).uuid(uuid).asString();
    }

    public void setName(UUID uuid, PetType pet, String name) {
        connectionManager.getTable().update().set(pet.getConfigName().toLowerCase(), name).uuid(uuid).execute();
    }

    public int getKeys(UUID uuid) {
        return connectionManager.getTable().select("treasureKeys").uuid(uuid).asInt();
    }

    public void addKeys(UUID uuid, int amount) {
        // it's possible to add one to an existing value in one statement,
        // but it's less complicated to do it in two.
        connectionManager.getTable().update().set("treasureKeys", getKeys(uuid) + amount).uuid(uuid).execute();
    }

    public void addAmmo(UUID uuid, GadgetType type, int amount) {
        String column = type.toString().toLowerCase().replace("_", "");
        connectionManager.getTable().update().set(column, getAmmo(uuid, type) + amount).uuid(uuid).execute();
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
