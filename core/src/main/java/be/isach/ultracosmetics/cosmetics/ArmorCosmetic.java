package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.player.UltraPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public abstract class ArmorCosmetic<T extends CosmeticType<?>> extends Cosmetic<T> {
    protected boolean success = false;
    protected ItemStack itemStack;
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
        setArmorItem(itemStack);
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

    /**
     * Returns the ArmorCosmetic's itemstack.
     *
     * @return the ArmorCosmetic's itemstack
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        handleClick(event);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (getOwner() == null || getPlayer() == null) {
            return;
        }
        if (event.getPlayer() == getPlayer() && isItemThis(event.getItemDrop().getItemStack())) {
            event.getItemDrop().remove();
            handleDrop();
        }
    }

    private void handleDrop() {
        if (SettingsManager.getConfig().getBoolean("Remove-Gadget-With-Drop")) {
            clear();
        }
    }

    private void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        // The cursor check here is vital, otherwise the item stays in the helmet slot
        // but gets duplicated elsewhere in the inventory.
        if (player == getPlayer() && (isItemThis(current) || isItemThis(event.getCursor()))) {
            event.setCancelled(true);
            //player.updateInventory();
            if (event.getAction().name().contains("DROP")) {
                handleDrop();
            }
        }
    }

    protected boolean isItemThis(ItemStack is) {
        return is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && is.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName());
    }

    protected abstract ArmorSlot getArmorSlot();
    protected abstract String getOccupiedSlotKey();
}
