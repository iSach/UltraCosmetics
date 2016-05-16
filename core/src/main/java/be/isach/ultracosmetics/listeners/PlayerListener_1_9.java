package be.isach.ultracosmetics.listeners;

import be.isach.ultracosmetics.config.SettingsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/**
 * Created by Matthew on 2016/5/16.
 */
public class PlayerListener_1_9 implements Listener{

    @EventHandler
    public void onPlayerSwapoffHand(PlayerSwapHandItemsEvent event) {
        if (event.getMainHandItem() != null
                && event.getMainHandItem().hasItemMeta()
                && event.getMainHandItem().getItemMeta().hasDisplayName()
                && event.getMainHandItem().getItemMeta().getDisplayName()
                .equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§"))) {
            event.setCancelled(true);
        }
        if (event.getOffHandItem() != null
                && event.getOffHandItem().hasItemMeta()
                && event.getOffHandItem().getItemMeta().hasDisplayName()
                && event.getOffHandItem().getItemMeta().getDisplayName()
                .equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§"))) {
            event.setCancelled(true);
        }
    }
}
