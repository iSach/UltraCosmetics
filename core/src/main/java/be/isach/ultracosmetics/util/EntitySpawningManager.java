package be.isach.ultracosmetics.util;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Created by Sacha on 23/12/15.
 */
public class EntitySpawningManager implements Listener {

    private static boolean bypass = false;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (bypass) {
            event.setCancelled(false);
        }
    }

    public static void setBypass(boolean newbypass) {
        bypass = newbypass;
    }

}
