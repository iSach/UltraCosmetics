package be.isach.ultracosmetics.v1_8_R3.customentities;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityTypes;

import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum CustomEntities {
    PUMPLING("Pumpling", EntityType.ZOMBIE, Pumpling.class, Pumpling.class),
    SLIME("CustomSlime", EntityType.SLIME, CustomSlime.class, CustomSlime.class),
    RIDEABLE_SPIDER("RideableSpider", EntityType.SPIDER, RideableSpider.class, RideableSpider.class),
    CUSTOM_GUARDIAN("CustomGuardian", EntityType.GHAST, CustomGuardian.class, CustomGuardian.class);

    private String name;
    private short id;
    private Class<? extends EntityInsentient> nmsClass;
    private Class<? extends EntityInsentient> customClass;

    private static final Set<Entity> customEntities = new HashSet<>();

    @SuppressWarnings("deprecation")
    private CustomEntities(String name, EntityType entityType, Class<? extends EntityInsentient> nmsClass,
                   Class<? extends EntityInsentient> customClass) {
        this.name = name;
        this.id = entityType.getTypeId();
        this.nmsClass = nmsClass;
        this.customClass = customClass;
    }

    public static void registerEntities() {
        for (CustomEntities entity : values())
            a(entity.customClass, entity.name, entity.id);
    }

    public static void unregisterEntities() {
        for (CustomEntities entity : values()) {
            try {
                getEntityTypesMap("d").remove(entity.customClass);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                getEntityTypesMap("f").remove(entity.customClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (CustomEntities entity : values())
            try {
                a(entity.nmsClass, entity.name, entity.id);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @SuppressWarnings("unchecked")
    private static Map<Object,Object> getEntityTypesMap(String f) throws Exception {
        Field field = EntityTypes.class.getDeclaredField(f);
        field.setAccessible(true);
        return (Map<Object, Object>) field.get(null);
    }

    @SuppressWarnings({"rawtypes"})
    private static void a(Class paramClass, String paramString, int paramInt) {
        try {
            getEntityTypesMap("c").put(paramString, paramClass);
            getEntityTypesMap("d").put(paramClass, paramString);
            getEntityTypesMap("e").put(paramInt,
                    paramClass);
            getEntityTypesMap("f").put(paramClass,
                    paramInt);
            getEntityTypesMap("g").put(paramString,
                    paramInt);
        } catch (Exception ignored) {
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
