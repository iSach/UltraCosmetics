package be.isach.ultracosmetics.player.profile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;
import be.isach.ultracosmetics.player.UltraPlayer;

public abstract class CosmeticsProfile {
    protected final UltraPlayer ultraPlayer;
    protected final UUID uuid;
    protected final UltraCosmetics ultraCosmetics;
    protected Map<Category,CosmeticType<?>> enabled = new HashMap<>();
    protected Map<ArmorSlot, SuitType> enabledSuitParts = new HashMap<>();
    protected int keys;
    protected boolean gadgetsEnabled;
    protected boolean morphSelfView;
    protected boolean treasureNotifications;
    protected boolean filterByOwned;
    protected Map<PetType,String> petNames = new HashMap<>();
    protected Map<GadgetType,Integer> ammo = new HashMap<>();
    public CosmeticsProfile(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
        this.ultraPlayer = ultraPlayer;
        this.uuid = ultraPlayer.getUUID();
        this.ultraCosmetics = ultraCosmetics;
        Bukkit.getScheduler().runTaskAsynchronously(ultraCosmetics, () -> {
            load();
            if (!UltraCosmeticsData.get().areCosmeticsProfilesEnabled()) return;
            if (!ultraPlayer.isOnline()) {
                UltraCosmeticsData.get().getPlugin().getSmartLogger().write(LogLevel.WARNING, "Player " + ultraPlayer.getUsername() + " is no longer online, cancelling load");
                return;
            }
            Bukkit.getScheduler().runTask(ultraCosmetics, () -> equip());
        });
    }

    protected abstract void load();
    public abstract void save();

    public void equip() {
        if (!SettingsManager.isAllowedWorld(ultraPlayer.getBukkitPlayer().getWorld())) return;
        for (Entry<Category,CosmeticType<?>> type : enabled.entrySet()) {
            if (type.getValue() == null || !type.getKey().isEnabled() || !type.getValue().isEnabled()) continue;
            type.getValue().equip(ultraPlayer, ultraCosmetics);
        }

        if (Category.SUITS.isEnabled()) {
            for (SuitType type : enabledSuitParts.values()) {
                if (type == null || !type.isEnabled()) continue;
                type.equip(ultraPlayer, ultraCosmetics);
            }
        }
    }

    public void setEnabledCosmetic(Category cat, Cosmetic<?> cosmetic) {
        setEnabledCosmetic(cat, cosmetic == null ? null : cosmetic.getType());
    }

    public void setEnabledCosmetic(Category cat, CosmeticType<?> type) {
        if (cat == Category.SUITS) {
            if (type == null) {
                throw new IllegalArgumentException("Updating Suit state requires a slot parameter");
            }
            SuitType suit = (SuitType) type;
            setEnabledSuitPart(suit.getSlot(), suit);
            return;
        }
        enabled.put(cat, type);
    }

    public void setEnabledSuitPart(ArmorSlot slot, SuitType suitType) {
        this.enabledSuitParts.put(slot, suitType);
    }

    public int getAmmo(GadgetType gadget) {
        return ammo.getOrDefault(gadget, 0);
    }

    public void setAmmo(GadgetType type, int amount) {
        ammo.put(type, amount);
    }

    public void addAmmo(GadgetType type, int amount) {
        setAmmo(type, getAmmo(type) + amount);
    }

    public String getPetName(PetType pet) {
        return petNames.get(pet);
    }

    public void setPetName(PetType pet, String name) {
        petNames.put(pet, name);
    }

    public int getKeys() {
        return keys;
    }

    public void setKeys(int amount) {
        keys = amount;
    }

    public void addKeys(int amount) {
        setKeys(getKeys() + amount);
    }

    public void setGadgetsEnabled(boolean enabled) {
        gadgetsEnabled = enabled;
    }

    public boolean hasGadgetsEnabled() {
        return gadgetsEnabled;
    }

    public void setSeeSelfMorph(boolean enabled) {
        morphSelfView = enabled;
    }

    public boolean canSeeSelfMorph() {
        return morphSelfView;
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
}
