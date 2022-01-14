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

import java.util.*;

/**
 * Created by sacha on 03/08/15.
 */
public class BlockUtils {
    /**
     * List of all the current Treasure Blocks.
     */
    public static List<Block> treasureBlocks = new ArrayList<>();

    /**
     * Set containing all materials that shouldn't
     * be used with player.sendBlockChange
     */
    public static Set<Material> badMaterials = new HashSet<>();

    static {
        badMaterials.add(Material.AIR);
        badMaterials.add(getOldMaterial("SIGN_POST"));
        badMaterials.add(Material.CHEST);
        badMaterials.add(getOldMaterial("STONE_PLATE"));
        badMaterials.add(getOldMaterial("WOOD_PLATE"));
        badMaterials.add(UCMaterial.ACACIA_WALL_SIGN.parseMaterial());
        badMaterials.add(UCMaterial.BIRCH_WALL_SIGN.parseMaterial());
        badMaterials.add(UCMaterial.DARK_OAK_WALL_SIGN.parseMaterial());
        badMaterials.add(UCMaterial.JUNGLE_WALL_SIGN.parseMaterial());
        badMaterials.add(UCMaterial.OAK_WALL_SIGN.parseMaterial());
        badMaterials.add(UCMaterial.DARK_OAK_WALL_SIGN.parseMaterial());
        badMaterials.add(getOldMaterial("WALL_BANNER"));
        badMaterials.add(getOldMaterial("STANDING_BANNER"));
        badMaterials.add(getOldMaterial("CROPS"));
        badMaterials.add(getOldMaterial("LONG_GRASS"));
        badMaterials.add(getOldMaterial("SAPLING"));
        badMaterials.add(Material.DEAD_BUSH);
        badMaterials.add(getOldMaterial("RED_ROSE"));
        badMaterials.add(Material.RED_MUSHROOM);
        badMaterials.add(Material.BROWN_MUSHROOM);
        badMaterials.add(Material.TORCH);
        badMaterials.add(Material.LADDER);
        badMaterials.add(Material.VINE);
        badMaterials.add(getOldMaterial("DOUBLE_PLANT"));
        badMaterials.add(getOldMaterial("PORTAL"));
        badMaterials.add(Material.CACTUS);
        badMaterials.add(Material.WATER);
        badMaterials.add(getOldMaterial("STATIONARY_WATER"));
        badMaterials.add(Material.LAVA);
        badMaterials.add(getOldMaterial("STATIONARY_LAVA"));
        badMaterials.add(getOldMaterial("PORTAL"));
        badMaterials.add(getOldMaterial("ENDER_PORTAL"));
        badMaterials.add(getOldMaterial("SOIL"));
        badMaterials.add(Material.BARRIER);
        badMaterials.add(getOldMaterial("COMMAND"));
        badMaterials.add(Material.DROPPER);
        badMaterials.add(Material.DISPENSER);
        badMaterials.add(getOldMaterial("BED"));
        badMaterials.add(getOldMaterial("BED_BLOCK"));

        for (Material mat : Material.values()) {
            if (mat.name().endsWith("_SLAB")) badMaterials.add(mat);
            if (mat.name().endsWith("_DOOR")) badMaterials.add(mat);
        }
    }

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
        for (GadgetRocket rocket : GadgetRocket.ROCKETS_WITH_BLOCKS) {
            if (rocket.getBlocks().contains(b)) {
                return true;
            }
        }
        return false;
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
            if (BlockViewUpdater.isUpdating(BLOCK.getLocation())) return;
            for (Player player : BLOCK.getLocation().getWorld().getPlayers())
                player.sendBlockChange(BLOCK.getLocation(), NEW_TYPE, NEW_DATA);
            new BlockViewUpdater(BLOCK).runTaskLater(UltraCosmeticsData.get().getPlugin(), TICK_DELAY);
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
        if (badMaterials.contains(block.getType())
                || SettingsManager.getConfig().getStringList("Gadgets.PaintballGun.BlackList").contains(block.getType().name())
                || isPortalBlock(block)
                || isRocketBlock(block)
                || isTreasureChestBlock(block)
                || !block.getType().isSolid()
                || !okAboveBlock(block.getRelative(BlockFace.UP).getType()))
            return;
        setToRestoreIgnoring(block, newType, newData, tickDelay);
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

    private static boolean okAboveBlock(Material mat) {
        return mat == Material.AIR || mat.isSolid();
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
                return getBlockByColor(oldMaterialName, VersionManager.IS_VERSION_1_13 ? DyeColor.LIGHT_GRAY : DyeColor.valueOf("SILVER"));
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
        return UCMaterial.WHITE_WOOL.parseMaterial();
    }
}
