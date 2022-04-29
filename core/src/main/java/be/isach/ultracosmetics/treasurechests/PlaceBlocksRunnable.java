package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.player.UltraPlayerManager;
import be.isach.ultracosmetics.util.BlockUtils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceBlocksRunnable extends BukkitRunnable {
    private static final BlockFace[] SURROUNDING_FACES = new BlockFace[] {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.SOUTH_EAST, BlockFace.NORTH_WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_WEST};
    private final TreasureChest chest;
    private final TreasureChestDesign design;
    private final UltraCosmetics uc = UltraCosmeticsData.get().getPlugin();
    private int i = 5;
    private ChestParticleRunnable particleRunnable = null;
    public PlaceBlocksRunnable(TreasureChest chest) {
        this.chest = chest;
        this.design = chest.getDesign();
    }

    @Override
    public void run() {
        Player player = chest.getPlayer();
        UltraPlayerManager pm = uc.getPlayerManager();
        if (player == null || (pm.getUltraPlayer(player).getCurrentTreasureChest() != chest)) {
            cancel();
            return;
        }
        if (i == 0) {
            particleRunnable = new ChestParticleRunnable(chest);
            particleRunnable.runTaskTimer(uc, 0L, 50L);
            cancel();
            return;
        }
        Block lampBlock;
        if (i == 5) {
            lampBlock = player.getLocation().add(0.0D, -1.0D, 0.0D).getBlock();
            chest.setCenter(lampBlock.getLocation().add(0.5D, 1.0D, 0.5D));
            doChestStage(Arrays.asList(lampBlock), design.getCenter());
        } else if (i == 4) {
            doChestStage(getSurroundingBlocks(chest.getCenter().clone().add(0.0D, -1.0D, 0.0D).getBlock()), design.getBlocks2());
        } else if (i == 3) {
            doChestStage(getSurroundingSurrounding(chest.getCenter().clone().add(0.0D, -1.0D, 0.0D).getBlock()), design.getBlocks3());
        } else if (i == 2) {
            doChestStage(getBlock3(chest.getCenter().clone().add(0.0D, -1.0D, 0.0D).getBlock()), design.getBelowChests());
        } else if (i == 1) {
            doChestStage(getSurroundingSurrounding(chest.getCenter().getBlock()), design.getBarriers());
        }
        i--;
    }

    private void doChestStage(Iterable<Block> blocks, XMaterial newData) {
        if (newData == null) return;
        for (Block b : blocks) {
            chest.addRestoreBlock(b);
            BlockUtils.treasureBlocks.add(b);
            XBlock.setType(b, newData);
        }
    }

    public List<Block> getSurroundingBlocks(Block b) {
        List<Block> blocks = new ArrayList<>();
        for (BlockFace face : SURROUNDING_FACES) {
            blocks.add(b.getRelative(face));
        }
        return blocks;
    }

    private List<Block> getSurroundingSurrounding(Block b) {
        List<Block> blocks = new ArrayList<>();
        // makes a pattern in the shape of:
        // XX XX
        // X   X
        //      
        // X   X
        // XX XX
        for (int x = -2; x <= 2; x++) {
            if (x == 0) continue;
            
            for (int z = -2; z <= 2; z++) {
                if (z == 0) continue;
                if (Math.abs(x) == 1 && Math.abs(z) == 1) continue;
                
                blocks.add(b.getRelative(x, 0, z));
            }
        }
        return blocks;
    }

    private List<Block> getBlock3(Block b) {
        List<Block> blocks = new ArrayList<>();
        blocks.add(b.getRelative(-2, 0, 0));
        blocks.add(b.getRelative(2, 0, 0));
        blocks.add(b.getRelative(0, 0, 2));
        blocks.add(b.getRelative(0, 0, -2));
        return blocks;
    }

    public void propogateCancel() {
        cancel();
        if (particleRunnable != null) {
            particleRunnable.propogateCancel();;
        }
    }
}
