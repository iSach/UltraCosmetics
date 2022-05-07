package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.player.UltraPlayer;

import org.bukkit.inventory.ItemStack;

public abstract class ArmorCosmetic<T extends CosmeticType<?>> extends Cosmetic<T> {
    protected boolean success = false;
    public ArmorCosmetic(UltraCosmetics ultraCosmetics, Category category, UltraPlayer owner, T type) {
        super(ultraCosmetics, category, owner, type);
    }

    @Override
    public void clear() {
        super.clear();
        setArmorItem(null);
    }

    @Override
    protected boolean tryEquip() {
        return trySetSlot();
    }

    protected boolean trySetSlot() {
        // Remove current equipped armor piece
        getOwner().removeSuit(getArmorSlot());

        if (getArmorSlot() == ArmorSlot.HELMET) {
            getOwner().removeCosmetic(Category.HATS);
            getOwner().removeCosmetic(Category.EMOTES);
        }

        // If the user's armor slot is still occupied after we've removed all related cosmetics,
        // give up and ask the user to free up the slot.
        if (getArmorItem() != null) {
            getOwner().sendMessage(MessageManager.getMessage(getOccupiedSlotKey()));
            return false;
        }
        setArmorItem(getType().getItemStack());
        success = true;
        return true;
    }

    protected ItemStack getArmorItem() {
        switch (getArmorSlot()) {
        case BOOTS:
            return getPlayer().getInventory().getBoots();
        case LEGGINGS:
            return getPlayer().getInventory().getLeggings();
        case CHESTPLATE:
            return getPlayer().getInventory().getChestplate();
        case HELMET:
            return getPlayer().getInventory().getHelmet();
        default:
            return null;
        }
    }

    protected void setArmorItem(ItemStack item) {
        switch (getArmorSlot()) {
        case BOOTS:
            getPlayer().getInventory().setBoots(item);
            break;
        case LEGGINGS:
            getPlayer().getInventory().setLeggings(item);
            break;
        case CHESTPLATE:
            getPlayer().getInventory().setChestplate(item);
            break;
        case HELMET:
            getPlayer().getInventory().setHelmet(item);
            break;
        }
    }

    protected abstract ArmorSlot getArmorSlot();
    protected abstract String getOccupiedSlotKey();
}
