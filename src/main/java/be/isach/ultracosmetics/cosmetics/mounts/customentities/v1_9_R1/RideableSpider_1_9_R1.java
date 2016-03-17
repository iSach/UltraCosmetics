package be.isach.ultracosmetics.cosmetics.mounts.customentities.v1_9_R1;

import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.craftbukkit.v1_9_R1.util.UnsafeList;

import java.lang.reflect.Field;

/**
 * Created by Sacha on 18/10/15.
 */
public class RideableSpider_1_9_R1 extends EntitySpider implements IMountCustomEntity {

    boolean isOnGround;

    public RideableSpider_1_9_R1(World world) {
        super(world);

        if (!CustomEntities_1_9_R1.customEntities.contains(this)) return;

        removeSelectors();
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

    /**
     * WASD Control.
     *
     * @param sideMot
     * @param forMot
     */
    public void g(float sideMot, float forMot) {
        Entity passenger = null;

        for (Entity ent : bu())
            if (ent instanceof EntityHuman)
                passenger = ent;

        if (passenger != null && CustomEntities_1_9_R1.customEntities.contains(this)) {
            this.lastYaw = this.yaw = passenger.yaw;
            this.pitch = passenger.pitch * 0.5F;
            this.setYawPitch(this.yaw, this.pitch);
            this.aM = this.aK = this.yaw;
            sideMot = ((EntityLiving) passenger).bd * 0.25f;
            forMot = ((EntityLiving) passenger).be * 0.5f;
            if (forMot <= 0.0F) {
                forMot *= 0.25F;
            }

            Field jump = null;
            try {
                jump = EntityLiving.class.getDeclaredField("bc");
            } catch (NoSuchFieldException | SecurityException e1) {
                e1.printStackTrace();
            }
            jump.setAccessible(true);

            if (jump != null) {
                try {
                    if (jump.getBoolean(passenger) && this.onGround)
                        this.motY = 0.4D;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            this.P = 1.0F;
            this.aO = this.yaw;
            if (!this.world.isClientSide) {
                this.l(0.35f);
                super.g(sideMot, forMot);
            }


            this.aB = this.aC;
            double d0 = this.locX - this.lastX;
            double d1 = this.locZ - this.lastZ;
            float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
            if (f4 > 1.0F) {
                f4 = 1.0F;
            }

            this.aF += (f4 - this.aF) * 0.4F;
            this.aG += this.aF;
        } else {
            this.P = 0.5F;
            this.aO = 0.02F;
            super.g(sideMot, forMot);
        }

    }

    @Override
    public org.bukkit.entity.Entity getEntity() {
        return getBukkitEntity();
    }
}
