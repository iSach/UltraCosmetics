package be.isach.ultracosmetics.listeners.v1_9;

import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.gadgets.Gadget;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 1.9 offhand listeners.
 * 
 * @author 	iSach
 * @since 	05-16-2016
 */
public class PlayerSwapItemListener implements Listener {

    private UltraCosmetics ultraCosmetics;

    public PlayerSwapItemListener(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
    }

    @EventHandler
    public void cancelOffHandMove(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        UltraPlayer ultraPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(player);
        if(ultraPlayer.getCurrentGadget() != null) {
            Gadget gadget = ultraPlayer.getCurrentGadget();
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
                .equals(ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname"))))) {
            event.setCancelled(true);
        }
        if (event.getOffHandItem() != null
                && event.getOffHandItem().hasItemMeta()
                && event.getOffHandItem().getItemMeta().hasDisplayName()
                && event.getOffHandItem().getItemMeta().getDisplayName()
                .equals(ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname"))))) {
            event.setCancelled(true);
        }
    }
}
