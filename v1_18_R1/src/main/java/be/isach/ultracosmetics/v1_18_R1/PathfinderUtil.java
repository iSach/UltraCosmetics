package be.isach.ultracosmetics.v1_18_R1;

import be.isach.ultracosmetics.version.IPathfinderUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

/**
 * @author iSach
 * <p>
 * Thanks to jojokobi (https://www.spigotmc.org/threads/clearing-pathfinder-goals-in-minecraft-1-14.371282/)
 * for the fix in 1.14
 */
public class PathfinderUtil implements IPathfinderUtil {
	// copied from be.isach.ultracosmetics.v1_17_R1.EntityUtil#clearPathfinders
	// TODO: either remove this class or remove the method from EntityUtil so we don't have duplicate code
	private static Field memoriesField;
	private static Field sensorsField;
	private static Field cField;
	private static Field fField;
	static {
		try {
			// corresponds to net.minecraft.world.entity.ai.Brain#memories
			memoriesField = Brain.class.getDeclaredField("d");
			memoriesField.setAccessible(true);
			
			// corresponds to net.minecraft.world.entity.ai.Brain#sensors
            sensorsField = Brain.class.getDeclaredField("e");
            sensorsField.setAccessible(true);
            
            // corresponds to net.minecraft.world.entity.ai.goal.GoalSelector#lockedFlags
            cField = GoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            
            // corresponds to net.minecraft.world.entity.ai.goal.GoalSelector#disabledFlags
            fField = GoalSelector.class.getDeclaredField("f");
            fField.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    @Override
    public void removePathFinders(Entity entity) {
    	Mob nmsEntity = (Mob) ((CraftEntity) entity).getHandle();
        GoalSelector goalSelector = nmsEntity.goalSelector;
        GoalSelector targetSelector = nmsEntity.targetSelector;

        Brain<?> brain = ((LivingEntity)nmsEntity).getBrain();

        try {
            memoriesField.set(brain, new HashMap<>());
            sensorsField.set(brain, new LinkedHashMap<>());

            // this method is annotated with VisibleForTesting but it seems like the easiest thing to do at the moment
            // this clears net.minecraft.world.entity.ai.Brain#availableBehaviorsByPriority
            brain.removeAllBehaviors();
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
        	// this is also annotated VisibleForTesting
        	// this clears net.minecraft.world.entity.ai.goal.GoalSelector#availableGoals
            goalSelector.removeAllGoals();
            targetSelector.removeAllGoals();

            // I'm  not sure what this line is supposed to do? it's just repeated
            //dField.set(goalSelector, new LinkedHashSet<>());
            cField.set(targetSelector, new EnumMap<Goal.Flag,WrappedGoal>(Goal.Flag.class));

            //dField.set(goalSelector, new LinkedHashSet<>());
            fField.set(targetSelector, EnumSet.noneOf(Goal.Flag.class));
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
