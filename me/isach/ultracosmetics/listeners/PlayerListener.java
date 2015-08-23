package me.isach.ultracosmetics.listeners;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.CustomPlayer;
import me.isach.ultracosmetics.config.SettingsManager;
import me.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by sacha on 03/08/15.
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Core.getCustomPlayers().add(new CustomPlayer(event.getPlayer().getUniqueId()));
        if ((boolean) SettingsManager.getConfig().get("Menu-Item.Give-On-Join") && !((List<String>) SettingsManager.getConfig().get("Disabled-Worlds")).contains(event.getPlayer().getWorld().getName())) {
            Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
                    if (event.getPlayer().getInventory().getItem(slot) != null) {
                        if(event.getPlayer().getInventory().getItem(slot).hasItemMeta()
                                && event.getPlayer().getInventory().getItem(slot).getItemMeta().hasDisplayName()
                                && event.getPlayer().getInventory().getItem(slot).getItemMeta().getDisplayName().equalsIgnoreCase((String)SettingsManager.getConfig().get("Menu-Item.Displayname"))) {
                            event.getPlayer().getInventory().remove(slot);
                            event.getPlayer().getInventory().setItem(slot, null);
                        }
                        event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), event.getPlayer().getInventory().getItem(slot));
                        event.getPlayer().getInventory().remove(slot);
                    }
                    String name = String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§");
                    Material material = Material.valueOf((String) SettingsManager.getConfig().get("Menu-Item.Type"));
                    byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Data")));
                    event.getPlayer().getInventory().setItem(slot, ItemFactory.create(material, data, name));
                }
            }, 5);
        }
        if (Core.outdated())
            if (event.getPlayer().isOp())
                event.getPlayer().sendMessage("§l§oUltraCosmetics > §c§lAn update is available: " + Core.getLastVersion());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().hasItemMeta()
                && event.getItemDrop().getItemStack().getItemMeta().hasDisplayName()
                && event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§"))) {
            event.setCancelled(true);
            event.getItemDrop().remove();
            ItemStack chest = event.getPlayer().getItemInHand().clone();
            chest.setAmount(1);
            event.getPlayer().setItemInHand(chest);
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onQuit(PlayerKickEvent event) {

    }

    @EventHandler
    public void onPickUp(InventoryClickEvent event) {
        if (event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName()
                && event.getCurrentItem().getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (!((List<String>) SettingsManager.getConfig().get("Disabled-Worlds")).contains(event.getPlayer().getWorld().getName())) {
            int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
            if (event.getPlayer().getInventory().getItem(slot) != null) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), event.getPlayer().getInventory().getItem(slot));
                event.getPlayer().getInventory().remove(slot);
            }
            String name = String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§");
            Material material = Material.valueOf((String) SettingsManager.getConfig().get("Menu-Item.Type"));
            byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Data")));
            event.getPlayer().getInventory().setItem(slot, ItemFactory.create(material, data, name));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Core.getCustomPlayer(event.getPlayer()).clear();
        Core.getCustomPlayers().remove(Core.getCustomPlayer(event.getPlayer()));
        int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
        if (event.getPlayer().getInventory().getItem(slot) != null
                && event.getPlayer().getInventory().getItem(slot).hasItemMeta()
                && event.getPlayer().getInventory().getItem(slot).getItemMeta().hasDisplayName()
                && event.getPlayer().getInventory().getItem(slot).getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§"))) {
            event.getPlayer().getInventory().setItem(slot, null);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
        if (event.getEntity().getInventory().getItem(slot) != null
                && event.getEntity().getInventory().getItem(slot).hasItemMeta()
                && event.getEntity().getInventory().getItem(slot).getItemMeta().hasDisplayName()
                && event.getEntity().getInventory().getItem(slot).getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§"))) {
            if (Core.getCustomPlayer(event.getEntity()).currentGadget != null)
                event.getDrops().remove(event.getEntity().getInventory().getItem((Integer) SettingsManager.getConfig().get("Gadget-Slot")));
            Core.getCustomPlayer(event.getEntity()).clear();
            event.getDrops().remove(event.getEntity().getInventory().getItem(slot));
            event.getEntity().getInventory().setItem(slot, null);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (Core.noFallDamageEntities.contains(event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

}
