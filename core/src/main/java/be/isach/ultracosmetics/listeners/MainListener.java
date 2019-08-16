package be.isach.ultracosmetics.listeners;

import be.isach.ultracosmetics.UltraCosmeticsData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Main listener
 *
 * @author iSach
 * @since 12-25-2015
 */
public class MainListener implements Listener {

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().hasMetadata("NO_INTER"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onHopperPickup(InventoryPickupItemEvent event) {
        ItemStack stack = event.getItem().getItemStack();
        if (event.getItem().hasMetadata("UNPICKABLEUP")
                || (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()
                && stack.getItemMeta().getDisplayName().equals(UltraCosmeticsData.get().getItemNoPickupString()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        ItemStack stack = event.getItem().getItemStack();
        if (event.getItem().hasMetadata("UNPICKABLEUP")
                || (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()
                && stack.getItemMeta().getDisplayName().equals(UltraCosmeticsData.get().getItemNoPickupString()))) {
            event.setCancelled(true);
        }
    }
}
