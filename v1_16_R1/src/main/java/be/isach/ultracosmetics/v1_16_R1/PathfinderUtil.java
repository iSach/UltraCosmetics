package be.isach.ultracosmetics.v1_16_R1;

import be.isach.ultracosmetics.version.IPathfinderUtil;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author iSach
 * <p>
 * Thanks to jojokobi (https://www.spigotmc.org/threads/clearing-pathfinder-goals-in-minecraft-1-14.371282/)
 * for the fix in 1.14
 */
public class PathfinderUtil implements IPathfinderUtil {


    @Override
    public void removePathFinders(Entity entity) {
        EntityInsentient nmsEntity = (EntityInsentient) ((CraftEntity) entity).getHandle();
        PathfinderGoalSelector goalSelector = nmsEntity.goalSelector;
        PathfinderGoalSelector targetSelector = nmsEntity.targetSelector;

        try {
            Field brField = EntityLiving.class.getDeclaredField("bo");
            brField.setAccessible(true);
            BehaviorController<?> controller = (BehaviorController<?>) brField.get(nmsEntity);

            Field memoriesField = BehaviorController.class.getDeclaredField("memories");
            memoriesField.setAccessible(true);
            memoriesField.set(controller, new HashMap<>());

            Field sensorsField = BehaviorController.class.getDeclaredField("sensors");
            sensorsField.setAccessible(true);
            sensorsField.set(controller, new LinkedHashMap<>());

            Field cField = BehaviorController.class.getDeclaredField("c");
            cField.setAccessible(true);
            cField.set(controller, new TreeMap<>());
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            Field dField;
            dField = PathfinderGoalSelector.class.getDeclaredField("d");
            dField.setAccessible(true);
            dField.set(goalSelector, new LinkedHashSet<>());
            dField.set(targetSelector, new LinkedHashSet<>());

            Field cField;
            cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            dField.set(goalSelector, new LinkedHashSet<>());
            cField.set(targetSelector, new EnumMap<>(PathfinderGoal.Type.class));

            Field fField;
            fField = PathfinderGoalSelector.class.getDeclaredField("f");
            fField.setAccessible(true);
            dField.set(goalSelector, new LinkedHashSet<>());
            fField.set(targetSelector, EnumSet.noneOf(PathfinderGoal.Type.class));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
