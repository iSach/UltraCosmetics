package be.isach.ultracosmetics.v1_12_R1.customentities;

import be.isach.ultracosmetics.v1_12_R1.EntityBase;
import be.isach.ultracosmetics.v1_12_R1.nms.EntityWrapper;

import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.server.v1_12_R1.BiomeBase;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityGuardian;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.EntitySlime;
import net.minecraft.server.v1_12_R1.EntitySpider;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.EntityZombie;
import net.minecraft.server.v1_12_R1.MathHelper;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.RegistryID;
import net.minecraft.server.v1_12_R1.RegistryMaterials;

/**
 * @author RadBuilder
 */
public enum CustomEntities {
    PUMPLING("Pumpling", EntityType.ZOMBIE, EntityZombie.class, Pumpling.class),
    SLIME("CustomSlime", EntityType.SLIME, EntitySlime.class, CustomSlime.class),
    RIDEABLE_SPIDER("RideableSpider", EntityType.SPIDER, EntitySpider.class, RideableSpider.class),
    CUSTOM_GUARDIAN("CustomGuardian", EntityType.GHAST, EntityGuardian.class, CustomGuardian.class);

    private static final Set<Entity> customEntities = new HashSet<>();

    private final short id;
    private final MinecraftKey minecraftKey;
    private final Class<? extends EntityInsentient> nmsClass;
    private final Class<? extends EntityInsentient> customClass;

    @SuppressWarnings("deprecation")
    private CustomEntities(String name, EntityType entityType, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass) {
        this.id = entityType.getTypeId();
        this.minecraftKey = new MinecraftKey(name);
        this.nmsClass = nmsClass;
        this.customClass = customClass;
    }

    @SuppressWarnings("unchecked")
    public static void registerEntities() {
        for (CustomEntities entity : values()) {
            try {
                // Use reflection to get the RegistryID of entities.
                RegistryID<Class<? extends Entity>> registryID = (RegistryID<Class<? extends Entity>>) getPrivateField(RegistryMaterials.class, EntityTypes.b, "a");
                Object[] idToClassMap = (Object[]) getPrivateField(RegistryID.class, registryID, "d");

                // Save the the ID -> entity class mapping before the registration.
                Object oldValue = idToClassMap[entity.id];

                // Register the entity class.
                registryID.a(entity.customClass, entity.id);

                // Restore the ID -> entity class mapping.
                idToClassMap[entity.id] = oldValue;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (BiomeBase biomeBase : (Iterable<BiomeBase>) BiomeBase.i) {
            if (biomeBase == null)
                break;
            for (String field : new String[]{"t", "u", "v", "w"})
                try {
                    Field list = BiomeBase.class.getDeclaredField(field);
                    list.setAccessible(true);
                    List<BiomeBase.BiomeMeta> mobList = (List<BiomeBase.BiomeMeta>) list.get(biomeBase);

                    for (BiomeBase.BiomeMeta meta : mobList)
                        for (CustomEntities entity : values())
                            if (entity.nmsClass.equals(meta.b))
                                meta.b = entity.customClass;
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    public static void unregisterEntities() {
        for (CustomEntities entity : values()) {
            try {
                EntityTypes.b.a(entity.id, entity.minecraftKey, entity.nmsClass);
            } catch (Exception exc) {
                // ignore temporarily... TODO fix NMS problems... I hate Mojang
            }
        }
    }

    public static Object getPrivateField(Class<?> clazz, Object handle, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(handle);
    }

    public static void ride(float sideMot, float forMot, EntityHuman passenger, EntityInsentient entity) {
        if (!(entity instanceof EntityBase))
            throw new IllegalArgumentException("The entity field should implements EntityBase");

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

            if (forMot <= 0.0F)
                forMot *= 0.25F;

            wEntity.setJumping(wPassenger.isJumping());

            if (wPassenger.isJumping() && entity.onGround) {
                entity.motY = 0.4D;

                float f2 = MathHelper.sin(entity.yaw * 0.017453292f);
                float f3 = MathHelper.cos(entity.yaw * 0.017453292f);
                entity.motX += -0.4f * f2;
                entity.motZ += 0.4f * f3;
            }

            wEntity.setStepHeight(1.0f);
            wEntity.setJumpMovementFactor(wEntity.getMoveSpeed() * 0.1f);

            wEntity.setRotationYawHead(entity.yaw);

            entityBase.g_(sideMot, forMot);

            wEntity.setPrevLimbSwingAmount(wEntity.getLimbSwingAmount());

            double dx = entity.locX - entity.lastX;
            double dz = entity.locZ - entity.lastZ;

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
