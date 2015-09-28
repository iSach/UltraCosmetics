package me.isach.ultracosmetics.util;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.CustomPlayer;
import me.isach.ultracosmetics.config.SettingsManager;
import me.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import me.isach.ultracosmetics.cosmetics.gadgets.GadgetRocket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sacha on 03/08/15.
 */
public class BlockUtils {

    public static Map<Location, String> blocksToRestore = new HashMap<>();

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

    public static boolean isRocketBlock(Block b) {
        for (CustomPlayer cp : Core.getCustomPlayers()) {
            if (cp.currentGadget != null
                    && cp.currentGadget.getType() == Gadget.GadgetType.ROCKET) {
                GadgetRocket rocket = (GadgetRocket) cp.currentGadget;
                if (rocket.blocks.contains(b))
                    return true;
            }
        }
        return false;
    }


    public static double getDistance(int x1, int z1, int x2, int z2) {
        int dx = x1 - x2;
        int dz = z1 - z2;
        return Math.sqrt((dx * dx + dz * dz));
    }

    /**
     * Forces restoring the blocks.
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
     * @param loc The location of the block to restore.
     */
    public static void restoreBlockAt(Location loc) {
        if (!blocksToRestore.containsKey(loc)) return;
        Block b = loc.getBlock();
        String s = blocksToRestore.get(loc);
        Material m = Material.valueOf(s.split(",")[0]);
        byte d = Byte.valueOf(s.split(",")[1]);
        b.setType(m);
        b.setData(d);
        blocksToRestore.remove(loc);
    }

    /**
     * Replaces a block with a new material and data, and after delay, restore it.
     *
     * @param b         The block.
     * @param newType   The new material.
     * @param newData   The new data.
     * @param tickDelay The delay after which the block is restored.
     */
    public static void setToRestore(final Block b, Material newType, byte newData, int tickDelay) {
        if (blocksToRestore.containsKey(b.getLocation())) return;
        Block bUp = b.getRelative(BlockFace.UP);
        if (b.getType() != Material.AIR
                && b.getType() != Material.SIGN_POST
                && b.getType() != Material.CHEST
                && b.getType() != Material.STONE_PLATE
                && b.getType() != Material.WOOD_PLATE
                && b.getType() != Material.WALL_SIGN
                && b.getType() != Material.WALL_BANNER
                && b.getType() != Material.STANDING_BANNER
                && b.getType() != Material.CROPS
                && b.getType() != Material.LONG_GRASS
                && b.getType() != Material.SAPLING
                && b.getType() != Material.DEAD_BUSH
                && b.getType() != Material.RED_ROSE
                && b.getType() != Material.RED_MUSHROOM
                && b.getType() != Material.BROWN_MUSHROOM
                && b.getType() != Material.TORCH
                && b.getType() != Material.LADDER
                && b.getType() != Material.VINE
                && b.getType() != Material.DOUBLE_PLANT
                && b.getType() != Material.PORTAL
                && b.getType() != Material.CACTUS
                && b.getType() != Material.WATER
                && b.getType() != Material.STATIONARY_WATER
                && b.getType() != Material.LAVA
                && b.getType() != Material.STATIONARY_LAVA
                && b.getType() != Material.PORTAL
                && b.getType() != Material.ENDER_PORTAL
                && b.getType() != Material.SOIL
                && b.getType() != Material.BARRIER
                && b.getType() != Material.COMMAND
                && b.getType() != Material.DROPPER
                && b.getType() != Material.DISPENSER
                && !((ArrayList<String>) SettingsManager.getConfig().get("Gadgets.PaintballGun.BlackList")).contains(b.getType().toString().toUpperCase())
                && !b.getType().toString().toLowerCase().contains("door")
                && b.getType() != Material.BED
                && b.getType() != Material.BED_BLOCK
                && !isPortalBlock(b)
                && !isRocketBlock(b)
                && net.minecraft.server.v1_8_R3.Block.getById(b.getTypeId()).getMaterial().isSolid()
                && a(bUp)
                && b.getType().getId() != 43
                && b.getType().getId() != 44) {
            if (!blocksToRestore.containsKey(b.getLocation())) {
                blocksToRestore.put(b.getLocation(), b.getType().toString() + "," + b.getData());
                b.setType(newType);
                b.setData(newData);
                Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        restoreBlockAt(b.getLocation());

                    }
                }, tickDelay);

            }

        }
    }

    private static boolean a(Block b) {
        if(b.getType() == Material.AIR
            || net.minecraft.server.v1_8_R3.Block.getById(b.getTypeId()).getMaterial().isSolid())
            return true;
        return false;
    }

    public static boolean isPortalBlock(Block b) {
        for(BlockFace face : BlockFace.values())
            if(b.getRelative(face).getType() == Material.PORTAL)
                return true;
        return false;
    }


}
