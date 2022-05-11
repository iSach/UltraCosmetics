package be.isach.ultracosmetics.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.function.Function;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.log.SmartLogger;

public class Area {
    private static final boolean DEBUG = SettingsManager.getConfig().getBoolean("Area-Debug");
    protected final World world;
    protected final int x1, y1, z1;
    protected final int x2, y2, z2;
    public Area(Location loc1, Location loc2) {
        if (loc1.getWorld() != loc2.getWorld()) {
            throw new IllegalArgumentException("Locations cannot be in different worlds");
        }
        this.world = loc1.getWorld();
        this.x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        this.y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        this.z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        this.x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        this.y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        this.z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
    }

    public Area(Location center, int radius, int yUp) {
        this(center.clone().add(-radius, 0, -radius), center.clone().add(radius, yUp, radius));
    }

    /**
     * Checks each material in the area against okMatFunc,
     * and returns true if every block is "ok" accordingly.
     *
     * Ignores the block at (badX, badY, badZ)
     *
     * @param badX X coordinate of block to ignore
     * @param badY Y coordinate of block to ignore
     * @param badZ Z coordinate of block to ignore
     * @param okMatFunc A function that decides if a Material is OK to be there
     * @return true if every block matches okMatFunc
     */
    public boolean isEmptyExcept(int badX, int badY, int badZ, Function<Material,Boolean> okMatFunc) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    if (x == badX && y == badY && z == badZ) continue;
                    if (!okMatFunc.apply(world.getBlockAt(x, y, z).getType())) {
                        if (DEBUG) {
                            SmartLogger log = UltraCosmeticsData.get().getPlugin().getSmartLogger();
                            log.write("Failed area check at (" + x + "," + y + "," + z + ") because it is " + world.getBlockAt(x, y, z).getType());
                        }
                        return false;
                    }
                }
            }
        }
        if (DEBUG) {
            UltraCosmeticsData.get().getPlugin().getSmartLogger().write("Area check passed");
        }
        return true;
    }

    public boolean isEmptyExcept(Location loc) {
        return isEmptyExcept(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), m -> BlockUtils.isAir(m));
    }

    public boolean isEmpty() {
        // no special meaning, but the loop will never make it that far
        return isEmptyExcept(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, m -> BlockUtils.isAir(m));
    }

    // It's deprecated because it "does not have an implementation which is well linked to the underlying server,"
    // but that doesn't really matter for our purposes.
    @SuppressWarnings("deprecation")
    public boolean isTransparent() {
        return isEmptyExcept(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, m -> m.isTransparent());
    }

    public boolean contains(Block block) {
        return block.getWorld() == world && block.getX() >= x1 && block.getX() <= x2 && block.getY() >= y1
                && block.getY() <= y2 && block.getZ() >= z1 && block.getZ() <= z2;
    }
}
