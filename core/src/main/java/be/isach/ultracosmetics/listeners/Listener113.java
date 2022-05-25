package be.isach.ultracosmetics.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

/**
 * Listens for any events that only exist in 1.13 and up
 */
public class Listener113 implements Listener {

    @EventHandler
    public void onEntityPickup(EntityPickupItemEvent event) {
        if (event.getEntity().hasMetadata("Pet")) {
            event.setCancelled(true);
        }
    }
}
