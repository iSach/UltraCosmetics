package be.isach.ultracosmetics.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 * Main listener
 *
 * @author iSach
 * @since 12-25-2015
 */
public class MainListener implements Listener {

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().hasMetadata("NO_INTER")) event.setCancelled(true);
    }

    @EventHandler
    public void onHopperPickup(InventoryPickupItemEvent event) {
        processPickup(event.getItem(), event);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerPickup(org.bukkit.event.player.PlayerPickupItemEvent event) {
        processPickup(event.getItem(), event);
    }

    public void processPickup(Item item, Cancellable event) {
        if (item.hasMetadata("UNPICKABLEUP")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMerge(ItemMergeEvent event) {
        if (event.getEntity().hasMetadata("UNPICKABLEUP") || event.getTarget().hasMetadata("UNPICKABLEUP")) {
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

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Firework)) return;
        FireworkMeta fm = ((Firework) event.getDamager()).getFireworkMeta();
        if (fm.getDisplayName().equals("uc_firework")) {
            event.setCancelled(true);
        }
    }
}
