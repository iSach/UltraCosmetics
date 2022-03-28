package be.isach.ultracosmetics.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Area {
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

    public boolean isEmptyExcept(int badX, int badY, int badZ) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    if (x == badX && y == badY && z == badZ) continue;
                    if (world.getBlockAt(x, y, z).getType() != Material.AIR) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isEmptyExcept(Location loc) {
        return isEmptyExcept(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public boolean isEmpty() {
        // no special meaning, but the loop will never make it that far
        return isEmptyExcept(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public boolean contains(Block block) {
        return block.getWorld() == world && block.getX() >= x1 && block.getX() <= x2 && block.getY() >= y1
                && block.getY() <= y2 && block.getZ() >= z1 && block.getZ() <= z2;
    }
}
