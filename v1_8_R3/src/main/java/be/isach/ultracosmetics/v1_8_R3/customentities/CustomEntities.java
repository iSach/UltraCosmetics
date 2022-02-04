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
    PUMPLING("Pumpling", EntityType.ZOMBIE.getTypeId(), EntityType.ZOMBIE, Pumpling.class, Pumpling.class),
    SLIME("CustomSlime", EntityType.SLIME.getTypeId(), EntityType.SLIME, CustomSlime.class, CustomSlime.class),
    RIDEABLE_SPIDER("RideableSpider", EntityType.SPIDER.getTypeId(), EntityType.SPIDER, RideableSpider.class, RideableSpider.class),
    CUSTOM_GUARDIAN("CustomGuardian", EntityType.GUARDIAN.getTypeId(), EntityType.GHAST, CustomGuardian.class, CustomGuardian.class);

    private String name;
    private int id;
    private EntityType entityType;
    private Class<? extends EntityInsentient> nmsClass;
    private Class<? extends EntityInsentient> customClass;

    // mounts
    public static final Set<Entity> customEntities = new HashSet<>();

    private CustomEntities(String name, int id, EntityType entityType,
                   Class<? extends EntityInsentient> nmsClass,
                   Class<? extends EntityInsentient> customClass) {
        this.name = name;
        this.id = id;
        this.entityType = entityType;
        this.nmsClass = nmsClass;
        this.customClass = customClass;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Class<? extends EntityInsentient> getNMSClass() {
        return nmsClass;
    }

    public Class<? extends EntityInsentient> getCustomClass() {
        return customClass;
    }

    public static void registerEntities() {
        for (CustomEntities entity : values())
            a(entity.getCustomClass(), entity.getName(), entity.getID());
    }

    public static void unregisterEntities() {
        for (CustomEntities entity : values()) {
            try {
                getEntityTypesMap("d").remove(entity.getCustomClass());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                getEntityTypesMap("f").remove(entity.getCustomClass());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (CustomEntities entity : values())
            try {
                a(entity.getNMSClass(), entity.getName(), entity.getID());
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
}
