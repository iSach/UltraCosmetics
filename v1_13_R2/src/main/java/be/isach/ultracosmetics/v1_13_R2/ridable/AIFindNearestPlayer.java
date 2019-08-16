package be.isach.ultracosmetics.v1_13_R2.ridable;

import net.minecraft.server.v1_13_R2.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author BillyGalbreath
 * <p>
 * Author of plugin: "Ridables"
 * Thanks for authorizing using Ridables code to make UC work!
 */
public class AIFindNearestPlayer extends PathfinderGoal {
    private static final Logger LOGGER = LogManager.getLogger();
    private final RidableEntity ridable;
    private final EntityInsentient entity;
    private final Predicate<Entity> predicate;
    private final PathfinderGoalNearestAttackableTarget.DistanceComparator sorter;
    private EntityLiving target;

    public AIFindNearestPlayer(RidableEntity ridable) {
        this.ridable = ridable;
        this.entity = (EntityInsentient) ridable;
        if (entity instanceof EntityCreature) {
            LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinderMob mobs!");
        }
        sorter = new PathfinderGoalNearestAttackableTarget.DistanceComparator(entity);
        predicate = (target) -> {
            if (!(target instanceof EntityHuman)) {
                return false;
            } else if (((EntityHuman) target).abilities.isInvulnerable) {
                return false;
            } else {
                double range = maxTargetRange();
                if (target.isSneaking()) {
                    range *= 0.8F;
                }
                if (target.isInvisible()) {
                    float f = ((EntityHuman) target).dk(); // getArmorVisibility
                    if (f < 0.1F) {
                        f = 0.1F;
                    }
                    range *= 0.7F * f;
                }
                return (double) target.g(entity) <= range && PathfinderGoalTarget.a(entity, (EntityLiving) target, false, true);
            }
        };
    }

    // shouldExecute
    @Override
    public boolean a() {
        if (ridable.getRider() != null) {
            return false;
        }
        double range = maxTargetRange();
        List<EntityHuman> list = entity.world.a(EntityHuman.class, entity.getBoundingBox().grow(range, 4.0D, range), predicate);
        if (list.isEmpty()) {
            return false;
        }
        list.sort(sorter);
        target = list.get(0);
        return true;
    }

    // shouldContinueExecuting
    @Override
    public boolean b() {
        if (ridable.getRider() != null) {
            return false;
        }
        EntityLiving target = entity.getGoalTarget();
        if (target == null || !target.isAlive() || (target instanceof EntityHuman && ((EntityHuman) target).abilities.isInvulnerable)) {
            return false;
        }
       /*ScoreboardTeamBase team = entity.getScoreboardTeam();
        if (team != null && team == target.getScoreboardTeam()) {
            return false;
        }*/
        double range = maxTargetRange();
        return entity.h(target) <= range * range && (!(target instanceof EntityPlayer) || !((EntityPlayer) target).playerInteractManager.isCreative());
    }

    // startExecuting
    @Override
    public void c() {
        entity.setGoalTarget(target, EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true);
        super.c();
    }

    // resetTask
    @Override
    public void d() {
        entity.setGoalTarget(null);
        super.c();
    }

    private double maxTargetRange() {
        AttributeInstance range = this.entity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
        return range == null ? 16.0D : range.getValue();
    }
}
