package be.isach.ultracosmetics.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.isach.ultracosmetics.version.VersionManager;

public class BlockViewUpdater extends BukkitRunnable {
    private static final Set<Block> blocksUpdating = ConcurrentHashMap.newKeySet();
    private Set<Block> blocks;
    public BlockViewUpdater(Set<Block> blocks) {
        this.blocks = blocks;
        addForProcessing(blocks);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        removeForProcessing(blocks);
        for (Block block : blocks) {
            for (Player player : block.getWorld().getPlayers()) {
                if (VersionManager.IS_VERSION_1_13) {
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                } else {
                    player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
                }
            }
        }
    }

    public static boolean isUpdating(Block block) {
        return blocksUpdating.contains(block);
    }

    // Synchronized add and remove methods so we can properly minimize packets sent,
    // as well as avoid leaving any blocks stuck in queue.
    private static synchronized void addForProcessing(Set<Block> blocks) {
        blocks.removeAll(blocksUpdating);
        blocksUpdating.addAll(blocks);
    }

    private static synchronized void removeForProcessing(Set<Block> blocks) {
        blocksUpdating.removeAll(blocks);
    }
}
