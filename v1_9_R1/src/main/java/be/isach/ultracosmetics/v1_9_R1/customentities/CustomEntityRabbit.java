package be.isach.ultracosmetics.v1_9_R1.customentities;

import be.isach.ultracosmetics.v1_9_R1.EntityBase;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityRabbit;
import net.minecraft.server.v1_9_R1.MethodProfiler;
import net.minecraft.server.v1_9_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_9_R1.World;

public class CustomEntityRabbit extends EntityRabbit implements EntityBase {

    public CustomEntityRabbit(World world) {
        super(world);
        goalSelector = new PathfinderGoalSelector(new MethodProfiler());
        targetSelector = new PathfinderGoalSelector(new MethodProfiler());
    }

    @Override
    public void g(float sideMot, float forMot) {
        EntityHuman passenger = (EntityHuman) bv().stream().filter(entity -> entity instanceof EntityHuman).findFirst().orElseGet(() -> null);
        EntityBase.ride(sideMot, forMot, passenger, this);
    }

    @Override
    public void g_(float sideMot, float forMot) { super.g(sideMot, forMot); }
    @Override
    public float getSpeed() { return 1; }
    @Override
    public boolean canFly() { return false; }

}
