package be.isach.ultracosmetics.v1_16_R3.customentities;

import be.isach.ultracosmetics.v1_16_R3.EntityBase;
import be.isach.ultracosmetics.v1_16_R3.nms.EntityWrapper;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.server.v1_16_R3.DataConverterRegistry;
import net.minecraft.server.v1_16_R3.DataConverterTypes;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EnumCreatureType;
import net.minecraft.server.v1_16_R3.IRegistry;
import net.minecraft.server.v1_16_R3.MathHelper;
import net.minecraft.server.v1_16_R3.SharedConstants;
import net.minecraft.server.v1_16_R3.Vec3D;

/**
 * @author RadBuilder
 */
public class CustomEntities {
    private static final Set<Entity> customEntities = new HashSet<>();
    @SuppressWarnings("unchecked")
    public static void registerEntities() {
        String customName = "ultracosmetics";

        Map<String, Type<?>> types = (Map<String, Type<?>>) DataConverterRegistry.a().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion())).findChoiceType(DataConverterTypes.ENTITY).types();

        // Pumpling
        types.put("minecraft:" + customName, types.get("minecraft:zombie"));
        EntityTypes.Builder<Entity> a = EntityTypes.Builder.a(Pumpling::new, EnumCreatureType.AMBIENT);
        IRegistry.a(IRegistry.ENTITY_TYPE, customName, a.a(customName));

        // Slime
        types.put("minecraft:" + customName, types.get("minecraft:slime"));
        EntityTypes.Builder<Entity> b = EntityTypes.Builder.a(CustomSlime::new, EnumCreatureType.AMBIENT);
        IRegistry.a(IRegistry.ENTITY_TYPE, customName, b.a(customName));

        // Spider
        types.put("minecraft:" + customName, types.get("minecraft:spider"));
        EntityTypes.Builder<Entity> c = EntityTypes.Builder.a(RideableSpider::new, EnumCreatureType.AMBIENT);
        IRegistry.a(IRegistry.ENTITY_TYPE, customName, c.a(customName));

        // Guardian
        types.put("minecraft:" + customName, types.get("minecraft:guardian"));
        EntityTypes.Builder<Entity> d = EntityTypes.Builder.a(CustomGuardian::new, EnumCreatureType.AMBIENT);
        IRegistry.a(IRegistry.ENTITY_TYPE, customName, d.a(customName));
    }

    public static void unregisterEntities() {}

    public static Object getPrivateField(Class<?> clazz, Object handle, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(handle);
    }

    public static void ride(float sideMot, float forMot, EntityHuman passenger, EntityInsentient entity) {
        if (!(entity instanceof EntityBase)) {
            throw new IllegalArgumentException("The entity field should implements EntityBase");
        }

        EntityBase entityBase = (EntityBase) entity;

        EntityWrapper wEntity = new EntityWrapper(entity);
        EntityWrapper wPassenger = new EntityWrapper(passenger);

        if (passenger != null) {
            entity.lastYaw = entity.yaw = passenger.yaw % 360f;
            entity.pitch = (passenger.pitch * 0.5F) % 360f;

            wEntity.setRenderYawOffset(entity.yaw);
            wEntity.setRotationYawHead(entity.yaw);

            sideMot = wPassenger.getMoveStrafing() * 0.25f;
            forMot = wPassenger.getMoveForward() * 0.5f;

            if (forMot <= 0.0F) {
                forMot *= 0.25F;
            }

            wEntity.setJumping(wPassenger.isJumping());

            if (wPassenger.isJumping() && entity.isOnGround()) {
                Vec3D v = entity.getMot();
                Vec3D v2 = new Vec3D(v.getX(), 0.4D, v.getZ());
                entity.setMot(v2);
            }

            wEntity.setStepHeight(1.0f);
            wEntity.setJumpMovementFactor(wEntity.getMoveSpeed() * 0.1f);

            wEntity.setRotationYawHead(entity.yaw);

            entityBase.g_(sideMot, forMot);


            wEntity.setPrevLimbSwingAmount(wEntity.getLimbSwingAmount());

            double dx = entity.locX() - entity.lastX;
            double dz = entity.locZ() - entity.lastZ;

            float f4 = MathHelper.sqrt(dx * dx + dz * dz) * 4;

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
