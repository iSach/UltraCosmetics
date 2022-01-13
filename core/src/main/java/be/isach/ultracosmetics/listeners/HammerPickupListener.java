package be.isach.ultracosmetics.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import be.isach.ultracosmetics.cosmetics.gadgets.GadgetThorHammer;

public class HammerPickupListener implements Listener {
    private GadgetThorHammer gadget;
    public HammerPickupListener(GadgetThorHammer gadget) {
        this.gadget = gadget;
    }

    @EventHandler
    public void onHammerPickup(EntityPickupItemEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) return;
        if (gadget.getHammerItems().contains(e.getItem())) {
            e.setCancelled(true);
        }
    }
}
