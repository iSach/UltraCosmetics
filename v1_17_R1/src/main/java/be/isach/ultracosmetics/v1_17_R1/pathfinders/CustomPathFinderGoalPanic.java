package be.isach.ultracosmetics.v1_17_R1.pathfinders;


import java.lang.reflect.InvocationTargetException;
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

    // speed
    protected double speed;
    // NMS Entity
    private PathfinderMob entity;
    // random PosX
    private double posX;

    // random PosY
    private double posY;

    // random PosZ
    private double posZ;

    public CustomPathFinderGoalPanic(PathfinderMob entitycreature, double d0) {
        this.entity = entitycreature;
        this.speed = d0;
        EnumSet<Flag> set = EnumSet.noneOf(Goal.Flag.class);
        set.add(Goal.Flag.MOVE);
        this.setFlags(set);
    }

    @Override
    public boolean canUse() {
        Vec3 vec3d = LandRandomPos.getPos(this.entity, 5, 4);
        if (vec3d == null) return false;
        this.posX = vec3d.x;
        this.posY = vec3d.y;
        this.posZ = vec3d.z;
        return true;
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
        Method method = null;
        boolean boo = false;
        try {
            // corresponds to net.minecraft.world.entity.ai.navigation.PathNavigation#isInLiquid()
            method = this.entity.getNavigation().getClass().getSuperclass().getDeclaredMethod("isInLiquid");
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        method.setAccessible(true);

        try {
            boo = (Boolean) method.invoke((this.entity.getNavigation()));
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }

        Vec3 vec3d = LandRandomPos.getPos(this.entity, 5, 4);
        if (vec3d != null) {
            this.entity.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 3);
        }
        return !boo;
    }


}
