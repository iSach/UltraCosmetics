package be.isach.ultracosmetics.cosmetics.mounts.customentities.v1_9_R1;

import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.craftbukkit.v1_9_R1.util.UnsafeList;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;

/**
 * Created by Sacha on 17/10/15.
 */
public class CustomSlime_1_9_R1 extends EntitySlime implements IMountCustomEntity {

    boolean canFly = false;

    public CustomSlime_1_9_R1(World world) {
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

    @Override
    public void g(float sideMot, float forMot) {
        if (this.passengers == null || this.passengers.size() == 0)
            this.die();
        for (net.minecraft.server.v1_9_R1.Entity e : passengers) {
            if (e instanceof EntityHuman) {

                EntityHuman passenger = (EntityHuman) e;

                this.lastYaw = this.yaw = passenger.yaw;
                this.pitch = passenger.pitch * 0.5F;
                this.setYawPitch(this.yaw, this.pitch);
                this.aI = this.aG = this.yaw;
                sideMot = ((EntityLiving) passenger).bd * 0.5F;
                forMot = ((EntityLiving) passenger).be;
                if (forMot <= 0.0F) {
                    forMot *= 0.25F;
                }

                Field jump = null;
                try {
                    jump = EntityLiving.class.getDeclaredField("bc");
                } catch (NoSuchFieldException e1) {
                    e1.printStackTrace();
                } catch (SecurityException e1) {
                    e1.printStackTrace();
                }
                jump.setAccessible(true);

                if (jump != null && this.onGround) {
                    try {
                        if (jump.getBoolean(passenger)) {
                            double jumpHeight = 1.5D;
                            this.motY = jumpHeight;
                        }
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    }
                }

                this.P = 1.0F;
                this.aN = this.ck() * 0.1F;
                if (!this.world.isClientSide) {
                    this.k((float) this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()); //Ou n'importe quel float, sa controle la vitesse de déplacement.
                    super.g(sideMot, forMot);
                }

                this.aB = this.az;//Some extra things
                double d0 = this.locX - this.lastX;
                double d1 = this.locZ - this.lastZ;
                float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
                if (f4 > 1.0F) {
                    f4 = 1.0F;
                }

                this.az += (f4 - this.az) * 0.4F;
                this.aE += this.az;
            } else {
                this.P = 0.5F;
                this.aK = 0.02F;
                super.g(sideMot, forMot);
                die();
            }
        }
    }

//    @Override
//    public void g(float sideMot, float forMot) {
//        EntityHuman passenger = (EntityHuman) bt();
//
//        if(passenger != null) {
//            this.lastYaw = this.yaw = passenger.yaw % 360f;
//            this.pitch = (passenger.pitch * 0.5F) % 360f;
//
//            this.aM = this.aK = this.yaw;
//            sideMot = passenger.bd * 0.25f;
//            forMot = passenger.be * 0.5f;
//            if(forMot <= 0.0F) {
//                forMot *= 0.25F;
//            }
//
//            try {
//                Field jump = EntityLiving.class.getDeclaredField("bc");
//                jump.setAccessible(true);
//
//                if (jump != null) {
//                    try {
//                        if (jump.getBoolean(passenger) && (this.onGround || canFly)) {
//                            this.motY = 0.4D;
//
//                            float f2 = MathHelper.sin(this.yaw * 0.017453292F);
//                            float f3 = MathHelper.cos(this.yaw * 0.017453292F);
//                            this.motX += (double)(-0.4F * f2);
//                            this.motZ += (double)(0.4F * f3);
//                        }
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } catch (NoSuchFieldException | SecurityException e1) {
//                e1.printStackTrace();
//            }
//
//            this.P = 1.0F;
//            this.aO = this.yaw;
//
//            if(this.bx()) {
//                this.l(0.35f);
//                super.g(sideMot, forMot);
//            }
//
//            this.aB = this.aC;
//            double d0 = this.locX - this.lastX;
//            double d1 = this.locZ - this.lastZ;
//            float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
//            if(f4 > 1.0F) {
//                f4 = 1.0F;
//            }
//
//            this.aC += (f4 - this.aC) * 0.4F;
//            this.aE += this.aC;
//        } else {
//            this.P = 0.5F;
//            this.aO = 0.02F;
//
//            super.g(sideMot, forMot);
//        }
//
//    }

    @Override
    public Entity getEntity() {
        return getBukkitEntity();
    }
}
