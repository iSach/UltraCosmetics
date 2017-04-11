package be.isach.ultracosmetics.v1_11_R1.pathfinders;

import net.minecraft.server.v1_11_R1.*;

/**
 * @author RadBuilder
 */
public class CustomPathFinderGoalPanic extends PathfinderGoal {

    // NMS Entity
    private EntityCreature b;

    // speed
    protected double a;

    // random PosX
    private double c;

    // random PosY
    private double d;

    // random PosZ
    private double e;

    public CustomPathFinderGoalPanic(EntityCreature entitycreature, double d0) {
        this.b = entitycreature;
        this.a = d0;
        this.a(1);
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
        this.b.getNavigation().a(vec3d.x, vec3d.y, vec3d.z, 3);
    }

    @Override
    public boolean b() {
        // CraftBukkit start - introduce a temporary timeout hack until this is fixed properly
        if ((this.b.ticksLived - this.b.hurtTimestamp) > 100) {
            this.b.b((EntityLiving) null);
            return false;
        }
        // CraftBukkit end
        return !this.b.getNavigation().n();
    }


}
