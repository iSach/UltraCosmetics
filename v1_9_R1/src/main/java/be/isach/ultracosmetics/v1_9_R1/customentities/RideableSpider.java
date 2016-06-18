package be.isach.ultracosmetics.v1_9_R1.customentities;

import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import be.isach.ultracosmetics.v1_9_R1.EntityBase;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.craftbukkit.v1_9_R1.util.UnsafeList;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;

/**
 * Created by Sacha on 18/10/15.
 */
public class RideableSpider extends EntitySpider implements EntityBase, IMountCustomEntity {

    public RideableSpider(World world) {
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
    public void g_(float sideMot, float forMot) {
        super.g(sideMot, forMot);
    }

    @Override
    public float getSpeed() {
        return 1;
    }

    @Override
    public boolean canFly() {
        return false;
    }

    @Override
    public Entity getEntity() {
        return getBukkitEntity();
    }
}

