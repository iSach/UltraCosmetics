package be.isach.ultracosmetics.cosmetics.hats;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.ArmorCosmetic;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.HatType;
import be.isach.ultracosmetics.player.UltraPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an instance of a hat summoned by a player.
 *
 * @author iSach
 * @since 08-23-2016
 */
public class Hat extends ArmorCosmetic<HatType> {

    public Hat(UltraCosmetics ultraCosmetics, UltraPlayer owner, HatType type) {
        super(ultraCosmetics, Category.HATS, owner, type);
    }

    @Override
    protected void onEquip() {
        // Setting the item is done in ArmorCosmetic#tryEquip
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)
                && getPlayer() != null
                && player.equals(getPlayer())
                && current != null
                && current.hasItemMeta()
                && current.getItemMeta().hasDisplayName()
                && getType().getItemStack() != null
                && current.getItemMeta().getDisplayName().equals(getType().getItemStack().getItemMeta().getDisplayName())) {
            event.setCancelled(true);
            if (event.getAction().name().contains("DROP")
                    && SettingsManager.getConfig().getBoolean("Remove-Gadget-With-Drop")) {
                clear();
            }
            player.updateInventory();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryCreativeEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)
                && getPlayer() != null
                && player.equals(getPlayer())
                && current != null
                && current.hasItemMeta()
                && current.getItemMeta().hasDisplayName()
                && getType().getItemStack() != null
                && current.getItemMeta().getDisplayName().equals(getType().getItemStack().getItemMeta().getDisplayName())) {
            event.setCancelled(true);
            if (event.getAction().name().contains("DROP")
                    && SettingsManager.getConfig().getBoolean("Remove-Gadget-With-Drop")) {
                clear();
            }
            player.closeInventory(); // Close the inventory because clicking again results in the event being handled client side
        }
    }

    public ItemStack getItemStack() {
        return getType().getItemStack();
    }

    @Override
    protected ArmorSlot getArmorSlot() {
        return ArmorSlot.HELMET;
    }

    @Override
    protected String getOccupiedSlotKey() {
        return "Hats.Must-Remove-Hat";
    }
}
