package be.isach.ultracosmetics.v1_18_R1.customentities;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;

import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author RadBuilder
 */
public enum CustomEntities {

    PUMPLING("pumpling", org.bukkit.entity.EntityType.ZOMBIE.getTypeId(), EntityType.ZOMBIE, Zombie.class, Pumpling.class),
    SLIME("customslime", org.bukkit.entity.EntityType.SLIME.getTypeId(), EntityType.SLIME, Slime.class, CustomSlime.class),
    RIDEABLE_SPIDER("rideablespider", org.bukkit.entity.EntityType.SPIDER.getTypeId(), EntityType.SPIDER, Spider.class, RideableSpider.class),
    CUSTOM_GUARDIAN("customguardian", org.bukkit.entity.EntityType.GUARDIAN.getTypeId(), EntityType.GHAST, Guardian.class, CustomGuardian.class);

    public static List<Entity> customEntities = new ArrayList<>();

    public static EntityType<Entity> typesLocA;
    public static EntityType<Entity> typesLocB;
    public static EntityType<Entity> typesLocC;
    public static EntityType<Entity> typesLocD;

    private String name;
    private int id;
    private EntityType entityType;
    private ResourceLocation minecraftKey;
    private Class<? extends Mob> nmsClass;
    private Class<? extends Entity> customClass;

    CustomEntities(String name, int id, EntityType entityType, Class<? extends Mob> nmsClass,
                   Class<? extends Entity> customClass) {
        this.name = name;
        this.id = id;
        this.entityType = entityType;
        this.minecraftKey = new ResourceLocation(name);
        this.nmsClass = nmsClass;
        this.customClass = customClass;
    }

    public static void registerEntities() {
        Map<String, Type<?>> types = (Map<String, Type<?>>) DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getWorldVersion())).findChoiceType(References.ENTITY).types();

        registerEntity("zombie", Pumpling::new, types);
        registerEntity("slime", CustomSlime::new, types);
        registerEntity("spider", RideableSpider::new, types);
        registerEntity("guardian", CustomGuardian::new, types);
    }
    
    private static void registerEntity(String type, EntityFactory customMob, Map<String,Type<?>> types) {
        String customName = "minecraft:ultracosmetics_" + type;
        types.put(customName, types.get("minecraft:" + type));
        EntityType.Builder<Entity> a = EntityType.Builder.of(customMob, MobCategory.AMBIENT);
        typesLocA = Registry.register(Registry.ENTITY_TYPE, customName, a.build(customName));
    }

    public static void unregisterEntities() {}

    public static Object getPrivateField(Class<?> clazz, Object handle, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(handle);
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

    public ResourceLocation getMinecraftKey() {
        return this.minecraftKey;
    }

    public Class<? extends Mob> getNMSClass() {
        return nmsClass;
    }

    public Class<? extends Entity> getCustomClass() {
        return customClass;
    }
}
