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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by sacha on 03/08/15.
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Core.customPlayers.add(new CustomPlayer(event.getPlayer().getUniqueId()));
        if ((boolean) SettingsManager.getConfig().get("Menu-Item.Give-On-Join")) {
            Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
                    if (event.getPlayer().getInventory().getItem(slot) != null) {
                        event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), event.getPlayer().getInventory().getItem(slot));
                        event.getPlayer().getInventory().remove(slot);
                    }
                    String name = String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replaceAll("&", "§");
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
                && event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replaceAll("&", "§"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickUp(InventoryClickEvent event) {
        if (event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName()
                && event.getCurrentItem().getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replaceAll("&", "§"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Core.getCustomPlayer(event.getPlayer()).removeGadget();
        Core.getCustomPlayer(event.getPlayer()).removeMount();
        Core.getCustomPlayer(event.getPlayer()).removeParticleEffect();
        Core.getCustomPlayer(event.getPlayer()).removePet();
        Core.customPlayers.remove(Core.getCustomPlayer(event.getPlayer()));
        int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
        if (event.getPlayer().getInventory().getItem(slot) != null
                && event.getPlayer().getInventory().getItem(slot).hasItemMeta()
                && event.getPlayer().getInventory().getItem(slot).getItemMeta().hasDisplayName()
                && event.getPlayer().getInventory().getItem(slot).getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replaceAll("&", "§"))) {
            event.getPlayer().getInventory().setItem(slot, null);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Core.getCustomPlayer(event.getEntity()).removeGadget();
        Core.getCustomPlayer(event.getEntity()).removeMount();
        Core.getCustomPlayer(event.getEntity()).removeParticleEffect();
        Core.getCustomPlayer(event.getEntity()).removePet();
        Core.customPlayers.remove(Core.getCustomPlayer(event.getEntity()));
        int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
        if (event.getEntity().getInventory().getItem(slot) != null
                && event.getEntity().getInventory().getItem(slot).hasItemMeta()
                && event.getEntity().getInventory().getItem(slot).getItemMeta().hasDisplayName()
                && event.getEntity().getInventory().getItem(slot).getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replaceAll("&", "§"))) {
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
