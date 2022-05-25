package be.isach.ultracosmetics.listeners;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.menu.CosmeticsInventoryHolder;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.run.FallDamageManager;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/**
 * Player listeners.
 *
 * @author iSach
 * @since 08-03-2015
 */
public class PlayerListener implements Listener {

    private final UltraCosmetics ultraCosmetics;

    public PlayerListener(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (SettingsManager.getConfig().getBoolean("Menu-Item.Enabled") && event.getPlayer().hasPermission("ultracosmetics.receivechest") && SettingsManager.isAllowedWorld(event.getPlayer().getWorld())) {
                    Bukkit.getScheduler().runTaskLater(ultraCosmetics, () -> {
                        UltraPlayer up = ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer());
                        if (up != null) {
                            up.giveMenuItem();
                        }
                    }, 5);
                }

                if (ultraCosmetics.getUpdateChecker() != null && ultraCosmetics.getUpdateChecker().isOutdated()) {
                    if (event.getPlayer().hasPermission("ultracosmetics.updatenotify")) {
                        event.getPlayer().sendMessage(MessageManager.getMessage("Prefix") + ChatColor.RED.toString() + ChatColor.BOLD + "An update is available: " + ultraCosmetics.getUpdateChecker().getLastVersion());
                        event.getPlayer().sendMessage(MessageManager.getMessage("Prefix") + ChatColor.RED.toString() + ChatColor.BOLD + "Use " + ChatColor.YELLOW + "/uc update" + ChatColor.RED.toString() + ChatColor.BOLD + " to install the update.");
                    } 
                }
            }
        }.runTaskAsynchronously(ultraCosmetics);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(final PlayerChangedWorldEvent event) {
        UltraPlayer ultraPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer());
        if (SettingsManager.isAllowedWorld(event.getPlayer().getWorld())) {
            if (SettingsManager.getConfig().getBoolean("Menu-Item.Enabled") && event.getPlayer().hasPermission("ultracosmetics.receivechest")) {
                ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer()).giveMenuItem();
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    // check is done in the method
                    ultraPlayer.equipProfile();
                }
            }.runTaskLater(ultraCosmetics, 5);
        }
    }

    // run this as early as possible for compatibility with MV-inventories
    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorldChangeEarly(final PlayerChangedWorldEvent event) {
        UltraPlayer ultraPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer());
        if (!SettingsManager.isAllowedWorld(event.getPlayer().getWorld())) {
            // Disable cosmetics when joining a bad world.
            ultraPlayer.removeMenuItem();
            ultraPlayer.setQuitting(true);
            if (ultraPlayer.clear()) {
                ultraPlayer.getBukkitPlayer().sendMessage(MessageManager.getMessage("World-Disabled"));
            }
            ultraPlayer.setQuitting(false);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent event) {
        if (isMenuItem(event.getItemDrop().getItemStack())) {
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
        // apparently can happen if a player disconnected while on a pressure plate
        if (ultraPlayer == null) return;
        // Avoid triggering this when clicking in the inventory
        InventoryType t = event.getPlayer().getOpenInventory().getType();
        if (t != InventoryType.CRAFTING
                && t != InventoryType.CREATIVE) {
            return;
        }
        if (ultraPlayer.getCurrentTreasureChest() != null) {
            event.setCancelled(true);
            return;
        }
        if (isMenuItem(event.getItem())) {
            event.setCancelled(true);
            ultraCosmetics.getMenus().getMainMenu().open(ultraPlayer);
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
        if (!SettingsManager.isAllowedWorld(player.getWorld())) return;
        boolean isMenuItem = isMenuItem(event.getCurrentItem()) || isMenuItem(event.getCursor()) || (event.getClick() == ClickType.NUMBER_KEY && isMenuItem(player.getInventory().getItem(event.getHotbarButton())));
        // TODO: redundant check? see be.isach.ultracosmetics.menu.Menu#onClick
        if (event.getView().getTopInventory().getHolder() instanceof CosmeticsInventoryHolder
                || isMenuItem) {
            event.setCancelled(true);
            player.updateInventory();
            if (isMenuItem && SettingsManager.getConfig().getBoolean("Menu-Item.Open-Menu-On-Inventory-Click", false)) {
                // if it's not delayed by one tick, the client holds the item in cursor slot until they open their inventory again
                Bukkit.getScheduler().runTaskLater(ultraCosmetics, () -> 
                    ultraCosmetics.getMenus().getMainMenu().open(ultraCosmetics.getPlayerManager().getUltraPlayer(player))
                , 1);
            }
        }
    }

    /**
     * Cancel players from removing, picking the item in their inventory.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void cancelMove(InventoryCreativeEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!SettingsManager.isAllowedWorld(player.getWorld())) return;
        if (isMenuItem(event.getCurrentItem()) || isMenuItem(event.getCursor())) {
            event.setCancelled(true);
            player.closeInventory(); // Close the inventory because clicking again results in the event being handled client side
            if (SettingsManager.getConfig().getBoolean("Menu-Item.Open-Menu-On-Inventory-Click", false)) {
                Bukkit.getScheduler().runTaskLater(ultraCosmetics, () -> 
                    ultraCosmetics.getMenus().getMainMenu().open(ultraCosmetics.getPlayerManager().getUltraPlayer(player))
                , 1);
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
            if (isMenuItem(item)) {
                event.setCancelled(true);
                ((Player) event.getWhoClicked()).updateInventory();
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        if (SettingsManager.getConfig().getBoolean("Menu-Item.Enabled") && SettingsManager.isAllowedWorld(event.getPlayer().getWorld())) {
            int slot = SettingsManager.getConfig().getInt("Menu-Item.Slot");
            if (event.getPlayer().getInventory().getItem(slot) != null) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), event.getPlayer().getInventory().getItem(slot));
                event.getPlayer().getInventory().setItem(slot, null);
            }
            String name = ChatColor.translateAlternateColorCodes('&', SettingsManager.getConfig().getString("Menu-Item.Displayname"));
            ItemStack stack = ItemFactory.getItemStackFromConfig("Menu-Item.Type");
            event.getPlayer().getInventory().setItem(slot, ItemFactory.rename(stack, name));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        if (ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer()).getCurrentTreasureChest() != null) {
            ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer()).getCurrentTreasureChest().forceOpen(0);
        }
        UltraPlayer up = ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer());
        up.setQuitting(true);
        up.saveCosmeticsProfile();
        up.clear();
        up.removeMenuItem();
        // workaround plugins calling events after player quit
        Bukkit.getScheduler().runTaskLater(ultraCosmetics, () -> ultraCosmetics.getPlayerManager().remove(event.getPlayer()), 1);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        // Ignore NPC deaths as per #467
        if (Bukkit.getPlayer(event.getEntity().getUniqueId()) == null) return;
        int slot = SettingsManager.getConfig().getInt("Menu-Item.Slot");
        if (isMenuItem(event.getEntity().getInventory().getItem(slot))) {
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
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework && event.getDamager().hasMetadata("UCFirework")) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickUpItem(PlayerPickupItemEvent event) {
        if (isMenuItem(event.getItem().getItemStack())) {
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
        if (event.getPlayer().hasPermission("ultracosmetics.bypass.disabledcommands")) return;
        String strippedCommand = event.getMessage().split(" ")[0].replace("/", "").toLowerCase();
        if (!SettingsManager.getConfig().getList("Disabled-Commands").contains(strippedCommand)) return;
        UltraPlayer player = ultraCosmetics.getPlayerManager().getUltraPlayer(event.getPlayer());
        if (player.hasCosmeticsEquipped()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(MessageManager.getMessage("Disabled-Command-Message"));
        }
    }

    private boolean isMenuItem(ItemStack item) {
        return item != null
                && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname"))));
    }
}
