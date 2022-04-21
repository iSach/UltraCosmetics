package be.isach.ultracosmetics.v1_8_R3.pathfinders;

import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import net.minecraft.server.v1_8_R3.RandomPositionGenerator;
import net.minecraft.server.v1_8_R3.Vec3D;

/**
 * Created by sacha on 25/07/15.
 */
public class CustomPathFinderGoalPanic extends PathfinderGoal {

    // NMS Entity
    private EntityCreature b;

    // speed
    protected double a;

    public CustomPathFinderGoalPanic(EntityCreature entitycreature, double d0) {
        this.b = entitycreature;
        this.a = d0;
        this.a(1);
    }

    @Override
    public boolean a() {
        return true;
    }

    @Override
    public void c() {
        Vec3D vec3d = RandomPositionGenerator.a(this.b, 5, 4);
        if (vec3d == null) return;

        this.b.getNavigation().a(vec3d.a, vec3d.b, vec3d.c, this.a);
    }

    @Override
    public boolean b() {
        // CraftBukkit start - introduce a temporary timeout hack until this is fixed properly
        if ((this.b.ticksLived - this.b.hurtTimestamp) > 100) {
            this.b.b((EntityLiving) null);
            return false;
        }
        // CraftBukkit end
        return !this.b.getNavigation().m();
    }


}
