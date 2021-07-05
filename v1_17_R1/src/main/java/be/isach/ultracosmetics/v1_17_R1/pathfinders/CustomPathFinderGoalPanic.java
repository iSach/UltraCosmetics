package be.isach.ultracosmetics.v1_17_R1.pathfinders;


import net.minecraft.server.v1_16_R3.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;

/**
 * @author RadBuilder
 */
public class CustomPathFinderGoalPanic extends PathfinderGoal {

    // speed
    protected double a;
    // NMS Entity
    private EntityCreature b;
    // random PosX
    private double c;

    // random PosY
    private double d;

    // random PosZ
    private double e;

    public CustomPathFinderGoalPanic(EntityCreature entitycreature, double d0) {
        this.b = entitycreature;
        this.a = d0;
        EnumSet<Type> set = EnumSet.noneOf(PathfinderGoal.Type.class);
        set.add(PathfinderGoal.Type.MOVE);
        this.a(set);
    }

    @Override
    public boolean a() {
        Vec3D vec3d = RandomPositionGenerator.a(this.b, 5, 4);
        if (vec3d == null) return false; //
        this.c = vec3d.x;
        this.d = vec3d.y;
        this.e = vec3d.z;
        return true;
    }

    @Override
    public void c() {
        Vec3D vec3d = RandomPositionGenerator.a(this.b, 5, 4);
        if (vec3d == null) return; // IN AIR
        this.b.getNavigation().a(vec3d.x, vec3d.y, vec3d.z, 3.0d);
    }

    @Override
    public boolean b() {
        // CraftBukkit start - introduce a temporary timeout hack until this is fixed properly
        if ((this.b.ticksLived - this.b.hurtTimestamp) > 100) {
            this.b.setLastDamager((EntityLiving) null);
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

        Vec3D vec3d = RandomPositionGenerator.a(this.b, 5, 4);
        if (vec3d != null) {
            this.b.getNavigation().a(vec3d.x, vec3d.y, vec3d.z, 3.0d);
        }

        return !boo;
    }


}
