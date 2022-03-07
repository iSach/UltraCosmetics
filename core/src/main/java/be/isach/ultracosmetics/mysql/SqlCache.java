package be.isach.ultracosmetics.mysql;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.cosmetics.type.PetType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

/**
 * Package: be.isach.ultracosmetics.mysql
 * Created by: sacha
 * Date: 15/08/15
 * Project: UltraCosmetics
 */
public class SqlCache {
    private final Table table;
    private final UUID uuid;
    private boolean gadgetsEnabled;
    private boolean morphSelfView;
    private Map<PetType,String> petNames = new HashMap<>();
    private Map<GadgetType,Integer> ammo = new HashMap<>();
    private int keys;

    public SqlCache(UUID uuid, UltraCosmetics ultraCosmetics) {
        this.table = ultraCosmetics.getMySqlConnectionManager().getTable();
        this.uuid = uuid;
        Bukkit.getScheduler().runTaskAsynchronously(ultraCosmetics, () -> load());
    }

    private void load() {
        // update table with UUID. If it's already there, ignore
        table.insertIgnore().insert("uuid", uuid.toString()).execute();
        Map<String,Object> properties = table.select("*").uuid(uuid).getAll();
        gadgetsEnabled = (boolean) properties.get("gadgetsEnabled");
        morphSelfView = (boolean) properties.get("selfmorphview");
        for (PetType type : PetType.enabled()) {
            petNames.put(type, (String) properties.get(cleanPetName(type)));
        }
        for (GadgetType type : GadgetType.enabled()) {
            ammo.put(type, (int) properties.get(cleanGadgetName(type)));
        }
        keys = (int) properties.get("treasureKeys");
    }

    public int getAmmo(GadgetType gadget) {
        return ammo.get(gadget);
    }

    public void setAmmo(GadgetType type, int amount) {
        table.update().set(cleanGadgetName(type), amount).uuid(uuid).execute();
        ammo.put(type, amount);
    }

    public void addAmmo(GadgetType type, int amount) {
        setAmmo(type, getAmmo(type) + amount);
    }

    public String getPetName(PetType pet) {
        return petNames.get(pet);
    }

    public void setName(PetType pet, String name) {
        table.update().set(cleanPetName(pet), name).uuid(uuid).execute();
        petNames.put(pet, name);
    }

    public int getKeys() {
        return keys;
    }

    public void setKeys(int amount) {
        table.update().set("treasureKeys", amount).uuid(uuid).execute();
        keys = amount;
    }

    public void addKeys(int amount) {
        setKeys(getKeys() + amount);
    }

    public void setGadgetsEnabled(boolean enabled) {
        table.update().set("gadgetsEnabled", enabled).uuid(uuid).execute();
        gadgetsEnabled = enabled;
    }

    public boolean hasGadgetsEnabled() {
        return gadgetsEnabled;
    }

    public void setSeeSelfMorph(boolean enabled) {
        table.update().set("selfmorphview", enabled).uuid(uuid).execute();
        morphSelfView = enabled;
    }

    public boolean canSeeSelfMorph() {
        return morphSelfView;
    }

    private String cleanGadgetName(GadgetType gadget) {
        return gadget.toString().toLowerCase().replace("_", "");
    }

    private String cleanPetName(PetType pet) {
        return pet.getConfigName().toLowerCase();
    }
}
