package be.isach.ultracosmetics.v1_12_R1.pathfinders;

import net.minecraft.server.v1_12_R1.EntityCreature;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.PathfinderGoal;
import net.minecraft.server.v1_12_R1.RandomPositionGenerator;
import net.minecraft.server.v1_12_R1.Vec3D;

/**
 * @author RadBuilder
 */
public class CustomPathFinderGoalPanic extends PathfinderGoal {

    // NMS Entity
    private EntityCreature b;

    public CustomPathFinderGoalPanic(EntityCreature entitycreature) {
        this.b = entitycreature;
        this.a(1);
    }

    @Override
    public boolean a() {
        return RandomPositionGenerator.a(this.b, 5, 4) != null;
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
        return !this.b.getNavigation().o();
    }
}
