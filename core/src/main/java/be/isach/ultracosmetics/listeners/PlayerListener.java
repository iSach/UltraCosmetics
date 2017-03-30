package be.isach.ultracosmetics.listeners;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.run.FallDamageManager;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Player listeners.
 * 
 * @author 	iSach
 * @since 	08-03-2015
 */
public class PlayerListener implements Listener {

    private UltraCosmetics ultraCosmetics;

    public PlayerListener(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(ultraCosmetics, () -> {
            ultraCosmetics.getPlayerManager().create(event.getPlayer());

            if ((boolean) SettingsManager.getConfig().get("Menu-Item.Give-On-Join") && event.getPlayer().hasPermission("ultracosmetics.receivechest") && ((List<String>) SettingsManager.getConfig().get("Enabled-Worlds")).contains(event.getPlayer().getWorld().getName())) {
                Bukkit.getScheduler().runTaskLater(ultraCosmetics, () -> {
                    UltraPlayer cp = ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer());
                    if (cp != null && event.getPlayer() != null)
                        cp.giveMenuItem();
                }, 5);
            }
            if (ultraCosmetics.getUpdateChecker() != null && ultraCosmetics.getUpdateChecker().isOutdated())
                if (event.getPlayer().isOp())
                    event.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.ITALIC + "UltraCosmetics > " + ChatColor.RED + "" + ChatColor.BOLD + "An update is available: " + ultraCosmetics.getUpdateChecker().getLastVersion());
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(final PlayerChangedWorldEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(ultraCosmetics, () -> {
            if ((boolean) SettingsManager.getConfig().get("Menu-Item.Give-On-Join") && event.getPlayer().hasPermission("ultracosmetics.receivechest") && ((List<String>) SettingsManager.getConfig().get("Enabled-Worlds")).contains(event.getPlayer().getWorld().getName())) {
                Bukkit.getScheduler().runTaskLater(ultraCosmetics, () -> ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer()).giveMenuItem(), 5);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().hasItemMeta()
                && event.getItemDrop().getItemStack().getItemMeta().hasDisplayName()
                && event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname"))))) {
            event.setCancelled(true);
            event.getItemDrop().remove();
            ItemStack chest = event.getPlayer().getItemInHand().clone();
            chest.setAmount(1);
            event.getPlayer().setItemInHand(chest);
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        UltraPlayer ultraPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer());
        if (ultraPlayer.getCurrentTreasureChest() != null) {
            event.setCancelled(true);
            return;
        }
        if (event.getItem() != null
                && event.getItem().hasItemMeta()
                && event.getItem().getItemMeta().hasDisplayName()
                && event.getItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname"))))) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTaskAsynchronously(ultraCosmetics, () -> ultraCosmetics.getMenus().getMainMenu().open(ultraPlayer));
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
                    && event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname"))))) {
                event.setCancelled(true);
                player.updateInventory();
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
                    && item.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname"))))) {
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
            String name = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")));
            Material material = Material.valueOf((String) SettingsManager.getConfig().get("Menu-Item.Type"));
            byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Data")));
            event.getPlayer().getInventory().setItem(slot, ItemFactory.create(material, data, name));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        if (ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer()).getCurrentTreasureChest() != null) {
            ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer()).getCurrentTreasureChest().forceOpen(0);
        }
        ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer()).clear();
        ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer()).removeMenuItem();
        ultraCosmetics.getPlayerManager().remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        int slot = SettingsManager.getConfig().getInt("Menu-Item.Slot");
        if (event.getEntity().getInventory().getItem(slot) != null
                && event.getEntity().getInventory().getItem(slot).hasItemMeta()
                && event.getEntity().getInventory().getItem(slot).getItemMeta().hasDisplayName()
                && event.getEntity().getInventory().getItem(slot).getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname"))))) {
            event.getDrops().remove(event.getEntity().getInventory().getItem(slot));
            event.getEntity().getInventory().setItem(slot, null);
        }
        UltraPlayer ultraPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(event.getEntity());
        if (ultraPlayer.getCurrentGadget() != null)
            event.getDrops().remove(event.getEntity().getInventory().getItem((Integer) SettingsManager.getConfig().get("Gadget-Slot")));
        if (ultraPlayer.getCurrentHat() != null)
            event.getDrops().remove(ultraPlayer.getCurrentHat().getItemStack());
        Arrays.asList(ArmorSlot.values()).forEach(armorSlot -> {
            if (ultraPlayer.getSuit(armorSlot) != null) {
                event.getDrops().remove(ultraPlayer.getSuit(armorSlot).getItemStack());
            }
        });
        if (ultraPlayer.getCurrentEmote() != null)
            event.getDrops().remove(ultraPlayer.getCurrentEmote().getItemStack());
        ultraPlayer.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL
                && FallDamageManager.shouldBeProtected(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickUpItem(PlayerPickupItemEvent event) {
        if (event.getItem().getItemStack() != null
                && event.getItem().getItemStack().hasItemMeta()
                && event.getItem().getItemStack().getItemMeta().hasDisplayName()
                && event.getItem().getItemStack().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname"))))) {
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

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (SettingsManager.getConfig().getList("Disabled-Commands").contains(event.getMessage().split(" ")[0].replace("/", "").toLowerCase())){
            UltraPlayer player = ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer());
            if (player.getCurrentEmote() != null || player.getCurrentHat() != null || player.hasSuitOn()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(MessageManager.getMessage("Disabled-Command-Wearing-Message"));
            } else if (player.getCurrentGadget() != null && player.getCurrentGadget().getItemStack().equals(event.getPlayer().getItemInHand())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(MessageManager.getMessage("Disabled-Command-Holding-Message"));
            }
        }
    }
}
