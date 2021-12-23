package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.treasurechests.TreasureChest;
import be.isach.ultracosmetics.treasurechests.TreasureChestDesign;
import be.isach.ultracosmetics.util.Cuboid;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by Sacha on 11/11/15.
 */
public class TreasureChestManager implements Listener {

    private static Random random = new Random();
    private UltraCosmetics ultraCosmetics;

    public TreasureChestManager(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
    }

    private static void openTreasureChest(Player player) {
        openTreasureChest(player, null);
    }

    private static void openTreasureChest(Player player, Location preLoc) {
        String designPath = getRandomDesign();
        player.closeInventory();
        new TreasureChest(player.getUniqueId(), new TreasureChestDesign(designPath), preLoc);
    }

    private static String getRandomDesign() {
        Set<String> set = UltraCosmeticsData.get().getPlugin().getConfig().getConfigurationSection("TreasureChests.Designs").getKeys(false);
        List<String> list = new ArrayList<>();
        list.addAll(set);
        return list.get(random.nextInt(set.size()));
    }


    public static void tryOpenChest(Player player) {
        tryOpenChest(player, null);
    }

    public static void tryOpenChest(Player player, Location preLoc) {
        UltraCosmetics plugin = UltraCosmeticsData.get().getPlugin();

        if (!plugin.areChestsAllowedInRegion(player)) {
            player.closeInventory();
            player.sendMessage(MessageManager.getMessage("Chest-Region-Disabled"));
            return;
        }

        if (plugin.getPlayerManager().getUltraPlayer(player).getKeys() < 1) {
            player.closeInventory();
            plugin.getPlayerManager().getUltraPlayer(player).openKeyPurchaseMenu();
            return;
        }

        Cuboid c = new Cuboid(player.getLocation().add(-2, 0, -2), player.getLocation().add(2, 1, 2));

        if (!c.isEmptyExcept(player.getLocation().getBlock().getLocation())) {
            player.sendMessage(MessageManager.getMessage("Chest-Not-Enough-Space"));

            if(preLoc != null) {
                player.teleport(preLoc);
            }
            return;
        }

        for (Entity ent : player.getNearbyEntities(5, 5, 5)) {
            if (!(ent instanceof Player)) continue;
            Player loopPlayer = (Player) ent;
            // check Bukkit.getPlayer(UUID) in case loopPlayer is really a player NPC
            if (Bukkit.getPlayer(loopPlayer.getUniqueId()) != null && plugin.getPlayerManager().getUltraPlayer(loopPlayer).getCurrentTreasureChest() != null) {
                player.closeInventory();
                player.sendMessage(MessageManager.getMessage("Too-Close-To-Other-Chest"));
                return;
            }
        }

        if (player.getLocation().getBlock().getRelative(BlockFace.UP).getType() != Material.AIR
                || !player.getLocation().getBlock().getType().isBlock()
                || player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            player.sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-On-Ground"));
            return;
        }
        plugin.getPlayerManager().getUltraPlayer(player).removeKey();
        openTreasureChest(player, preLoc);
    }

    @EventHandler
    public void buyKeyConfirm(InventoryClickEvent event) {
        if (!event.getView().getTitle().equalsIgnoreCase(MessageManager.getMessage("Buy-Treasure-Key"))) return;
        event.setCancelled(true);
        if (event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName()) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Purchase"))) {
                if (UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer((Player) event.getWhoClicked()).getBalance() >= (int) SettingsManager.getConfig().get("TreasureChests.Key-Price")) {
                    ultraCosmetics.getEconomyHandler().withdraw((Player) event.getWhoClicked(), (int) SettingsManager.getConfig().get("TreasureChests.Key-Price"));
                    UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer((Player) event.getWhoClicked()).addKey();
                    event.getWhoClicked().sendMessage(MessageManager.getMessage("Successful-Purchase"));
                    event.getWhoClicked().closeInventory();
                    UltraCosmeticsData.get().getPlugin().getMenus().getMainMenu().open(UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer((Player) event.getWhoClicked()));
                } else {
                    event.getWhoClicked().sendMessage(MessageManager.getMessage("Not-Enough-Money"));
                    event.getWhoClicked().closeInventory();
                }
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Cancel"))) {
                event.getWhoClicked().closeInventory();
                UltraCosmeticsData.get().getPlugin().getMenus().getMainMenu().open(UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer((Player) event.getWhoClicked()));
            }
        }
    }

}