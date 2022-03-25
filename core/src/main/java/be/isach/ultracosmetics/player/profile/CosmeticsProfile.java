package be.isach.ultracosmetics.player.profile;

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
import be.isach.ultracosmetics.player.UltraPlayer;

public abstract class CosmeticsProfile {
    protected final UltraPlayer ultraPlayer;
    protected final UUID uuid;
    protected final UltraCosmetics ultraCosmetics;
    protected final PlayerData data;
    public CosmeticsProfile(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
        this.ultraPlayer = ultraPlayer;
        this.uuid = ultraPlayer.getUUID();
        this.ultraCosmetics = ultraCosmetics;
        this.data = new PlayerData(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(ultraCosmetics, () -> {
            load();
            if (!UltraCosmeticsData.get().areCosmeticsProfilesEnabled()) return;
            Bukkit.getScheduler().runTask(ultraCosmetics, () -> equip());
        });
    }

    protected abstract void load();
    public abstract void save();

    public void equip() {
        if (!ultraPlayer.isOnline()) return;
        if (!SettingsManager.isAllowedWorld(ultraPlayer.getBukkitPlayer().getWorld())) return;
        for (Entry<Category,CosmeticType<?>> type : data.getEnabledCosmetics().entrySet()) {
            if (type.getValue() == null || !type.getKey().isEnabled() || !type.getValue().isEnabled()) continue;
            type.getValue().equip(ultraPlayer, ultraCosmetics);
        }

        if (Category.SUITS.isEnabled()) {
            for (SuitType type : data.getEnabledSuitParts().values()) {
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
        data.getEnabledCosmetics().put(cat, type);
    }

    public void setEnabledSuitPart(ArmorSlot slot, SuitType suitType) {
        data.getEnabledSuitParts().put(slot, suitType);
    }

    public int getAmmo(GadgetType gadget) {
        return data.getAmmo().getOrDefault(gadget, 0);
    }

    public void setAmmo(GadgetType type, int amount) {
        data.getAmmo().put(type, amount);
    }

    public void addAmmo(GadgetType type, int amount) {
        setAmmo(type, getAmmo(type) + amount);
    }

    public String getPetName(PetType pet) {
        return data.getPetNames().get(pet);
    }

    public void setPetName(PetType pet, String name) {
        data.getPetNames().put(pet, name);
    }

    public int getKeys() {
        return data.getKeys();
    }

    public void setKeys(int amount) {
        data.setKeys(amount);
    }

    public void addKeys(int amount) {
        setKeys(getKeys() + amount);
    }

    public void setGadgetsEnabled(boolean enabled) {
        data.setGadgetsEnabled(enabled);
    }

    public boolean hasGadgetsEnabled() {
        return data.isGadgetsEnabled();
    }

    public void setSeeSelfMorph(boolean enabled) {
        data.setMorphSelfView(enabled);
    }

    public boolean canSeeSelfMorph() {
        return data.isMorphSelfView();
    }

    public boolean isTreasureNotifications() {
        return data.isTreasureNotifications();
    }

    public void setTreasureNotifications(boolean treasureNotifications) {
        data.setTreasureNotifications(treasureNotifications);
    }

    public boolean isFilterByOwned() {
        return data.isFilterByOwned();
    }

    public void setFilterByOwned(boolean filterByOwned) {
        data.setFilterByOwned(filterByOwned);
    }
}
