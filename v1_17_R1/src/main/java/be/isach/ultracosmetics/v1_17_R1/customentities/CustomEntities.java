package be.isach.ultracosmetics.v1_17_R1.customentities;

import be.isach.ultracosmetics.v1_17_R1.EntityBase;
import be.isach.ultracosmetics.v1_17_R1.nms.EntityWrapper;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * @author RadBuilder
 */
public class CustomEntities {
    private static final Set<Entity> customEntities = new HashSet<>();
    @SuppressWarnings("unchecked")
    public static void registerEntities() {
        String customName = "ultracosmetics";

        Map<String, Type<?>> types = (Map<String, Type<?>>) DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getWorldVersion())).findChoiceType(References.ENTITY).types();

        // Pumpling
        types.put("minecraft:" + customName, types.get("minecraft:zombie"));
        EntityType.Builder<Entity> a = EntityType.Builder.of(Pumpling::new, MobCategory.AMBIENT);
        Registry.register(Registry.ENTITY_TYPE, customName, a.build(customName));

        // Slime
        types.put("minecraft:" + customName, types.get("minecraft:slime"));
        EntityType.Builder<Entity> b = EntityType.Builder.of(CustomSlime::new, MobCategory.AMBIENT);
        Registry.register(Registry.ENTITY_TYPE, customName, b.build(customName));

        // Spider
        types.put("minecraft:" + customName, types.get("minecraft:spider"));
        EntityType.Builder<Entity> c = EntityType.Builder.of(RideableSpider::new, MobCategory.AMBIENT);
        Registry.register(Registry.ENTITY_TYPE, customName, c.build(customName));

        // Guardian
        types.put("minecraft:" + customName, types.get("minecraft:guardian"));
        EntityType.Builder<Entity> d = EntityType.Builder.of(CustomGuardian::new, MobCategory.AMBIENT);
        Registry.register(Registry.ENTITY_TYPE, customName, d.build(customName));
    }

    public static void ride(float sideMot, float forMot, Player passenger, Mob mob) {
        if (!(mob instanceof EntityBase)) {
            throw new IllegalArgumentException("The entity field should implements EntityBase");
        }

        EntityBase entityBase = (EntityBase) mob;
        Entity entity = mob;

        EntityWrapper wEntity = new EntityWrapper(mob);
        EntityWrapper wPassenger = new EntityWrapper(passenger);

        if (passenger != null) {
            entity.yRotO = ((Entity)passenger).getYRot() % 360f;
            entity.setYRot(entity.yRotO);
            entity.setXRot((((Entity)passenger).getXRot() * 0.5F) % 360f);

            wEntity.setRenderYawOffset(entity.getYRot());
            wEntity.setRotationYawHead(entity.getYRot());

            sideMot = wPassenger.getMoveStrafing() * 0.25f;
            forMot = wPassenger.getMoveForward() * 0.5f;

            if (forMot <= 0.0F) {
                forMot *= 0.25F;
            }

            wEntity.setJumping(wPassenger.isJumping());

            if (wPassenger.isJumping() && entity.isOnGround()) {
                Vec3 v = entity.getDeltaMovement();
                Vec3 v2 = new Vec3(v.x(), 0.4D, v.z());
                entity.setDeltaMovement(v2);

                /*float f2 = MathHelper.sin(entity.yaw * 0.017453292f);
                float f3 = MathHelper.cos(entity.yaw * 0.017453292f);
                entity.setMot(entity.getMot().add(-0.4f * f2, upMot, 0.4f * f3));*/
            }

            wEntity.setStepHeight(1.0f);
            wEntity.setJumpMovementFactor(wEntity.getMoveSpeed() * 0.1f);

            wEntity.setRotationYawHead(entity.getYRot());

            entityBase.g_(sideMot, forMot);


            wEntity.setPrevLimbSwingAmount(wEntity.getLimbSwingAmount());

            double dx = entity.getX() - entity.xo;
            double dz = entity.getZ() - entity.zo;

            float f4 = Mth.sqrt((float) (dx * dx + dz * dz)) * 4;

            if (f4 > 1)
                f4 = 1;

            wEntity.setLimbSwingAmount(wEntity.getLimbSwingAmount() + (f4 - wEntity.getLimbSwingAmount()) * 0.4f);
            wEntity.setLimbSwing(wEntity.getLimbSwing() + wEntity.getLimbSwingAmount());
        } else {
            wEntity.setStepHeight(0.5f);
            wEntity.setJumpMovementFactor(0.02f);

            entityBase.g_(sideMot, forMot);
        }
    }

    public static void addCustomEntity(Entity entity) {
        customEntities.add(entity);
    }

    public static boolean isCustomEntity(Entity entity) {
        return customEntities.contains(entity);
    }

    public static void removeCustomEntity(Entity entity) {
        customEntities.remove(entity);
    }
}
