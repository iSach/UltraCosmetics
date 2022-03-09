package be.isach.ultracosmetics.player.profile;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.*;
import be.isach.ultracosmetics.player.UltraPlayer;

import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Used to save what cosmetics a player toggled.
 */
public class FileCosmeticsProfile extends CosmeticsProfile {

    public FileCosmeticsProfile(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
        super(ultraPlayer, ultraCosmetics);
    }

    /**
     * Loads the profile from the player file.
     */
    @Override
    protected void load() {
        SettingsManager sm = SettingsManager.getData(ultraPlayer.getUUID());
        if (sm.fileConfiguration.isConfigurationSection("enabled")) {
            loadEnabled(sm);
        }
        

        for (PetType pet : PetType.enabled()) {
            petNames.put(pet, sm.getString("Pet-Names." + pet.getConfigName()));
        }

        if (UltraCosmeticsData.get().isAmmoEnabled()) {
            for (GadgetType gadget : GadgetType.enabled()) {
                ammo.put(gadget, sm.getInt("Ammo." + gadget.getConfigName()));
            }
        }

        keys = sm.getInt("Keys");
        gadgetsEnabled = sm.getBoolean("Gadgets-Enabled", true);
        morphSelfView = sm.getBoolean("Third-Person-Morph-View", true);
        treasureNotifications = sm.getBoolean("Treasure-Notifications", true);
        filterByOwned = sm.getBoolean("Filter-By-Owned", false);
    }

    public void loadEnabled(SettingsManager sm) {
        ConfigurationSection s = sm.fileConfiguration.getConfigurationSection("enabled");
        for (Category cat : Category.values()) {
            if (cat == Category.SUITS) continue; // handled below
            String key = cat.toString().toLowerCase();
            String oldKey = key.substring(0, key.length() - 1);
            String value;
            if (!s.isString(key) && s.isString(oldKey)) {
                value = s.getString(oldKey);
                s.set(key, value);
                s.set(oldKey, null);
            } else {
                value = s.getString(key);
            }
            if (value == null || value.equals("none")) continue;
            enabled.put(cat, cat.valueOfType(value));
        }

        String suitKey = Category.SUITS.toString().toLowerCase();
        String oldSuitKey = suitKey.substring(0, suitKey.length() - 1);
        if (!s.isConfigurationSection(suitKey) && s.isConfigurationSection(oldSuitKey)) {
            ConfigurationSection value = s.getConfigurationSection(oldSuitKey);
            s.set(suitKey, value);
            s.set(oldSuitKey, null);
        }
        for (ArmorSlot slot : ArmorSlot.values()) {
            String slotKey = slot.toString().toLowerCase();
            String value = s.getString(suitKey + "." + slotKey);
            if (value == null || value.equals("none")) continue;
            enabled.put(Category.SUITS, SuitCategory.valueOf(value).getPiece(slot));
        }
    }

    @Override
    public void save() {
        SettingsManager data = SettingsManager.getData(ultraPlayer.getUUID());

        data.set("Keys", keys);
        data.set("Gadgets-Enabled", gadgetsEnabled);
        data.set("Third-Person-Morph-View", morphSelfView);
        data.set("Treasure-Notifications", treasureNotifications);
        data.set("Filter-By-Owned", filterByOwned);

        for (Category cat : Category.enabled()) {
            if (cat == Category.SUITS) continue; // handled in loop below
            CosmeticType<?> type = enabled.get(cat);
            data.set("enabled." + cat.toString().toLowerCase(), type == null ? null : type.getConfigName());
        }
        for (ArmorSlot slot : ArmorSlot.values()) {
            SuitType type = enabledSuitParts.get(slot);
            data.set("enabled.suit." + slot.toString().toLowerCase(), type == null ? null : type.getConfigName());
        }

        for (Entry<PetType,String> entry : petNames.entrySet()) {
            data.set("Pet-Names." + entry.getKey().getConfigName(), entry.getValue());
        }

        for (Entry<GadgetType,Integer> entry : ammo.entrySet()) {
            data.set("Ammo." + entry.getKey().getConfigName().toLowerCase(), entry.getValue());
        }
    }
}
