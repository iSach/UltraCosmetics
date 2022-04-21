package be.isach.ultracosmetics.v1_18_R2.pathfinders;


import be.isach.ultracosmetics.v1_18_R2.ObfuscatedFields;

import java.lang.reflect.Method;
import java.util.EnumSet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

/**
 * @author RadBuilder
 */
public class CustomPathFinderGoalPanic extends Goal {

    // NMS Entity
    private PathfinderMob entity;

    public CustomPathFinderGoalPanic(PathfinderMob entitycreature) {
        this.entity = entitycreature;
        EnumSet<Flag> set = EnumSet.noneOf(Goal.Flag.class);
        set.add(Goal.Flag.MOVE);
        this.setFlags(set);
    }

    @Override
    public boolean canUse() {
        return LandRandomPos.getPos(this.entity, 5, 4) != null;
    }

    @Override
    public void start() {
        Vec3 vec3d = LandRandomPos.getPos(this.entity, 5, 4);
        if (vec3d == null) return; // IN AIR
        this.entity.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 3.0d);
    }

    @Override
    public boolean canContinueToUse() {
        // CraftBukkit start - introduce a temporary timeout hack until this is fixed properly
        if ((this.entity.tickCount - this.entity.lastHurtByMobTimestamp) > 100) {
            this.entity.setLastHurtByMob((LivingEntity) null);
            return false;
        }
        // CraftBukkit end
        // Call by reflection (protected...)
        Method inLiquidMethod = null;
        boolean inLiquid = false;
        try {
            inLiquidMethod = this.entity.getNavigation().getClass().getSuperclass().getDeclaredMethod(ObfuscatedFields.IS_IN_LIQUID);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        inLiquidMethod.setAccessible(true);

        try {
            inLiquid = (boolean) inLiquidMethod.invoke((this.entity.getNavigation()));
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }

        Vec3 vec3d = LandRandomPos.getPos(this.entity, 5, 4);
        if (vec3d != null) {
            this.entity.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 3);
        }
        return !inLiquid;
    }


}
