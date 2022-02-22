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
    public static final List<Block> treasureBlocks = new ArrayList<>();

    /**
     * Set containing all materials that shouldn't
     * be used with player.sendBlockChange
     */
    private static final Set<Material> badMaterials = new HashSet<>();

    static {
        Set<XMaterial> badXMaterials = new HashSet<>();
        badXMaterials.add(XMaterial.AIR);
        badXMaterials.add(XMaterial.CHEST);
        badXMaterials.add(XMaterial.ACACIA_WALL_SIGN);
        badXMaterials.add(XMaterial.BIRCH_WALL_SIGN);
        badXMaterials.add(XMaterial.DARK_OAK_WALL_SIGN);
        badXMaterials.add(XMaterial.JUNGLE_WALL_SIGN);
        badXMaterials.add(XMaterial.OAK_WALL_SIGN);
        badXMaterials.add(XMaterial.DARK_OAK_WALL_SIGN);
        badXMaterials.add(XMaterial.WHEAT);
        badXMaterials.add(XMaterial.GRASS);
        badXMaterials.add(XMaterial.DEAD_BUSH);
        badXMaterials.add(XMaterial.POPPY);
        badXMaterials.add(XMaterial.RED_MUSHROOM);
        badXMaterials.add(XMaterial.BROWN_MUSHROOM);
        badXMaterials.add(XMaterial.TORCH);
        badXMaterials.add(XMaterial.LADDER);
        badXMaterials.add(XMaterial.VINE);
        badXMaterials.add(XMaterial.LARGE_FERN);
        badXMaterials.add(XMaterial.LILAC);
        badXMaterials.add(XMaterial.PEONY);
        badXMaterials.add(XMaterial.ROSE_BUSH);
        badXMaterials.add(XMaterial.SUNFLOWER);
        badXMaterials.add(XMaterial.NETHER_PORTAL);
        badXMaterials.add(XMaterial.CACTUS);
        // I don't think we need to check FLOWING_ variants separately
        badXMaterials.add(XMaterial.WATER);
        badXMaterials.add(XMaterial.LAVA);
        badXMaterials.add(XMaterial.END_PORTAL);
        badXMaterials.add(XMaterial.FARMLAND);
        badXMaterials.add(XMaterial.BARRIER);
        badXMaterials.add(XMaterial.COMMAND_BLOCK);
        badXMaterials.add(XMaterial.DROPPER);
        badXMaterials.add(XMaterial.DISPENSER);

        Set<String> badEndings = new HashSet<>();
        badEndings.add("SLAB");
        badEndings.add("DOOR");
        badEndings.add("SIGN");
        badEndings.add("BED");
        badEndings.add("PRESSURE_PLATE");
        badEndings.add("BANNER");
        badEndings.add("SAPLING");

        for (XMaterial mat : XMaterial.VALUES) {
            for (String ending : badEndings) {
                if (mat.name().endsWith("_" + ending)) {
                    badXMaterials.add(mat);
                    break;
                }
            }
        }

        for (XMaterial mat : badXMaterials) {
            badMaterials.add(mat.parseMaterial());
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
        for (BlockFace face : BlockFace.values()) {
            if (b.getRelative(face).getType() == XMaterial.NETHER_PORTAL.parseMaterial()) {
                return true;
            }
        }
        return false;
    }

    public static Material getBlockByColor(String oldMaterialName, byte color) {
        return getBlockByColor(oldMaterialName, DyeColor.values()[color]);
    }

    public static Material getDyeByColor(byte color) {
        if (!VersionManager.IS_VERSION_1_13) {
            return Material.getMaterial("INK_SACK");
        }
        return Material.getMaterial("INK_SACK");
    }

    public static Material getBlockByColor(String oldMaterialName, DyeColor color) {
        if (VersionManager.IS_VERSION_1_13) {
            oldMaterialName = oldMaterialName.replace("STAINED_CLAY", "CONCRETE");
            return Material.getMaterial(color.toString() + "_" + oldMaterialName);
        }
        return XMaterial.WHITE_WOOL.parseMaterial();
    }

    // Returns true if mat should not be used with player.sendBlockChange per badMaterials Set
    public static boolean isBadMaterial(Material mat) {
        return badMaterials.contains(mat);
    }
}
