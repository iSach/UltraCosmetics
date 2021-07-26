package be.isach.ultracosmetics.v1_17_R1.customentities;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;

import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
        String customName = "ultracosmetics";

        Map<String, Type<?>> types = (Map<String, Type<?>>) DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getWorldVersion())).findChoiceType(References.ENTITY).types();

        // Pumpling
        types.put("minecraft:" + customName, types.get("minecraft:zombie"));
        EntityType.Builder<Entity> a = EntityType.Builder.of(Pumpling::new, MobCategory.AMBIENT);
        typesLocA = Registry.register(Registry.ENTITY_TYPE, customName, a.build(customName));

        // Slime
        types.put("minecraft:" + customName, types.get("minecraft:slime"));
        EntityType.Builder<Entity> b = EntityType.Builder.of(CustomSlime::new, MobCategory.AMBIENT);
        typesLocB = Registry.register(Registry.ENTITY_TYPE, customName, b.build(customName));

        // Spider
        types.put("minecraft:" + customName, types.get("minecraft:spider"));
        EntityType.Builder<Entity> c = EntityType.Builder.of(RideableSpider::new, MobCategory.AMBIENT);
        typesLocC = Registry.register(Registry.ENTITY_TYPE, customName, c.build(customName));

        // Guardian
        types.put("minecraft:" + customName, types.get("minecraft:guardian"));
        EntityType.Builder<Entity> d = EntityType.Builder.of(CustomGuardian::new, MobCategory.AMBIENT);
        typesLocD = Registry.register(Registry.ENTITY_TYPE, customName, d.build(customName));

        /**for (CustomEntities entity : values()) {
         try {
         // Use reflection to get the RegistryID of entities.
         @SuppressWarnings("unchecked") RegistryID<EntityTypes < ?>> registryID = (RegistryID<EntityTypes<?>>) getPrivateField(RegistryMaterials.class, IRegistry.ENTITY_TYPE, "b");
         Object[] idToClassMap = (Object[]) getPrivateField(RegistryID.class, registryID, "d");

         // Save the the ID -> entity class mapping before the registration.
         Object oldValue = idToClassMap[entity.getID()];

         // Register the entity class.
         //registryID.a(new EntityTypes<Entity>(entity.getCustomClass(), world -> null, true, true, null), entity.getID());
         EntityTypes.b test = EntityTypes::a; // entity.getCustomClass ??? ^^^
         registryID.a(new EntityTypes<Entity>(test, EnumCreatureType.AMBIENT, true, true, false, null, new EntitySize(0.5f, 0.5f, true)), entity.getID());

         // Restore the ID -> entity class mapping.
         idToClassMap[entity.getID()] = oldValue;
         } catch (Exception e) {
         e.printStackTrace();
         }
         }*/


// Should no longer be needed with 1.13
//		for (BiomeBase biomeBase : (Iterable<BiomeBase>) BiomeBase.i) {
//			if (biomeBase == null)
//				break;
//			for (String field : new String[]{ "t", "u", "v", "w" })
//				try {
//					Field list = BiomeBase.class.getDeclaredField(field);
//					list.setAccessible(true);
//					@SuppressWarnings("unchecked") List<BiomeBase.BiomeMeta> mobList = (List<BiomeBase.BiomeMeta>) list
//							.get(biomeBase);
//
//					for (BiomeBase.BiomeMeta meta : mobList)
//						for (CustomEntities entity : values())
//							if (entity.getNMSClass().equals(meta.b))
//								meta.b = entity.getCustomClass();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//		}
    }

    public static void unregisterEntities() {
// Should no longer be needed with 1.13
//		for (CustomEntities entity : values()) {
//			try {
//				EntityTypes.b.a(entity.getID(), entity.getMinecraftKey(), entity.getNMSClass());
//			} catch (Exception exc) {
//				// ignore temporarily... TODO fix NMS problems... I hate Mojang
//			}
//		}
    }

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
