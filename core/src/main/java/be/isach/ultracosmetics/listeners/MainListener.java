package be.isach.ultracosmetics.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

/**
 * Created by Sacha on 25/12/15.
 */
public class MainListener implements Listener {

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked().hasMetadata("NO_INTER"))
            event.setCancelled(true);
    }

}
