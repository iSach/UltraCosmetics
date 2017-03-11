package be.isach.ultracosmetics.run;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by sacha on 11/26/2016.
 */
public class MovingChecker extends BukkitRunnable {

    private UltraCosmetics ultraCosmetics;

    public MovingChecker(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
    }

    @Override
    public void run() {
        synchronized (ultraCosmetics.getPlayerManager().getUltraPlayers()) {
            for (UltraPlayer ultraPlayer : ultraCosmetics.getPlayerManager().getUltraPlayers()) {
                if(ultraPlayer != null
                        && ultraPlayer.getBukkitPlayer() != null) {
                    continue;
                }

                Location currentPos = ultraPlayer.getBukkitPlayer().getLocation();
                ultraPlayer.setMoving(!areEqual(currentPos, ultraPlayer.getLastPos()));
                ultraPlayer.setLastPos(currentPos);
            }
        }
    }

    private boolean areEqual(Location from, Location to) {
        if (from == null || to == null) {
            return false;
        } else if (from.getClass() != to.getClass()) {
            return false;
        } else {
            return !(from.getWorld() != to.getWorld()
                    && (from.getWorld() == null || !from.getWorld().equals(to.getWorld())))
                    && (Double.doubleToLongBits(from.getX()) == Double.doubleToLongBits(to.getX())
                    && (Double.doubleToLongBits(from.getY()) == Double.doubleToLongBits(to.getY())
                    && (Double.doubleToLongBits(from.getZ()) == Double.doubleToLongBits(to.getZ()))));
        }
    }

}
