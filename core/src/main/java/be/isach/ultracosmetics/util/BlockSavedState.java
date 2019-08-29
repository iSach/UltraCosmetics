package be.isach.ultracosmetics.util;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

// TODO Delete
public class BlockSavedState {

    private Material type;
    private byte data;
    private Location location;

    public BlockSavedState(Material type, byte data, Location location) {
        this.type = type;
        this.data = data;
        this.location = location;
    }

    public static BlockSavedState fromBlock(Block block) {
        return new BlockSavedState(block.getType(), block.getState().getRawData(), block.getLocation());
    }

    public byte getData() {
        return data;
    }

    public Location getLocation() {
        return location;
    }

    public Material getType() {
        return type;
    }
}
