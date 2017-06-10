package be.isach.ultracosmetics.v1_12_R1.customentities;

import net.minecraft.server.v1_12_R1.BiomeBase;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityGuardian;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.EntitySlime;
import net.minecraft.server.v1_12_R1.EntitySpider;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.EntityZombie;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.RegistryID;
import net.minecraft.server.v1_12_R1.RegistryMaterials;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RadBuilder
 */
public enum CustomEntities {
	PUMPLING("Pumpling", EntityType.ZOMBIE.getTypeId(), EntityType.ZOMBIE, EntityZombie.class, Pumpling.class),
	SLIME("CustomSlime", EntityType.SLIME.getTypeId(), EntityType.SLIME, EntitySlime.class, CustomSlime.class),
	RIDEABLE_SPIDER("RideableSpider", EntityType.SPIDER.getTypeId(), EntityType.SPIDER, EntitySpider.class, RideableSpider.class),
	CUSTOM_GUARDIAN("CustomGuardian", EntityType.GUARDIAN.getTypeId(), EntityType.GHAST, EntityGuardian.class, CustomGuardian.class);
	
	public static List<Entity> customEntities = new ArrayList<>();
	
	private String name;
	private int id;
	private EntityType entityType;
	private MinecraftKey minecraftKey;
	private Class<? extends EntityInsentient> nmsClass;
	private Class<? extends EntityInsentient> customClass;
	
	CustomEntities(String name, int id, EntityType entityType, Class<? extends EntityInsentient> nmsClass,
	               Class<? extends EntityInsentient> customClass) {
		this.name = name;
		this.id = id;
		this.entityType = entityType;
		this.minecraftKey = new MinecraftKey(name);
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
	
	public MinecraftKey getMinecraftKey() {
		return this.minecraftKey;
	}
	
	public Class<? extends EntityInsentient> getNMSClass() {
		return nmsClass;
	}
	
	public Class<? extends EntityInsentient> getCustomClass() {
		return customClass;
	}
	
	public static void registerEntities() {
		for (CustomEntities entity : values()) {
			try {
				// Use reflection to get the RegistryID of entities.
				@SuppressWarnings("unchecked") RegistryID<Class<? extends Entity>> registryID = (RegistryID<Class<? extends Entity>>) getPrivateField(RegistryMaterials.class, EntityTypes.b, "a");
				Object[] idToClassMap = (Object[]) getPrivateField(RegistryID.class, registryID, "d");
				
				// Save the the ID -> entity class mapping before the registration.
				Object oldValue = idToClassMap[entity.getID()];
				
				// Register the entity class.
				registryID.a(entity.getCustomClass(), entity.getID());
				
				// Restore the ID -> entity class mapping.
				idToClassMap[entity.getID()] = oldValue;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for (BiomeBase biomeBase : (Iterable<BiomeBase>) BiomeBase.i) {
			if (biomeBase == null)
				break;
			for (String field : new String[]{ "t", "u", "v", "w" })
				try {
					Field list = BiomeBase.class.getDeclaredField(field);
					list.setAccessible(true);
					@SuppressWarnings("unchecked") List<BiomeBase.BiomeMeta> mobList = (List<BiomeBase.BiomeMeta>) list
							.get(biomeBase);
					
					for (BiomeBase.BiomeMeta meta : mobList)
						for (CustomEntities entity : values())
							if (entity.getNMSClass().equals(meta.b))
								meta.b = entity.getCustomClass();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
	
	public static void unregisterEntities() {
		for (CustomEntities entity : values()) {
			try {
				EntityTypes.b.a(entity.getID(), entity.getMinecraftKey(), entity.getNMSClass());
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
}
