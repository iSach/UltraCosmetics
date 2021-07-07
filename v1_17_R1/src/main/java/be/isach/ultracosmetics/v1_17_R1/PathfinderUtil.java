package be.isach.ultracosmetics.v1_17_R1;

import be.isach.ultracosmetics.version.IPathfinderUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
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
        Mob nmsEntity = (Mob) ((CraftEntity) entity).getHandle();
        GoalSelector goalSelector = nmsEntity.goalSelector;
        GoalSelector targetSelector = nmsEntity.targetSelector;

        Brain<?> brain = ((LivingEntity)nmsEntity).getBrain();
        
        try {
        	// these first two are identical in Spigot and Mojang mappings
            Field memoriesField = Brain.class.getDeclaredField("memories");
            memoriesField.setAccessible(true);
            memoriesField.set(brain, new HashMap<>());

            Field sensorsField = Brain.class.getDeclaredField("sensors");
            sensorsField.setAccessible(true);
            sensorsField.set(brain, new LinkedHashMap<>());

            // this method is annotated with VisibleForTesting but I'm not sure what else we can do here
            // this clears net.minecraft.world.entity.ai.Brain#availableBehaviorsByPriority
            brain.removeAllBehaviors();
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
        	// this is also annotated VisibleForTesting
        	// this clears net.minecraft.world.entity.ai.goal.GoalSelector#availableGoals
            goalSelector.removeAllGoals();
            targetSelector.removeAllGoals();

            Field cField;
            // corresponds to net.minecraft.world.entity.ai.goal.GoalSelector#lockedFlags
            cField = GoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            // I'm  not sure what this line is supposed to do? it's just repeated
            //dField.set(goalSelector, new LinkedHashSet<>());
            cField.set(targetSelector, new EnumMap<Goal.Flag,WrappedGoal>(Goal.Flag.class));

            Field fField;
            // corresponds to net.minecraft.world.entity.ai.goal.GoalSelector#disabledFlags
            fField = GoalSelector.class.getDeclaredField("f");
            fField.setAccessible(true);
            //dField.set(goalSelector, new LinkedHashSet<>());
            fField.set(targetSelector, EnumSet.noneOf(Goal.Flag.class));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
