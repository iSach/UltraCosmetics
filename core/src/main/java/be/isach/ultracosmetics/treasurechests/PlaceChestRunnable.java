package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.util.Particles;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XSound;

public class PlaceChestRunnable extends BukkitRunnable {
    private final TreasureChest chest;
    private final Location chestLocation;
    private final int locationIndex;
    public PlaceChestRunnable(TreasureChest chest, Location chestLocation, int locationIndex) {
        this.chest = chest;
        this.chestLocation = chestLocation;
        this.locationIndex = locationIndex;
    }

    @Override
    public void run() {
        Block b = chestLocation.getBlock();
        b.setType(chest.getDesign().getChestType().getType());
        XSound.BLOCK_ANVIL_LAND.play(chest.getPlayer(), 1.4f, 1.5f);
        Particles.SMOKE_LARGE.display(b.getLocation(), 5);
        Particles.LAVA.display(b.getLocation(), 5);
        BlockFace blockFace = BlockFace.SOUTH;
        switch (locationIndex) {
            case 4:
                blockFace = BlockFace.SOUTH;
                break;
            case 3:
                blockFace = BlockFace.NORTH;
                break;
            case 2:
                blockFace = BlockFace.EAST;
                break;
            case 1:
                blockFace = BlockFace.WEST;
                break;
        }
        XBlock.setDirection(b, blockFace);
        chest.addChest(b);
    }

}
