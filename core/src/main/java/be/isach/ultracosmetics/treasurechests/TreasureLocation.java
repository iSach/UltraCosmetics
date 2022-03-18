package be.isach.ultracosmetics.treasurechests;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

// the main purpose of this class is to hold a Location with a nullable World
public class TreasureLocation {
    private final World world;
    private final double x;
    private final double y;
    private final double z;
    public TreasureLocation(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location toLocation(Player player) {
        // uses originally defined world if present, otherwise uses player's current world
        return new Location(world == null ? player.getWorld() : world, x, y, z);
    }

    public void tpTo(Player player) {
        // add 0.5 to the Y too so it's less likely the player gets stuck in the ground
        player.teleport(toLocation(player).add(0.5, 0.5, 0.5));
    }

    public static TreasureLocation fromLocation(Location location) {
        return new TreasureLocation(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
