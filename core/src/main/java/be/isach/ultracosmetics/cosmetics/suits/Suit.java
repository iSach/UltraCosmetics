package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.ArmorCosmetic;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an instance of a suit summoned by a player.
 *
 * @author iSach
 * @since 12-20-2015
 */
public abstract class Suit extends ArmorCosmetic<SuitType> implements Updatable {
    /**
     * ItemStack of the Suit.
     */
    protected ItemStack itemStack;

    public Suit(UltraPlayer ultraPlayer, SuitType suitType, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.SUITS, ultraPlayer, suitType);
        setupItemStack();
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (getOwner() == null || getPlayer() == null) {
            return;
        } 
        if (event.getPlayer() == getPlayer() && isItemThis(event.getItemDrop().getItemStack())) {
            event.getItemDrop().remove();
            if (SettingsManager.getConfig().getBoolean("Remove-Gadget-With-Drop")) {
                clear();
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        handleClick(event);
    }

    // InventoryCreativeEvent is a subclass of InventoryClickEvent,
    // so do we really need both listeners?
    @EventHandler
    public void onInventoryClick(InventoryCreativeEvent event) {
        handleClick(event);
    }

    private void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        if (event.getSlotType().equals(SlotType.ARMOR) && player == getPlayer() && isItemThis(current)) {
            event.setCancelled(true);
            if (event instanceof InventoryCreativeEvent) {
                // Close the inventory because clicking again results in the event being handled client side
                player.closeInventory();
            } else {
                player.updateInventory();
            }
        }
    }

    private boolean isItemThis(ItemStack is) {
        return is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && is.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName());
    }

    @Override
    protected void onEquip() {
        runTaskTimerAsynchronously(getUltraCosmetics(), 0, 1);
    }

    protected void setupItemStack() {
        itemStack = ItemFactory.create(getType().getMaterial(), getType().getName(), "", MessageManager.getMessage("Suits.Suit-Part-Lore"));
    }

    @Override
    public void run() {
        if (getOwner() == null || getPlayer() == null) {
            cancel();
            return;
        }
        onUpdate();
    }

    @Override
    protected void unsetCosmetic() {
        getOwner().setCurrentSuitPart(getArmorSlot(), null);
    }

    @Override
    protected void unequipLikeCosmetics() {
        getOwner().removeSuit(getArmorSlot());
    }
    
    @Override
    public void onClear() {
    }

    /**
     * The Suit ItemStack.
     *
     * @return The Suit ItemStack.
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Get Suit Armor Slot.
     *
     * @return Suit Armor Slot.
     */
    @Override
    public ArmorSlot getArmorSlot() {
        return cosmeticType.getSlot();
    }

    @Override
    public String getOccupiedSlotKey() {
        return "Suits.Must-Remove." + getArmorSlot().toString();
    }

    @Override
    public void onUpdate() {
    }
}