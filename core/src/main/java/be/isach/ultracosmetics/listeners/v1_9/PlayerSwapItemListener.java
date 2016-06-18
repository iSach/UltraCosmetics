package be.isach.ultracosmetics.listeners.v1_9;

import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Sacha on 16/05/16.
 */
public class PlayerSwapItemListener implements Listener {

    @EventHandler
    public void cancelOffHandMove(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        CustomPlayer customPlayer = UltraCosmetics.getCustomPlayer(player);
        if(customPlayer.currentGadget != null) {
            Gadget gadget = customPlayer.currentGadget;
            ItemStack itemStack = gadget.getItemStack();
            if (event.getMainHandItem() != null) {
                if (event.getMainHandItem().equals(itemStack)) {
                    event.setCancelled(true);
                    event.getPlayer().updateInventory();
                    return;
                }
            }
            if (event.getOffHandItem() != null) {
                if (event.getOffHandItem().equals(itemStack)) {
                    event.setCancelled(true);
                    event.getPlayer().updateInventory();
                    return;
                }
            }
        }
    }



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
