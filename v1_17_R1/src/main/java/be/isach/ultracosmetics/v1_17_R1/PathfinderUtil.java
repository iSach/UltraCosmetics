package be.isach.ultracosmetics.v1_17_R1;

import be.isach.ultracosmetics.version.IPathfinderUtil;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

/**
 * @author iSach
 * <p>
 * Thanks to jojokobi (https://www.spigotmc.org/threads/clearing-pathfinder-goals-in-minecraft-1-14.371282/)
 * for the fix in 1.14
 *
 * In 1.17 we have an API method for essentially disabling all pathfinders, and nobody that uses this method
 * needs to use any other pathfinders or needs them re-enabled at any point.
 */
public class PathfinderUtil implements IPathfinderUtil {

    @Override
    public void removePathFinders(Entity entity) {
        ((Mob)entity).setAware(false);
    }
}
