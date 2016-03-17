package be.isach.ultracosmetics.cosmetics.mounts.customentities.v1_9_R1;

import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.util.BlockUtils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;

import java.lang.reflect.Field;

/**
 * Created by Sacha on 17/10/15.
 */
public class CustomSlime_1_9_R1 extends EntitySlime {

    boolean isOnGround;

    public CustomSlime_1_9_R1(World world) {
        super(world);

        if (!Mount.customEntities.contains(this)) return;

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
     * @param sideMot
     * @param forMot
     */
    @Override
    public void g(float sideMot, float forMot) {
        if (getSize() != 3)
            setSize(3);
        if (this.passenger != null && this.passenger instanceof EntityHuman
                && Mount.customEntities.contains(this)) {
            this.lastYaw = this.yaw = this.passenger.yaw;
            this.pitch = this.passenger.pitch * 0.5F;
            this.setYawPitch(this.yaw, this.pitch);//Update the pitch and yaw
            this.aI = this.aG = this.yaw;
            sideMot = ((EntityLiving) this.passenger).aZ * 0.5F;
            forMot = ((EntityLiving) this.passenger).ba;

            Field jump = null; //Jumping
            try {
                jump = EntityLiving.class.getDeclaredField("aY");
            } catch (NoSuchFieldException | SecurityException e1) {
                e1.printStackTrace();
            }
            jump.setAccessible(true);

            if (jump != null && BlockUtils.isOnGround(this.getBukkitEntity())) {
                try {
                    if (jump.getBoolean(this.passenger)) {
                        double jumpHeight = 0.3D;
                        this.motY = jumpHeight;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            this.S = 1.0F;
            this.aK = this.yaw;
            if (!this.world.isClientSide) {
                this.k(0.2f);

                if (bM()) {
                    if (V()) {
                        double d0 = locY;
                        float f3 = 0.8F;
                        float f4 = 0.02F;
                        float f2 = EnchantmentManager.b(this);
                        if (f2 > 3.0F) {
                            f2 = 3.0F;
                        }

                        if (f2 > 0.0F) {
                            f3 += (0.5460001F - f3) * f2 / 3.0F;
                            f4 += (bI() * 1.0F - f4) * f2 / 3.0F;
                        }

                        a(sideMot, forMot, f4);
                        move(motX, motY, motZ);
                        motX *= f3;
                        motY *= 0.800000011920929D;
                        motZ *= f3;
                        motY -= 0.02D;
                        if ((positionChanged) && (c(motX, motY + 0.6000000238418579D - locY + d0, motZ)))
                            motY = 0.300000011920929D;
                    } else if (ab()) {
                        double d0 = locY;
                        a(sideMot, forMot, 0.02F);
                        move(motX, motY, motZ);
                        motX *= 0.5D;
                        motY *= 0.5D;
                        motZ *= 0.5D;
                        motY -= 0.02D;
                        if ((positionChanged) && (c(motX, motY + 0.6000000238418579D - locY + d0, motZ)))
                            motY = 0.300000011920929D;
                    } else {
                        float f5 = world.getType(new BlockPosition(MathHelper.floor(locX), MathHelper.floor(getBoundingBox().b) - 1, MathHelper.floor(locZ))).getBlock().frictionFactor * 0.91F;

                        float f6 = 0.1627714F / (f5 * f5 * f5);
                        float f3 = bI() * f6;

                        a(sideMot, forMot, f3);
                        f5 = world.getType(new BlockPosition(MathHelper.floor(locX), MathHelper.floor(getBoundingBox().b) - 1, MathHelper.floor(locZ))).getBlock().frictionFactor * 0.91F;

                        if (k_()) {
                            float f4 = 0.15F;
                            motX = MathHelper.a(motX, -f4, f4);
                            motZ = MathHelper.a(motZ, -f4, f4);
                            fallDistance = 0.0F;
                            if (motY < -0.15D) {
                                motY = -0.15D;
                            }

                            if (motY < 0.0D) {
                                motY = 0.0D;
                            }
                        }

                        move(motX, motY, motZ);
                        if ((positionChanged) && (k_())) {
                            motY = 0.2D;
                        }

                        if ((world.isClientSide) && ((!world.isLoaded(new BlockPosition((int) locX, 0, (int) locZ))) || (!world.getChunkAtWorldCoords(new BlockPosition((int) locX, 0, (int) locZ)).o()))) {
                            if (locY > 0.0D)
                                motY = -0.1D;
                            else
                                motY = 0.0D;
                        } else {
                            motY += 0D;
                        }

                        motY *= 0.9800000190734863D;
                        motX *= f5;
                        motZ *= f5;
                    }
                }

                ay = az;
                double d0 = locX - lastX;
                double d1 = locZ - lastZ;

                float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
                if (f2 > 1.0F) {
                    f2 = 1.0F;
                }

                az += (f2 - az) * 0.4F;
                aA += az;

                super.g(sideMot, forMot);
            }


            this.ay = this.az;
            double d0 = this.locX - this.lastX;
            double d1 = this.locZ - this.lastZ;
            float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
            if (f4 > 1.0F) {
                f4 = 1.0F;
            }

            this.az += (f4 - this.az) * 0.4F;
            this.aA += this.az;
        } else {
            this.S = 0.5F;
            this.aK = 0.02F;
            super.g(sideMot, forMot);
        }


    }
}
