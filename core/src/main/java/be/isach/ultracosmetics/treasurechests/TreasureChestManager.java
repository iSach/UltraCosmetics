package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Area;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by Sacha on 11/11/15.
 */
public class TreasureChestManager implements Listener {

    private static Random random = new Random();
    private static final List<TreasureLocation> TREASURE_LOCATIONS = new ArrayList<>();

    static {
        Set<String> locationNames = SettingsManager.getConfig().getConfigurationSection("TreasureChests.Locations").getKeys(false);
        for (String locationName : locationNames) {
            if (!SettingsManager.getConfig().isConfigurationSection("TreasureChests.Locations." + locationName)) continue;
            ConfigurationSection location = SettingsManager.getConfig().getConfigurationSection("TreasureChests.Locations." + locationName);
            String worldName = location.getString("World", "none");
            World world = null;
            if (!worldName.equals("none")) {
                world = Bukkit.getWorld(worldName);
                if (world == null) {
                    UltraCosmeticsData.get().getPlugin().getSmartLogger().write(LogLevel.ERROR, "Invalid world set for location " + locationName + ", using player world");
                }
            }
            TreasureLocation tloc = new TreasureLocation(world, location.getInt("X", 0), location.getInt("Y", 63), location.getInt("Z", 0));
            TREASURE_LOCATIONS.add(tloc);
        }
        if (TREASURE_LOCATIONS.size() == 0 && SettingsManager.getConfig().getBoolean("TreasureChests.Locations.Enabled")) {
            UltraCosmeticsData.get().getPlugin().getSmartLogger().write(LogLevel.WARNING, "No treasure chest locations are defined, the setting will be ignored");
        }
    }

    private TreasureChestManager() {}

    private static String getRandomDesign() {
        Set<String> set = UltraCosmeticsData.get().getPlugin().getConfig().getConfigurationSection("TreasureChests.Designs").getKeys(false);
        List<String> list = new ArrayList<>();
        list.addAll(set);
        return list.get(random.nextInt(set.size()));
    }

    public static void tryOpenChest(Player player) {
        if (!SettingsManager.getConfig().getBoolean("TreasureChests.Locations.Enabled") || TREASURE_LOCATIONS.size() == 0) {
            tryOpenChest(player, null);
            return;
        }
        List<TreasureLocation> locations = new ArrayList<>(TREASURE_LOCATIONS);
        for (UltraPlayer up : UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayers()) {
            if (up.getCurrentTreasureChest() != null) {
                locations.remove(up.getCurrentTreasureChest().getTreasureLocation());
            }
        }
        if (locations.size() == 0) {
            player.sendMessage(MessageManager.getMessage("Treasure-Chest-Occupied"));
            return;
        }
        TreasureLocation tloc = locations.get(random.nextInt(locations.size()));
        tryOpenChest(player, tloc);
    }

    public static void tryOpenChest(Player player, TreasureLocation tpTo) {
        UltraCosmetics plugin = UltraCosmeticsData.get().getPlugin();
        UltraPlayer ultraPlayer = plugin.getPlayerManager().getUltraPlayer(player);
        if (ultraPlayer.getKeys() < 1) {
            player.closeInventory();
            ultraPlayer.openKeyPurchaseMenu();
            return;
        }

        Location targetLoc = tpTo == null ? player.getLocation() : tpTo.toLocation(player);

        Area area = new Area(targetLoc, 2, 1);

        if (!area.isEmptyExcept(targetLoc.getBlock().getLocation())) {
            player.sendMessage(MessageManager.getMessage("Chest-Location.Not-Enough-Space"));
            return;
        }

        for (Entity ent : targetLoc.getWorld().getNearbyEntities(targetLoc, 5, 5, 5)) {
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

        Block block = targetLoc.getBlock();
        if (block.getRelative(BlockFace.UP).getType() != Material.AIR
                || block.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            player.sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-On-Ground"));
            return;
        }

        Location preLoc = null;
        if (tpTo != null) {
            preLoc = player.getLocation();
            tpTo.tpTo(player);
        }

        if (!plugin.areChestsAllowedInRegion(player)) {
            player.closeInventory();
            player.sendMessage(MessageManager.getMessage("Chest-Location.Region-Disabled"));
            if (preLoc != null) {
                player.teleport(preLoc);
            }
            return;
        }

        ultraPlayer.removeKey();
        String designPath = getRandomDesign();
        player.closeInventory();
        new TreasureChest(player.getUniqueId(), new TreasureChestDesign(designPath), preLoc, tpTo);
    }
}