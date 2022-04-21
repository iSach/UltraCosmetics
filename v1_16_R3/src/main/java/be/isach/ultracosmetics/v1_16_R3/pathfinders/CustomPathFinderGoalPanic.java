package be.isach.ultracosmetics.v1_16_R3.pathfinders;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;

import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import net.minecraft.server.v1_16_R3.RandomPositionGenerator;
import net.minecraft.server.v1_16_R3.Vec3D;

/**
 * @author RadBuilder
 */
public class CustomPathFinderGoalPanic extends PathfinderGoal {

    // NMS Entity
    private EntityCreature b;

    public CustomPathFinderGoalPanic(EntityCreature entitycreature) {
        this.b = entitycreature;
        EnumSet<Type> set = EnumSet.noneOf(PathfinderGoal.Type.class);
        set.add(PathfinderGoal.Type.MOVE);
        this.a(set);
    }

    @Override
    public boolean a() {
        return RandomPositionGenerator.a(this.b, 5, 4) != null;
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
