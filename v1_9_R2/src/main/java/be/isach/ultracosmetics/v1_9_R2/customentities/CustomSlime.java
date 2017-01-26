package be.isach.ultracosmetics.v1_9_R2.customentities;

import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import be.isach.ultracosmetics.v1_9_R2.EntityBase;
import be.isach.ultracosmetics.v1_9_R2.nms.WrapperEntityHuman;
import be.isach.ultracosmetics.v1_9_R2.nms.WrapperEntityInsentient;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.craftbukkit.v1_9_R2.util.UnsafeList;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;

/**
 * Created by Sacha on 17/10/15.
 */
public class CustomSlime extends EntitySlime implements IMountCustomEntity, EntityBase {

    public CustomSlime(World world) {
        super(world);
    }

    @Override
    public void removeAi() {
        setAI(false);
    }

    @Override
    public void g(float sideMot, float forMot) {
        if (!CustomEntities.customEntities.contains(this)) {
            super.g(sideMot, forMot);
            return;
        }
        EntityHuman passenger = null;
        if (!bv().isEmpty()) {
            passenger = (EntityHuman) bv().get(0);
        }
        ride(sideMot, forMot, passenger, this);
    }

    @Override
    public void g_(float sideMot, float forMot) {
        super.g(sideMot, forMot);
    }

    @Override
    public float getSpeed() {
        return 1.75f;
    }

    @Override
    public boolean canFly() {
        return false;
    }

    @Override
    public Entity getEntity() {
        return getBukkitEntity();
    }
    static void ride(float sideMot, float forMot, EntityHuman passenger, EntityInsentient entity) {
        if(!(entity instanceof EntityBase))
            throw new IllegalArgumentException("The entity field should implements EntityBase");

        EntityBase entityBase = (EntityBase) entity;

        WrapperEntityInsentient wEntity = new WrapperEntityInsentient(entity);
        WrapperEntityHuman wPassenger = new WrapperEntityHuman(passenger);

        if(passenger != null) {
            entity.lastYaw = entity.yaw = passenger.yaw % 360f;
            entity.pitch = (passenger.pitch * 0.5F) % 360f;

            wEntity.setRenderYawOffset(entity.yaw);
            wEntity.setRotationYawHead(entity.yaw);

            sideMot = wPassenger.getMoveStrafing() * 0.25f;
            forMot = wPassenger.getMoveForward() * 0.5f;

            if(forMot <= 0.0F)
                forMot *= 0.25F;

            wEntity.setJumping(wPassenger.isJumping());

            if(wPassenger.isJumping() && (entity.onGround || entityBase.canFly())) {
                entity.motY = 0.4D;

                float f2 = MathHelper.sin(entity.yaw * 0.017453292f);
                float f3 = MathHelper.cos(entity.yaw * 0.017453292f);
                entity.motX += (double) (-0.4f * f2);
                entity.motZ += (double) (0.4f * f3);
            }

            wEntity.setStepHeight(1.0f);
            wEntity.setJumpMovementFactor(wEntity.getMoveSpeed() * 0.1f);

            wEntity.setRotationYawHead(entity.yaw);

            if(wEntity.canPassengerSteer()) {
                wEntity.setMoveSpeed(0.35f * entityBase.getSpeed());
                entityBase.g_(sideMot, forMot);
            }

            wEntity.setPrevLimbSwingAmount(wEntity.getLimbSwingAmount());

            double dx = entity.locX - entity.lastX;
            double dz = entity.locZ - entity.lastZ;

            float f4 = MathHelper.sqrt(dx * dx + dz * dz) * 4;

            if(f4 > 1)
                f4 = 1;

            wEntity.setLimbSwingAmount(wEntity.getLimbSwingAmount() + (f4 - wEntity.getLimbSwingAmount()) * 0.4f);
            wEntity.setLimbSwing(wEntity.getLimbSwing() + wEntity.getLimbSwingAmount());
        } else {
            wEntity.setStepHeight(0.5f);
            wEntity.setJumpMovementFactor(0.02f);

            entityBase.g_(sideMot, forMot);
        }

    }
}
