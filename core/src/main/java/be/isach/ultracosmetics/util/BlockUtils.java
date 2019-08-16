package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetRocket;
import be.isach.ultracosmetics.version.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
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
     * List of all the BLOCKS to restore.
     */
    public static Map<Location, String> blocksToRestore = new HashMap<>();

    /**
     * List of all the current Treasure Blocks.
     */
    public static List<Block> treasureBlocks = new ArrayList<>();

    /**
     * Gets BLOCKS in radius.
     *
     * @param location The center.
     * @param radius   The radius.
     * @param hollow   if the sphere of BLOCKS should be hollow.
     * @return The list of all the BLOCKS in the given radius.
     */
    public static List<Block> getBlocksInRadius(Location location, int radius, boolean hollow) {
        List<Block> blocks = new ArrayList<>();
        int bX = location.getBlockX(),
                bY = location.getBlockY(),
                bZ = location.getBlockZ();
        for (int x = bX - radius; x <= bX + radius; x++)
            for (int y = bY - radius; y <= bY + radius; y++)
                for (int z = bZ - radius; z <= bZ + radius; z++) {
                    double distance = ((bX - x) * (bX - x) + (bY - y) * (bY - y) + (bZ - z) * (bZ - z));
                    if (distance < radius * radius
                            && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
                        Location l = new Location(location.getWorld(), x, y, z);
                        if (l.getBlock().getType() != Material.BARRIER)
                            blocks.add(l.getBlock());
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
        return block.getType().isSolid();
    }

    /**
     * Checks if a block is part of a rocket.
     *
     * @param b The block to check.
     * @return {@code true} if the block is part of a rocket, otherwise {@code false}.
     */
    public static boolean isRocketBlock(Block b) {
        return GadgetRocket.BLOCKS.contains(b);
    }

    /**
     * Force-restores the BLOCKS.
     */
    public static void forceRestore() {
        for (Location loc : blocksToRestore.keySet()) {
            try {
                Block b = loc.getBlock();
                String s = blocksToRestore.get(loc);
                Material m = Material.valueOf(s.split(",")[0]);
                byte d = Byte.valueOf(s.split(",")[1]);
                b.setType(m);
                b.getState().setRawData(d);
                b.getState().update();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Restores the block at the location "loc".
     *
     * @param location The location of the block to restore.
     */
    public static void restoreBlockAt(final Location location) {
        if (!blocksToRestore.containsKey(location)) return;
        Block b = location.getBlock();
        String s = blocksToRestore.get(location);
        Material m = Material.valueOf(s.split(",")[0]);
        byte d = Byte.valueOf(s.split(",")[1]);
        b.getLocation().getWorld().getPlayers().forEach(player -> player.sendBlockChange(location, m, d));
        blocksToRestore.remove(location);
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
        Bukkit.getScheduler().runTaskAsynchronously(UltraCosmeticsData.get().getPlugin(), () -> {
            if (blocksToRestore.containsKey(BLOCK.getLocation())) return;
            if (!blocksToRestore.containsKey(BLOCK.getLocation())) {
                blocksToRestore.put(BLOCK.getLocation(), BLOCK.getType().toString() + "," + BLOCK.getData());
                for (Player player : BLOCK.getLocation().getWorld().getPlayers())
                    player.sendBlockChange(BLOCK.getLocation(), NEW_TYPE, NEW_DATA);
                Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> restoreBlockAt(BLOCK.getLocation()), TICK_DELAY);
            }
        });
    }

    /**
     * Replaces a block with a new material and data, and after delay, restore it.
     *
     * @param block     The block.
     * @param newType   The new material.
     * @param newData   The new data.
     * @param tickDelay The delay after which the block is restored.
     */
    public static void setToRestore(final Block block, final Material newType, final byte newData, final int tickDelay) {
        Bukkit.getScheduler().runTaskAsynchronously(UltraCosmeticsData.get().getPlugin(), () -> {
            if (blocksToRestore.containsKey(block.getLocation())) return;
            Block bUp = block.getRelative(BlockFace.UP);
            if (!(block.getType() != Material.AIR
                    && block.getType() != getOldMaterial("SIGN_POST")
                    && block.getType() != Material.CHEST
                    && block.getType() != getOldMaterial("STONE_PLATE")
                    && block.getType() != getOldMaterial("WOOD_PLATE")
                    && block.getType() != UCMaterial.ACACIA_WALL_SIGN.parseMaterial()
                    && block.getType() != UCMaterial.BIRCH_WALL_SIGN.parseMaterial()
                    && block.getType() != UCMaterial.DARK_OAK_WALL_SIGN.parseMaterial()
                    && block.getType() != UCMaterial.JUNGLE_WALL_SIGN.parseMaterial()
                    && block.getType() != UCMaterial.OAK_WALL_SIGN.parseMaterial()
                    && block.getType() != UCMaterial.DARK_OAK_WALL_SIGN.parseMaterial()
                    && block.getType() != getOldMaterial("WALL_BANNER")
                    && block.getType() != getOldMaterial("STANDING_BANNER")
                    && block.getType() != getOldMaterial("CROPS")
                    && block.getType() != getOldMaterial("LONG_GRASS")
                    && block.getType() != getOldMaterial("SAPLING")
                    && block.getType() != Material.DEAD_BUSH
                    && block.getType() != getOldMaterial("RED_ROSE")
                    && block.getType() != Material.RED_MUSHROOM
                    && block.getType() != Material.BROWN_MUSHROOM
                    && block.getType() != Material.TORCH
                    && block.getType() != Material.LADDER
                    && block.getType() != Material.VINE
                    && block.getType() != getOldMaterial("DOUBLE_PLANT")
                    && block.getType() != getOldMaterial("PORTAL")
                    && block.getType() != Material.CACTUS
                    && block.getType() != Material.WATER
                    && block.getType() != getOldMaterial("STATIONARY_WATER")
                    && block.getType() != Material.LAVA
                    && block.getType() != getOldMaterial("STATIONARY_LAVA")
                    && block.getType() != getOldMaterial("PORTAL")
                    && block.getType() != getOldMaterial("ENDER_PORTAL")
                    && block.getType() != getOldMaterial("SOIL")
                    && block.getType() != Material.BARRIER
                    && block.getType() != getOldMaterial("COMMAND")
                    && block.getType() != Material.DROPPER
                    && block.getType() != Material.DISPENSER
                    && !SettingsManager.getConfig().getStringList("Gadgets.PaintballGun.BlackList").contains(block.getType().toString().toUpperCase())
                    && !block.getType().toString().toLowerCase().contains("door")
                    && block.getType() != getOldMaterial("BED")
                    && block.getType() != getOldMaterial("BED_BLOCK")
                    && !isPortalBlock(block)
                    && !isRocketBlock(block)
                    && !isTreasureChestBlock(block)
                    && !blocksToRestore.containsKey(block.getLocation())
                    && block.getType().isSolid()
                    && a(bUp)
                    && !block.getType().toString().toLowerCase().contains("slab"))) return;

            blocksToRestore.put(block.getLocation(), block.getType().toString() + "," + block.getData());
            for (Player player : block.getLocation().getWorld().getPlayers()) {
                player.sendBlockChange(block.getLocation(), newType, newData);
            }
            Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> restoreBlockAt(block.getLocation()), tickDelay);
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
        return b.getType() == Material.AIR
                || b.getType().isSolid();
    }

    /**
     * Checks if a block is part of a Nether Portal.
     *
     * @param b The block to check
     * @return {@code true} if a block is part of a Nether Portal, otherwise {@code false}.
     */
    public static boolean isPortalBlock(Block b) {
        for (BlockFace face : BlockFace.values())
            if (b.getRelative(face).getType() == getOldMaterial("PORTAL"))
                return true;
        return false;
    }

    public static Material getOldMaterial(String material) {
        if (VersionManager.IS_VERSION_1_13) {
            return Material.getMaterial(material, true);
        }
        return Material.getMaterial(material);
    }

    public static Material getBlockByColor(String oldMaterialName, byte color) {
        switch (color) {
            case 0x0:
                return getBlockByColor(oldMaterialName, DyeColor.WHITE);
            case 0x1:
                return getBlockByColor(oldMaterialName, DyeColor.ORANGE);
            case 0x2:
                return getBlockByColor(oldMaterialName, DyeColor.MAGENTA);
            case 0x3:
                return getBlockByColor(oldMaterialName, DyeColor.LIGHT_BLUE);
            case 0x4:
                return getBlockByColor(oldMaterialName, DyeColor.YELLOW);
            case 0x5:
                return getBlockByColor(oldMaterialName, DyeColor.LIME);
            case 0x6:
                return getBlockByColor(oldMaterialName, DyeColor.PINK);
            case 0x7:
                return getBlockByColor(oldMaterialName, DyeColor.GRAY);
            case 0x8:
                return getBlockByColor(oldMaterialName, DyeColor.LIGHT_GRAY);
            case 0x9:
                return getBlockByColor(oldMaterialName, DyeColor.CYAN);
            case 0xA:
                return getBlockByColor(oldMaterialName, DyeColor.PURPLE);
            case 0xB:
                return getBlockByColor(oldMaterialName, DyeColor.BLUE);
            case 0xC:
                return getBlockByColor(oldMaterialName, DyeColor.BROWN);
            case 0xD:
                return getBlockByColor(oldMaterialName, DyeColor.GREEN);
            case 0xE:
                return getBlockByColor(oldMaterialName, DyeColor.RED);
            case 0xF:
                return getBlockByColor(oldMaterialName, DyeColor.BLACK);
            default:
                return getBlockByColor(oldMaterialName, DyeColor.WHITE);
        }
    }

    public static Material getDyeByColor(byte color) {
        if (!VersionManager.IS_VERSION_1_13) {
            return Material.getMaterial("INK_SACK");
        }
        return Material.getMaterial("INK_SACK");
        /*switch (color) {
            case 0x0:
                return Material.valueOf("INK_SAC");
            case 0x1:
                return Material.valueOf("ROSE_RED");
            case 0x2:
                return Material.valueOf("CACTUS_GREEN");
            case 0x3:
                return Material.valueOf("COCOA_BEANS");
            case 0x4:
                return Material.valueOf("LAPIS_LAZULI");
            case 0x5:
                return Material.valueOf("PURPLE_DYE");
            case 0x6:
                return Material.valueOf("CYAN_DYE");
            case 0x7:
                return Material.valueOf("LIGHT_GRAY_DYE");
            case 0x8:
                return Material.valueOf("GRAY_DYE");
            case 0x9:
                return Material.valueOf("PINK_DYE");
            case 0xA:
                return Material.valueOf("LIME_DYE");
            case 0xB:
                return Material.valueOf("DANDELION_YELLOW");
            case 0xC:
                return Material.valueOf("LIGHT_BLUE_DYE");
            case 0xD:
                return Material.valueOf("MAGENTA_DYE");
            case 0xE:
                return Material.valueOf("ORANGE_DYE");
            case 0xF:
                return Material.valueOf("BONE_MEAL");
            default:
                return Material.valueOf("BONE_MEAL");
        }*/
    }

    public static Material getBlockByColor(String oldMaterialName, DyeColor color) {
        if (VersionManager.IS_VERSION_1_13) {
            oldMaterialName = oldMaterialName.replace("STAINED_CLAY", "CONCRETE");
            return Material.getMaterial(color.toString() + "_" + oldMaterialName);
        }
        return Material.WHITE_WOOL;
    }
}
