package be.isach.ultracosmetics.v1_17_R1.pathfinders;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

/**
 * @author RadBuilder
 */
public class CustomPathFinderGoalPanic extends Goal {

    // speed
    protected double a;
    // NMS Entity
    private PathfinderMob b;
    // random PosX
    private double c;

    // random PosY
    private double d;

    // random PosZ
    private double e;

    public CustomPathFinderGoalPanic(PathfinderMob entitycreature, double d0) {
        this.b = entitycreature;
        this.a = d0;
        EnumSet<Flag> set = EnumSet.noneOf(Goal.Flag.class);
        set.add(Goal.Flag.MOVE);
        this.setFlags(set);
    }

    @Override
    public boolean canUse() {
        Vec3 vec3d = LandRandomPos.getPos(this.b, 5, 4);
        if (vec3d == null) return false;
        this.c = vec3d.x;
        this.d = vec3d.y;
        this.e = vec3d.z;
        return true;
    }

    @Override
    public void start() {
        Vec3 vec3d = LandRandomPos.getPos(this.b, 5, 4);
        if (vec3d == null) return; // IN AIR
        this.b.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 3.0d);
    }

    @Override
    public boolean canContinueToUse() {
        // CraftBukkit start - introduce a temporary timeout hack until this is fixed properly
        if ((this.b.tickCount - this.b.lastHurtByMobTimestamp) > 100) {
            this.b.setLastHurtByMob((LivingEntity) null);
            return false;
        }
        // CraftBukkit end
        // Call by reflection (protected...)
        Method method = null;
        boolean boo = false;
        try {
            method = this.b.getNavigation().getClass().getSuperclass().getDeclaredMethod("p");
        } catch (Exception e) {
            e.printStackTrace();
        }

        method.setAccessible(true);

        try {
            boo = (Boolean) method.invoke((this.b.getNavigation()));
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }

        Vec3 vec3d = LandRandomPos.getPos(this.b, 5, 4);
        if (vec3d != null) {
            this.b.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 3);
        }
        return !boo;
    }


}
