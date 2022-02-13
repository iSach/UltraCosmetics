package be.isach.ultracosmetics.listeners;

import be.isach.ultracosmetics.UltraCosmeticsData;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
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
        processPickup(event.getItem(), event);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        processPickup(event.getItem(), event);
    }

    public void processPickup(Item item, Cancellable event) {
        ItemStack stack = item.getItemStack();
        // TODO: just switch to UNPICKABLEUP meta entirely and remove getItemNoPickupString?
        if (item.hasMetadata("UNPICKABLEUP")
                || (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()
                && stack.getItemMeta().getDisplayName().equals(UltraCosmeticsData.get().getItemNoPickupString()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.hasMetadata("Pet")) {
                entity.remove();
            }
        }
    }
}
