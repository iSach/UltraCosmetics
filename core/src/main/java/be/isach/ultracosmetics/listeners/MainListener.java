package be.isach.ultracosmetics.listeners;

import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Main listener
 * 
 * @author 	iSach
 * @since 	12-25-2015
 */
public class MainListener implements Listener {

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked().hasMetadata("NO_INTER"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onTakeUpMelon(PlayerPickupItemEvent event) {
        if (event.getItem().hasMetadata("UNPICKABLEUP")) {
            event.setCancelled(true);
        }
    }
}
