package be.isach.ultracosmetics.player.profile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.cosmetics.type.SuitCategory;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.mysql.StandardQuery;
import be.isach.ultracosmetics.mysql.Table;

public class PlayerData {
    private UUID uuid;
    private int keys;
    private boolean gadgetsEnabled;
    private boolean morphSelfView;
    private boolean treasureNotifications;
    private boolean filterByOwned;
    private Map<PetType,String> petNames = new HashMap<>();
    private Map<GadgetType,Integer> ammo = new HashMap<>();
    private Map<Category,CosmeticType<?>> enabledCosmetics = new HashMap<>();
    private Map<ArmorSlot, SuitType> enabledSuitParts = new HashMap<>();
    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    public int getKeys() {
        return keys;
    }

    public void setKeys(int keys) {
        this.keys = keys;
    }

    public boolean isGadgetsEnabled() {
        return gadgetsEnabled;
    }

    public void setGadgetsEnabled(boolean gadgetsEnabled) {
        this.gadgetsEnabled = gadgetsEnabled;
    }

    public boolean isMorphSelfView() {
        return morphSelfView;
    }

    public void setMorphSelfView(boolean morphSelfView) {
        this.morphSelfView = morphSelfView;
    }

    public boolean isTreasureNotifications() {
        return treasureNotifications;
    }

    public void setTreasureNotifications(boolean treasureNotifications) {
        this.treasureNotifications = treasureNotifications;
    }

    public boolean isFilterByOwned() {
        return filterByOwned;
    }

    public void setFilterByOwned(boolean filterByOwned) {
        this.filterByOwned = filterByOwned;
    }

    public Map<PetType, String> getPetNames() {
        return petNames;
    }

    public Map<GadgetType, Integer> getAmmo() {
        return ammo;
    }

    public Map<Category,CosmeticType<?>> getEnabledCosmetics() {
        return enabledCosmetics;
    }

    public Map<ArmorSlot,SuitType> getEnabledSuitParts() {
        return enabledSuitParts;
    }

    /**
     * Loads the profile from the player file.
     */
    public void loadFromFile() {
        SettingsManager sm = SettingsManager.getData(uuid);
        if (sm.fileConfiguration.isConfigurationSection("enabled")) {
            cosmeticsFromFile(sm);
        }

        for (PetType pet : PetType.enabled()) {
            petNames.put(pet, sm.getString(ProfileKey.PET_NAMES.getFileKey() + "." + pet.getConfigName()));
        }

        for (GadgetType gadget : GadgetType.enabled()) {
            ammo.put(gadget, sm.getInt(ProfileKey.AMMO.getFileKey() + "." + gadget.getConfigName().toLowerCase()));
        }

        keys = sm.getInt(ProfileKey.KEYS.getFileKey());
        gadgetsEnabled = sm.getBoolean(ProfileKey.GADGETS_ENABLED.getFileKey(), true);
        morphSelfView = sm.getBoolean(ProfileKey.MORPH_VIEW.getFileKey(), true);
        treasureNotifications = sm.getBoolean(ProfileKey.TREASURE_NOTIFICATION.getFileKey(), true);
        filterByOwned = sm.getBoolean(ProfileKey.FILTER_OWNED.getFileKey(), false);
    }

    private void cosmeticsFromFile(SettingsManager sm) {
        ConfigurationSection s = sm.fileConfiguration.getConfigurationSection("enabled");
        boolean changed = false;
        for (Category cat : Category.values()) {
            if (cat == Category.SUITS) continue; // handled below
            String key = cat.toString().toLowerCase();
            String oldKey = key.substring(0, key.length() - 1);
            String value;
            if (s.isString(oldKey)) {
                value = s.getString(oldKey);
                s.set(key, value);
                s.set(oldKey, null);
                changed = true;
            } else {
                value = s.getString(key);
            }
            if (value == null || value.equals("none")) continue;
            enabledCosmetics.put(cat, cat.valueOfType(value));
        }

        String suitKey = Category.SUITS.toString().toLowerCase();
        String oldSuitKey = suitKey.substring(0, suitKey.length() - 1);
        if (s.isConfigurationSection(oldSuitKey)) {
            ConfigurationSection value = s.getConfigurationSection(oldSuitKey);
            s.set(suitKey, value);
            s.set(oldSuitKey, null);
            changed = true;
        }
        for (ArmorSlot slot : ArmorSlot.values()) {
            String slotKey = slot.toString().toLowerCase();
            String value = s.getString(suitKey + "." + slotKey);
            if (value == null || value.equals("none")) continue;
            enabledCosmetics.put(Category.SUITS, SuitCategory.valueOf(value.toUpperCase()).getPiece(slot));
        }
        if (changed) sm.save();
    }

    public void saveToFile() {
        SettingsManager data = SettingsManager.getData(uuid);

        data.set(ProfileKey.KEYS.getFileKey(), keys);
        data.set(ProfileKey.GADGETS_ENABLED.getFileKey(), gadgetsEnabled);
        data.set(ProfileKey.MORPH_VIEW.getFileKey(), morphSelfView);
        data.set(ProfileKey.TREASURE_NOTIFICATION.getFileKey(), treasureNotifications);
        data.set(ProfileKey.FILTER_OWNED.getFileKey(), filterByOwned);

        for (Category cat : Category.enabled()) {
            if (cat == Category.SUITS) continue; // handled in loop below
            CosmeticType<?> type = enabledCosmetics.get(cat);
            data.set("enabled." + cat.toString().toLowerCase(), type == null ? null : type.getConfigName().toLowerCase());
        }

        for (ArmorSlot slot : ArmorSlot.values()) {
            SuitType type = enabledSuitParts.get(slot);
            data.set("enabled." + Category.SUITS.toString().toLowerCase() + "." + slot.toString().toLowerCase(), type == null ? null : type.getConfigName().toLowerCase());
        }

        for (Entry<PetType,String> entry : petNames.entrySet()) {
            data.set(ProfileKey.PET_NAMES.getFileKey() + "." + entry.getKey().getConfigName(), entry.getValue());
        }

        for (Entry<GadgetType,Integer> entry : ammo.entrySet()) {
            Integer amount = entry.getValue();
            // carefully handled because numeric comparisons auto-unbox but null checks do not
            if (amount != null && amount == 0) amount = null;
            data.set(ProfileKey.AMMO.getFileKey() + "." + entry.getKey().getConfigName().toLowerCase(), amount);
        }
        data.save();
    }

    public void loadFromSQL() {
        Table table = UltraCosmeticsData.get().getPlugin().getMySqlConnectionManager().getTable();
        // update table with UUID. If it's already there, ignore
        table.insertIgnore().insert("uuid", uuid.toString()).execute();
        Map<String,Object> properties = table.select("*").uuid(uuid).getAll();
        gadgetsEnabled = (boolean) properties.get(ProfileKey.GADGETS_ENABLED.getSqlKey());
        morphSelfView = (boolean) properties.get(ProfileKey.MORPH_VIEW.getSqlKey());
        for (PetType type : PetType.enabled()) {
            petNames.put(type, (String) properties.get(Table.cleanCosmeticName(type)));
        }
        for (GadgetType type : GadgetType.enabled()) {
            ammo.put(type, (int) properties.get(Table.cleanCosmeticName(type)));
        }
        keys = (int) properties.get(ProfileKey.KEYS.getSqlKey());
        for (Category cat : Category.enabled()) {
            if (cat == Category.SUITS) {
                for (ArmorSlot slot : ArmorSlot.values()) {
                    String suitCategory = (String) properties.get(Category.SUITS.toString().toLowerCase() + "_" + slot.toString().toLowerCase());
                    if (suitCategory == null) continue;
                    enabledSuitParts.put(slot, SuitCategory.valueOf(suitCategory.toUpperCase()).getPiece(slot));
                }
                continue;
            }
            enabledCosmetics.put(cat, cat.valueOfType((String) properties.get(Table.cleanCategoryName(cat))));
        }
    }

    public void saveToSQL() {
        Table table = UltraCosmeticsData.get().getPlugin().getMySqlConnectionManager().getTable();
        // should have been added on load but just to be safe
        table.insertIgnore().insert("uuid", uuid.toString()).execute();
        StandardQuery query = table.update().uuid(uuid);
        query.set(ProfileKey.KEYS.getSqlKey(), keys);
        query.set(ProfileKey.GADGETS_ENABLED.getSqlKey(), gadgetsEnabled);
        query.set(ProfileKey.MORPH_VIEW.getSqlKey(), morphSelfView);
        query.set(ProfileKey.TREASURE_NOTIFICATION.getSqlKey(), treasureNotifications);
        query.set(ProfileKey.FILTER_OWNED.getSqlKey(), filterByOwned);
        petNames.forEach((k,v) -> query.set(Table.cleanCosmeticName(k), v));
        ammo.forEach((k,v) -> query.set(Table.cleanCosmeticName(k), v == null ? 0 : v));
        enabledCosmetics.forEach((k,v) -> query.set(Table.cleanCategoryName(k), Table.cleanCosmeticName(v)));
        enabledSuitParts.forEach((k,v) -> query.set(Table.cleanCategoryName(Category.SUITS) + "_" + k.toString().toLowerCase(), Table.cleanCosmeticName(v)));
        query.execute();
    }
}
