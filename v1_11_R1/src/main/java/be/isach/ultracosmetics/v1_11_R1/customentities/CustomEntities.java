package be.isach.ultracosmetics.v1_11_R1.customentities;

import net.minecraft.server.v1_11_R1.BiomeBase;
import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityInsentient;
import net.minecraft.server.v1_11_R1.EntityTypes;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RadBuilder
 */
public enum CustomEntities {
	//    FLYING_SQUID("FlyingSquid", EntityType.SQUID.getTypeId(), EntityType.SQUID, FlyingSquid.class, FlyingSquid.class),
	PUMPLING("Pumpling", EntityType.ZOMBIE.getTypeId(), EntityType.ZOMBIE, Pumpling.class, Pumpling.class),
	SLIME("CustomSlime", EntityType.SLIME.getTypeId(), EntityType.SLIME, CustomSlime.class, CustomSlime.class),
	RIDEABLE_SPIDER("RideableSpider", EntityType.SPIDER.getTypeId(), EntityType.SPIDER, RideableSpider.class, RideableSpider.class),
	CUSTOM_GUARDIAN("CustomGuardian", EntityType.GUARDIAN.getTypeId(), EntityType.GHAST, CustomGuardian.class, CustomGuardian.class);

	public static List<Entity> customEntities = new ArrayList<Entity>();

	private String name;
	private int id;
	private EntityType entityType;
	private Class<? extends EntityInsentient> nmsClass;
	private Class<? extends EntityInsentient> customClass;

	CustomEntities(String name, int id, EntityType entityType, Class<? extends EntityInsentient> nmsClass,
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
			CustomEntityRegistry.registerCustomEntity(entity.getID(), entity.getName(), entity.getCustomClass());

		for (BiomeBase biomeBase : BiomeBase.i) {
			if (biomeBase == null)
				break;
			for (String field : new String[] { "u", "v", "w", "x" })
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
		Field field = getField(EntityTypes.class, "b");
		Field modifiersField = getField(Field.class, "modifiers");
		try {
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(null, CustomEntityRegistry.getInstance().getWrapped());
		} catch (Exception e) {
		}
	}

	private static Field getField(Class<?> clazz, String field) {
		if (clazz == null)
			return null;
		Field f = null;
		try {
			f = clazz.getDeclaredField(field);
			f.setAccessible(true);
		} catch (Exception e) {
		}
		return f;
	}
}
