package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetRocket;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sacha on 03/08/15.
 */
public class BlockUtils {

    /**
     * List of all the blocks to restore.
     */
    public static Map<Location, String> blocksToRestore = new HashMap<>();

    /**
     * List of all the current Treasure Blocks.
     */
    public static List<Block> treasureBlocks = new ArrayList<>();

    /**
     * Gets blocks in radius.
     *
     * @param location The center.
     * @param radius   The radius.
     * @param hollow   if the sphere of blocks should be hollow.
     * @return The list of all the blocks in the given radius.
     */
    public static List<Block> getBlocksInRadius(Location location, int radius, boolean hollow) {
        List<Block> blocks = new ArrayList<>();

        int bX = location.getBlockX();
        int bY = location.getBlockY();
        int bZ = location.getBlockZ();

        for (int x = bX - radius; x <= bX + radius; x++) {
            for (int y = bY - radius; y <= bY + radius; y++) {
                for (int z = bZ - radius; z <= bZ + radius; z++) {

                    double distance = ((bX - x) * (bX - x) + (bY - y) * (bY - y) + (bZ - z) * (bZ - z));

                    if (distance < radius * radius
                            && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
                        Location l = new Location(location.getWorld(), x, y, z);
                        if (l.getBlock().getType() != Material.BARRIER)
                            blocks.add(l.getBlock());
                    }
                }

            }
        }

        return blocks;
    }

    /**
     * Checks if an entity is on ground.
     *
     * @param entity The entity to check.
     * @return {@code true} if entity is on ground, otherwise {@code false}.
     */
    public static boolean isOnGround(Entity entity) {
        Block block = entity.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (block.getType().isSolid())
            return true;
        return false;
    }

    /**
     * Checks if a block is part of a rocket.
     *
     * @param b The block to check.
     * @return {@code true} if the block is part of a rocket, otherwise {@code false}.
     */
    public static boolean isRocketBlock(Block b) {
        for (CustomPlayer cp : Core.getCustomPlayers()) {
            if (cp.currentGadget != null
                    && cp.currentGadget.getType() == GadgetType.ROCKET) {
                GadgetRocket rocket = (GadgetRocket) cp.currentGadget;
                if (rocket.blocks.contains(b))
                    return true;
            }
        }
        return false;
    }

    /**
     * Force-restores the blocks.
     */
    public static void forceRestore() {
        for (Location loc : blocksToRestore.keySet()) {
            Block b = loc.getBlock();
            String s = blocksToRestore.get(loc);
            Material m = Material.valueOf(s.split(",")[0]);
            byte d = Byte.valueOf(s.split(",")[1]);
            b.setType(m);
            b.setData(d);
        }
    }

    /**
     * Restores the block at the location "loc".
     *
     * @param LOCATION The location of the block to restore.
     */
    public static void restoreBlockAt(final Location LOCATION) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (!blocksToRestore.containsKey(LOCATION)) return;
                Block b = LOCATION.getBlock();
                String s = blocksToRestore.get(LOCATION);
                Material m = Material.valueOf(s.split(",")[0]);
                byte d = Byte.valueOf(s.split(",")[1]);
                for (Player player : b.getLocation().getWorld().getPlayers())
                    player.sendBlockChange(LOCATION, m, d);
                blocksToRestore.remove(LOCATION);
            }
        });
    }

    /**
     * Replaces a block with a new material and data, and after delay, restore it.
     *
     * @param BLOCK      The block.
     * @param NEW_TYPE   The new material.
     * @param NEW_DATA   The new data.
     * @param TICK_DELAY The delay after which the block is restored.
     */
    public static void setToRestoreIgnoring(final Block BLOCK, final Material NEW_TYPE, final byte NEW_DATA, final int TICK_DELAY) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (blocksToRestore.containsKey(BLOCK.getLocation())) return;
                if (!blocksToRestore.containsKey(BLOCK.getLocation())) {
                    blocksToRestore.put(BLOCK.getLocation(), BLOCK.getType().toString() + "," + BLOCK.getData());
                    for (Player player : BLOCK.getLocation().getWorld().getPlayers())
                        player.sendBlockChange(BLOCK.getLocation(), NEW_TYPE, NEW_DATA);
                    Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            restoreBlockAt(BLOCK.getLocation());

                        }
                    }, TICK_DELAY);
                }
            }
        });
    }

    /**
     * Replaces a block with a new material and data, and after delay, restore it.
     *
     * @param BLOCK      The block.
     * @param NEW_TYPE   The new material.
     * @param NEW_DATA   The new data.
     * @param TICK_DELAY The delay after which the block is restored.
     */
    public static void setToRestore(final Block BLOCK, final Material NEW_TYPE, final byte NEW_DATA, final int TICK_DELAY) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (blocksToRestore.containsKey(BLOCK.getLocation())) return;
                Block bUp = BLOCK.getRelative(BlockFace.UP);
                if (BLOCK.getType() != Material.AIR
                        && BLOCK.getType() != Material.SIGN_POST
                        && BLOCK.getType() != Material.CHEST
                        && BLOCK.getType() != Material.STONE_PLATE
                        && BLOCK.getType() != Material.WOOD_PLATE
                        && BLOCK.getType() != Material.WALL_SIGN
                        && BLOCK.getType() != Material.WALL_BANNER
                        && BLOCK.getType() != Material.STANDING_BANNER
                        && BLOCK.getType() != Material.CROPS
                        && BLOCK.getType() != Material.LONG_GRASS
                        && BLOCK.getType() != Material.SAPLING
                        && BLOCK.getType() != Material.DEAD_BUSH
                        && BLOCK.getType() != Material.RED_ROSE
                        && BLOCK.getType() != Material.RED_MUSHROOM
                        && BLOCK.getType() != Material.BROWN_MUSHROOM
                        && BLOCK.getType() != Material.TORCH
                        && BLOCK.getType() != Material.LADDER
                        && BLOCK.getType() != Material.VINE
                        && BLOCK.getType() != Material.DOUBLE_PLANT
                        && BLOCK.getType() != Material.PORTAL
                        && BLOCK.getType() != Material.CACTUS
                        && BLOCK.getType() != Material.WATER
                        && BLOCK.getType() != Material.STATIONARY_WATER
                        && BLOCK.getType() != Material.LAVA
                        && BLOCK.getType() != Material.STATIONARY_LAVA
                        && BLOCK.getType() != Material.PORTAL
                        && BLOCK.getType() != Material.ENDER_PORTAL
                        && BLOCK.getType() != Material.SOIL
                        && BLOCK.getType() != Material.BARRIER
                        && BLOCK.getType() != Material.COMMAND
                        && BLOCK.getType() != Material.DROPPER
                        && BLOCK.getType() != Material.DISPENSER
                        && !((ArrayList<String>) SettingsManager.getConfig().get("Gadgets.PaintballGun.BlackList")).contains(BLOCK.getType().toString().toUpperCase())
                        && !BLOCK.getType().toString().toLowerCase().contains("door")
                        && BLOCK.getType() != Material.BED
                        && BLOCK.getType() != Material.BED_BLOCK
                        && !isPortalBlock(BLOCK)
                        && !isRocketBlock(BLOCK)
                        && !isTreasureChestBlock(BLOCK)
                        && !blocksToRestore.containsKey(BLOCK.getLocation())
                        && net.minecraft.server.v1_8_R3.Block.getById(BLOCK.getTypeId()).getMaterial().isSolid()
                        && a(bUp)
                        && BLOCK.getType().getId() != 43
                        && BLOCK.getType().getId() != 44) {
                    blocksToRestore.put(BLOCK.getLocation(), BLOCK.getType().toString() + "," + BLOCK.getData());
                    for (Player player : BLOCK.getLocation().getWorld().getPlayers())
                        player.sendBlockChange(BLOCK.getLocation(), NEW_TYPE, NEW_DATA);
                    Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            restoreBlockAt(BLOCK.getLocation());
                        }
                    }, TICK_DELAY);
                }

            }
        });
    }

    /**
     * Checks if a block is part of a Treasure Chest.
     *
     * @param block The block to check.
     * @return {@code true} if yes, otherwise {@code false}.
     */
    public static boolean isTreasureChestBlock(Block block) {
        return treasureBlocks.contains(block);
    }


    private static boolean a(Block b) {
        if (b.getType() == Material.AIR
                || net.minecraft.server.v1_8_R3.Block.getById(b.getTypeId()).getMaterial().isSolid())
            return true;
        return false;
    }

    /**
     * Checks if a block is part of a Nether Portal.
     *
     * @param b The block to check
     * @return {@code true} if a block is part of a Nether Portal, otherwise {@code false}.
     */
    public static boolean isPortalBlock(Block b) {
        for (BlockFace face : BlockFace.values())
            if (b.getRelative(face).getType() == Material.PORTAL)
                return true;
        return false;
    }


}
