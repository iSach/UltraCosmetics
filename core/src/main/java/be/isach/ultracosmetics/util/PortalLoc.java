package be.isach.ultracosmetics.util;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class PortalLoc {
    private final int red, green, blue;
    private Location location;
    private BlockFace face;

    public PortalLoc(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Location getLocation() {
        return location;
    }

    public BlockFace getFace() {
        return face;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setFace(BlockFace face) {
        this.face = face;
    }

    public boolean isValid() {
        return location != null && face != null;
    }

    public void clear() {
        location = null;
        face = null;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }
}
