package be.isach.ultracosmetics.v1_9_R2.customentities;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import be.isach.ultracosmetics.v1_9_R2.EntityBase;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R2.Overridden;
import org.bukkit.craftbukkit.v1_9_R2.util.UnsafeList;

import java.lang.reflect.Field;

/**
 * Created by Sacha on 18/10/15.
 */
public class RideableSpider extends EntitySpider implements IMountCustomEntity, EntityBase {

    boolean isOnGround;

    public RideableSpider(World world) {
        super(world);

        if (!CustomEntities.customEntities.contains(this)) return;

//        removeSelectors();
    }

    private void removeSelectors() {
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public void g(float sideMot, float forMot) {
        if (!CustomEntities.customEntities.contains(this)) return;
        EntityHuman passenger = (EntityHuman) bv().stream().filter(entity -> entity instanceof EntityHuman).findFirst().orElseGet(() -> null);
        EntityBase.ride(sideMot, forMot, passenger, this);
    }

    @Override
    public org.bukkit.entity.Entity getEntity() {
        return getBukkitEntity();
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
}
