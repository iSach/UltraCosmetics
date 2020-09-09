package be.isach.ultracosmetics.v1_16_R2;

import be.isach.ultracosmetics.version.IPathfinderUtil;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftEntity;
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
            // Corresponds to net.minecraft.world.entity.EntityLiving#brain
            Field brField = EntityLiving.class.getDeclaredField("bg");
            brField.setAccessible(true);
            BehaviorController<?> controller = (BehaviorController<?>) brField.get(nmsEntity);

            // Corresponds to net.minecraft.world.entity.ai.Brain#memories
            Field memoriesField = BehaviorController.class.getDeclaredField("memories");
            memoriesField.setAccessible(true);
            memoriesField.set(controller, new HashMap<>());

            // Corresponds to net.minecraft.world.entity.ai.Brain#sensors
            Field sensorsField = BehaviorController.class.getDeclaredField("sensors");
            sensorsField.setAccessible(true);
            sensorsField.set(controller, new LinkedHashMap<>());

            // Corresponds to net.minecraft.world.entity.ai.Brain#availableBehaviorsByPriority
            Field cField = BehaviorController.class.getDeclaredField("e");
            cField.setAccessible(true);
            cField.set(controller, new TreeMap<>());
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            Field dField;
            // Corresponds to net.minecraft.world.entity.ai.goal.GoalSelector#availableGoals
            dField = PathfinderGoalSelector.class.getDeclaredField("d");
            dField.setAccessible(true);
            dField.set(goalSelector, new LinkedHashSet<>());
            dField.set(targetSelector, new LinkedHashSet<>());

            // Corresponds to net.minecraft.world.entity.ai.goal.GoalSelector#lockedFlags
            Field cField;
            cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            dField.set(goalSelector, new LinkedHashSet<>());
            cField.set(targetSelector, new EnumMap<>(PathfinderGoal.Type.class));

            // Corresponds to net.minecraft.world.entity.ai.goal.GoalSelector#disabledFlags
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
