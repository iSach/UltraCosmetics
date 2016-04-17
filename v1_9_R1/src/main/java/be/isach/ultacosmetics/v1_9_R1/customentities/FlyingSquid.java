package be.isach.ultacosmetics.v1_9_R1.customentities;

import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.craftbukkit.v1_9_R1.util.UnsafeList;

import java.lang.reflect.Field;

/**
 * Custom Squid class.
 * <p/>
 * Created by Sacha on 11/10/15.
 */
public class FlyingSquid extends EntitySquid implements IMountCustomEntity {

    boolean canFly = true;

    public FlyingSquid(World world) {
        super(world);

        if (!CustomEntities.customEntities.contains(this)) return;
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
        EntityHuman passenger = null;
        for (Entity pass : bu())
            if (pass instanceof EntityHuman)
                passenger = (EntityHuman)pass;

        if(passenger != null) {
            this.lastYaw = this.yaw = passenger.yaw % 360f;
            this.pitch = (passenger.pitch * 0.5F) % 360f;

            this.aM = this.aK = this.yaw;
            sideMot = passenger.bd * 0.25f;
            forMot = passenger.be * 0.5f;
            if(forMot <= 0.0F) {
                forMot *= 0.25F;
            }

            try {
                Field jump = EntityLiving.class.getDeclaredField("bc");
                jump.setAccessible(true);

                if (jump != null) {
                    try {
                        if (jump.getBoolean(passenger) && (this.onGround || canFly)) {
                            this.motY = 0.4D;

                            float f2 = MathHelper.sin(this.yaw * 0.017453292F);
                            float f3 = MathHelper.cos(this.yaw * 0.017453292F);
                            this.motX += (double)(-0.4F * f2);
                            this.motZ += (double)(0.4F * f3);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } catch (NoSuchFieldException | SecurityException e1) {
                e1.printStackTrace();
            }

            this.P = 1.0F;
            this.aO = this.yaw;

            if(this.bx()) {
                this.l(0.35f);
                super.g(sideMot, forMot);
            }

            this.aB = this.aC;
            double d0 = this.locX - this.lastX;
            double d1 = this.locZ - this.lastZ;
            float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
            if(f4 > 1.0F) {
                f4 = 1.0F;
            }

            this.aC += (f4 - this.aC) * 0.4F;
            this.aE += this.aC;
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
