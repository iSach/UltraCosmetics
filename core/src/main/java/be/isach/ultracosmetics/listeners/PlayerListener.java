package be.isach.ultracosmetics.listeners;

import be.isach.ultracosmetics.UltraPlayer;
import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.run.FallDamageManager;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by sacha on 03/08/15.
 */
public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                UltraCosmetics.getPlayerManager().create(event.getPlayer());

                if ((boolean) SettingsManager.getConfig().get("Menu-Item.Give-On-Join") && event.getPlayer().hasPermission("ultracosmetics.receivechest") && ((List<String>) SettingsManager.getConfig().get("Enabled-Worlds")).contains(event.getPlayer().getWorld().getName())) {
                    Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            UltraPlayer cp = UltraCosmetics.getPlayerManager().getCustomPlayer(event.getPlayer());
                            if (cp != null && event.getPlayer() != null)
                                cp.giveMenuItem();
                        }
                    }, 5);
                }
                if (UltraCosmetics.outdated)
                    if (event.getPlayer().isOp())
                        event.getPlayer().sendMessage("§l§oUltraCosmetics > §c§lAn update is available: " + UltraCosmetics.lastVersion);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(final PlayerChangedWorldEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                if ((boolean) SettingsManager.getConfig().get("Menu-Item.Give-On-Join") && event.getPlayer().hasPermission("ultracosmetics.receivechest") && ((List<String>) SettingsManager.getConfig().get("Enabled-Worlds")).contains(event.getPlayer().getWorld().getName())) {
                    Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            UltraCosmetics.getCustomPlayer(event.getPlayer()).giveMenuItem();
                        }
                    }, 5);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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

    /**
     * Cancel players from removing, picking the item in their inventory.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void cancelMove(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if ((SettingsManager.getConfig().getStringList("Enabled-Worlds")).contains(player.getWorld().getName())) {

            if ((event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
                    && (event.getCursor() == null || event.getCursor().getType() == Material.AIR)) {
                event.setCancelled(true);
                player.updateInventory();
                return;
            }

            if (event.getCurrentItem() != null
                    && event.getCurrentItem().hasItemMeta()
                    && event.getCurrentItem().getItemMeta().hasDisplayName()
                    && event.getCurrentItem().getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§"))) {
                event.setCancelled(true);
                player.updateInventory();
                return;
            }
        }
    }

    /**
     * Cancel players from removing, picking the item in their inventory.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void cancelMove(InventoryDragEvent event) {
        for (ItemStack item : event.getNewItems().values()) {
            if (item != null
                    && item.hasItemMeta()
                    && item.getItemMeta().hasDisplayName()
                    && item.getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§"))) {
                event.setCancelled(true);
                ((Player) event.getWhoClicked()).updateInventory();
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        if ((boolean) SettingsManager.getConfig().get("Menu-Item.Give-On-Respawn") && ((List<String>) SettingsManager.getConfig().get("Enabled-Worlds")).contains(event.getPlayer().getWorld().getName())) {
            int slot = SettingsManager.getConfig().getInt("Menu-Item.Slot");
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        if (UltraCosmetics.getCustomPlayer(event.getPlayer()).currentTreasureChest != null)
            UltraCosmetics.getCustomPlayer(event.getPlayer()).currentTreasureChest.forceOpen(0);
        UltraCosmetics.getCustomPlayer(event.getPlayer()).clear();
        UltraCosmetics.getPlayerManager().getCustomPlayer(event.getPlayer()).removeMenuItem();
        UltraCosmetics.getPlayerManager().remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        int slot = SettingsManager.getConfig().getInt("Menu-Item.Slot");
        if (event.getEntity().getInventory().getItem(slot) != null
                && event.getEntity().getInventory().getItem(slot).hasItemMeta()
                && event.getEntity().getInventory().getItem(slot).getItemMeta().hasDisplayName()
                && event.getEntity().getInventory().getItem(slot).getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§"))) {
            event.getDrops().remove(event.getEntity().getInventory().getItem(slot));
            event.getEntity().getInventory().setItem(slot, null);
        }
        if (UltraCosmetics.getCustomPlayer(event.getEntity()).currentGadget != null)
            event.getDrops().remove(event.getEntity().getInventory().getItem((Integer) SettingsManager.getConfig().get("Gadget-Slot")));
        if (UltraCosmetics.getCustomPlayer(event.getEntity()).currentHat != null)
            event.getDrops().remove(UltraCosmetics.getCustomPlayer(event.getEntity()).currentHat.getItemStack());
        if (UltraCosmetics.getCustomPlayer(event.getEntity()).currentHelmet != null)
            event.getDrops().remove(UltraCosmetics.getCustomPlayer(event.getEntity()).currentHelmet.getItemStack());
        if (UltraCosmetics.getCustomPlayer(event.getEntity()).currentChestplate != null)
            event.getDrops().remove(UltraCosmetics.getCustomPlayer(event.getEntity()).currentChestplate.getItemStack());
        if (UltraCosmetics.getCustomPlayer(event.getEntity()).currentLeggings != null)
            event.getDrops().remove(UltraCosmetics.getCustomPlayer(event.getEntity()).currentLeggings.getItemStack());
        if (UltraCosmetics.getCustomPlayer(event.getEntity()).currentBoots != null)
            event.getDrops().remove(UltraCosmetics.getCustomPlayer(event.getEntity()).currentBoots.getItemStack());
        if (UltraCosmetics.getCustomPlayer(event.getEntity()).currentEmote != null)
            event.getDrops().remove(UltraCosmetics.getCustomPlayer(event.getEntity()).currentEmote.getItemStack());
        UltraCosmetics.getCustomPlayer(event.getEntity()).clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL
                && FallDamageManager.shouldBeProtected(event.getEntity()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickUpItem(PlayerPickupItemEvent event) {
        if (event.getItem().getItemStack() != null
                && event.getItem().getItemStack().hasItemMeta()
                && event.getItem().getItemStack().getItemMeta().hasDisplayName()
                && event.getItem().getItemStack().getItemMeta().getDisplayName().equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§"))) {
            event.setCancelled(true);
            event.getItem().remove();
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractGhost(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() != null
                && event.getRightClicked().hasMetadata("C_AD_ArmorStand"))
            event.setCancelled(true);
    }

}
