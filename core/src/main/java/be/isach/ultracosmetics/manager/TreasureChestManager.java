package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.treasurechests.TreasureChest;
import be.isach.ultracosmetics.treasurechests.TreasureChestDesign;
import be.isach.ultracosmetics.util.Area;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
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
        if (!SettingsManager.getConfig().getBoolean("TreasureChests.Location.Enabled")) {
            tryOpenChest(player, null);
            return;
        }
        ConfigurationSection location = SettingsManager.getConfig().getConfigurationSection("TreasureChests.Location");
        Location originalLocation = player.getLocation().clone();
        // just modify a copy of the player's original location so we preserve yaw and pitch
        Location treasureChestLocation = player.getLocation().clone();
        String worldName = location.getString("World", "none");
        if (worldName != null && !worldName.equals("none")) {
            World world = Bukkit.getWorld(location.getString("World"));
            if (world == null) {
                player.sendMessage(ChatColor.RED + "Invalid world set in config.yml!");
            } else {
                treasureChestLocation.setWorld(world);
            }
        }
        treasureChestLocation.setX(location.getInt("X", 0) + 0.5);
        // add 0.5 to the Y too so it's less likely the player gets stuck in the ground
        treasureChestLocation.setY(location.getInt("Y", 63) + 0.5);
        treasureChestLocation.setZ(location.getInt("Z", 0) + 0.5);
        player.teleport(treasureChestLocation);
        tryOpenChest(player, originalLocation);
    }

    public static void tryOpenChest(Player player, Location preLoc) {
        UltraCosmetics plugin = UltraCosmeticsData.get().getPlugin();

        if (!plugin.areChestsAllowedInRegion(player)) {
            player.closeInventory();
            player.sendMessage(MessageManager.getMessage("Chest-Location.Region-Disabled"));
            return;
        }

        UltraPlayer ultraPlayer = plugin.getPlayerManager().getUltraPlayer(player);
        if (ultraPlayer.getKeys() < 1) {
            player.closeInventory();
            ultraPlayer.openKeyPurchaseMenu();
            return;
        }

        Area area = new Area(player.getLocation().add(-2, 0, -2), player.getLocation().add(2, 1, 2));

        if (!area.isEmptyExcept(player.getLocation().getBlock().getLocation())) {
            player.sendMessage(MessageManager.getMessage("Chest-Location.Not-Enough-Space"));

            if(preLoc != null) {
                player.teleport(preLoc);
            }
            return;
        }

        for (Entity ent : player.getNearbyEntities(5, 5, 5)) {
            if (!(ent instanceof Player)) continue;
            Player loopPlayer = (Player) ent;
            // check Bukkit.getPlayer(UUID) in case loopPlayer is really a player NPC
            if (Bukkit.getPlayer(loopPlayer.getUniqueId()) != null 
                    && plugin.getPlayerManager().getUltraPlayer(loopPlayer).getCurrentTreasureChest() != null) {
                player.closeInventory();
                player.sendMessage(MessageManager.getMessage("Chest-Location.Too-Close"));
                return;
            }
        }

        Block block = player.getLocation().getBlock();
        if (block.getRelative(BlockFace.UP).getType() != Material.AIR
                || block.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            player.sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-On-Ground"));
            return;
        }
        ultraPlayer.removeKey();
        openTreasureChest(player, preLoc);
    }

    @EventHandler
    public void buyKeyConfirm(InventoryClickEvent event) {
        if (!event.getView().getTitle().equalsIgnoreCase(MessageManager.getMessage("Buy-Treasure-Key"))) return;
        event.setCancelled(true);

        if (event.getCurrentItem() == null || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;
        Player player = (Player) event.getWhoClicked();
        UltraPlayer ultraPlayer = UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(player);

        if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Cancel"))) {
            player.closeInventory();
            UltraCosmeticsData.get().getPlugin().getMenus().getMainMenu().open(ultraPlayer);
            return;
        }

        if (!event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Purchase"))) return;

        if (ultraPlayer.getBalance() < SettingsManager.getConfig().getInt("TreasureChests.Key-Price")) {
            player.sendMessage(MessageManager.getMessage("Not-Enough-Money"));
            player.closeInventory();
            return;
        }

        ultraCosmetics.getEconomyHandler().withdraw(player, SettingsManager.getConfig().getInt("TreasureChests.Key-Price"));
        ultraPlayer.addKey();
        player.sendMessage(MessageManager.getMessage("Successful-Purchase"));
        player.closeInventory();
        UltraCosmeticsData.get().getPlugin().getMenus().getMainMenu().open(ultraPlayer);
    }

}