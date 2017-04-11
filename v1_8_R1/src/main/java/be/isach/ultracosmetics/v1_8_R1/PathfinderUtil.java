package be.isach.ultracosmetics.v1_8_R1;

import be.isach.ultracosmetics.version.IPathfinderUtil;
import net.minecraft.server.v1_8_R1.EntityInsentient;
import net.minecraft.server.v1_8_R1.PathfinderGoalSelector;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R1.util.UnsafeList;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;

/**
 * @author RadBuilder
 */
public class PathfinderUtil implements IPathfinderUtil {

    @Override
    public void removePathFinders(Entity entity) {
        net.minecraft.server.v1_8_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(((EntityInsentient) nmsEntity).goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(((EntityInsentient) nmsEntity).targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(((EntityInsentient) nmsEntity).goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(((EntityInsentient) nmsEntity).targetSelector, new UnsafeList<PathfinderGoalSelector>());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
