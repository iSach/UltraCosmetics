package be.isach.ultracosmetics.v1_18_R2.customentities;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;

import be.isach.ultracosmetics.v1_18_R2.EntityBase;
import be.isach.ultracosmetics.v1_18_R2.ObfuscatedFields;
import be.isach.ultracosmetics.v1_18_R2.nms.EntityWrapper;
import net.minecraft.SharedConstants;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
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

    private String name;
    private int id;
    private EntityType<?> entityType;
    private ResourceLocation minecraftKey;
    private Class<? extends Mob> nmsClass;
    private Class<? extends Entity> customClass;
    private static DefaultedRegistry<EntityType<?>> entityRegistry;

    private CustomEntities(String name, int id, EntityType entityType, Class<? extends Mob> nmsClass,
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

        // Get the current entityRegistry
        entityRegistry = getRegistry(Registry.ENTITY_TYPE);

        unfreezeRegistry();
        registerEntity("zombie", Pumpling::new, types);
        registerEntity("slime", CustomSlime::new, types);
        registerEntity("spider", RideableSpider::new, types);
        registerEntity("guardian", CustomGuardian::new, types);
        entityRegistry.freeze();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static DefaultedRegistry<EntityType<?>> getRegistry(DefaultedRegistry registryMaterials) {
        /* We want to use custom entity-registries if there are any.
            We therefore check if there is one and then look for the right registry.

            This avoids problems with UltraCosmetics using the minecraft-registry whilst other
            plugins are using the custom one which can lead to problems with one of the registries
            being frozen and then blocking the other one from being edited.
         */

        if (!registryMaterials.getClass().getName().equals(DefaultedRegistry.class.getName())) {
            UltraCosmeticsData.get().getPlugin().getSmartLogger().write("Custom entity registry found: " + registryMaterials.getClass().getName());
            for (Field field : registryMaterials.getClass().getDeclaredFields()) {
                if (field.getType() == MappedRegistry.class) {
                    field.setAccessible(true);
                    try {
                        DefaultedRegistry<EntityType<?>> reg = (DefaultedRegistry<EntityType<?>>) field.get(registryMaterials);

                        if (!reg.getClass().getName().equals(DefaultedRegistry.class.getName())) {
                            reg = getRegistry(reg);
                        }

                        return reg;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return registryMaterials;
    }

    private static void unfreezeRegistry() {
        /* As of 1.18.2, registries are frozen once NMS is done adding to them,
           so we have to do some super hacky things to add custom entities now.
           Basically, when the registry is frozen, the "frozen" field is set to prevent new entries,
           and a map "intrusiveHolderCache" is set to null (don't really know what it does.)
           If frozen is true or "intrusiveHolderCache" is null, it will refuse to add entries,
           so we just have to fix both of those things and it'll let us add entries again.
           The registry being frozen may be vital to how the registry works (idk), so it is refrozen after adding our entries.

           Partial stack trace produced when trying to add entities when the registry is frozen:
           [Server thread/ERROR]: Registry is already frozen initializing UltraCosmetics v2.6.1-DEV-b5 (Is it up to date?)
            java.lang.IllegalStateException: Registry is already frozen
                    at net.minecraft.core.RegistryMaterials.e(SourceFile:343) ~[spigot-1.18.2-R0.1-SNAPSHOT.jar:3445-Spigot-fb0dd5f-05a38da]
                    at net.minecraft.world.entity.EntityTypes.<init>(EntityTypes.java:300) ~[spigot-1.18.2-R0.1-SNAPSHOT.jar:3445-Spigot-fb0dd5f-05a38da]
                    at net.minecraft.world.entity.EntityTypes$Builder.a(EntityTypes.java:669) ~[spigot-1.18.2-R0.1-SNAPSHOT.jar:3445-Spigot-fb0dd5f-05a38da]
                    at be.isach.ultracosmetics.v1_18_R2.customentities.CustomEntities.registerEntity(CustomEntities.java:78) ~[?:?]
        */
        Class<MappedRegistry> registryClass = MappedRegistry.class;
        try {
            Field intrusiveHolderCache = registryClass.getDeclaredField(ObfuscatedFields.INTRUSIVE_HOLDER_CACHE);
            intrusiveHolderCache.setAccessible(true);
            intrusiveHolderCache.set(entityRegistry, new IdentityHashMap<EntityType<?>, Holder.Reference<EntityType<?>>>());
            Field frozen = registryClass.getDeclaredField(ObfuscatedFields.FROZEN);
            frozen.setAccessible(true);
            frozen.set(entityRegistry, false);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
    }

    private static void registerEntity(String type, EntityFactory customMob, Map<String,Type<?>> types) {
        String customName = "minecraft:ultracosmetics_" + type;
        types.put(customName, types.get("minecraft:" + type));
        EntityType.Builder<Entity> a = EntityType.Builder.of(customMob, MobCategory.AMBIENT);
        Registry.register(entityRegistry, customName, a.build(customName));
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

    public static void ride(float sideMot, float forMot, Player passenger, Mob mob) {
        if (!(mob instanceof EntityBase)) {
            throw new IllegalArgumentException("The entity parameter should implement EntityBase");
        }

        EntityBase entityBase = (EntityBase) mob;
        Entity entity = mob;

        EntityWrapper wEntity = new EntityWrapper(mob);
        EntityWrapper wPassenger = new EntityWrapper(passenger);

        if (passenger == null) {
            wEntity.setStepHeight(0.5f);
            wEntity.setJumpMovementFactor(0.02f);

            entityBase.travel_(sideMot, forMot);
            return;
        }

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
        }

        wEntity.setStepHeight(1.0f);
        wEntity.setJumpMovementFactor(wEntity.getMoveSpeed() * 0.1f);

        wEntity.setRotationYawHead(entity.getYRot());

        entityBase.travel_(sideMot, forMot);


        wEntity.setPrevLimbSwingAmount(wEntity.getLimbSwingAmount());

        double dx = entity.getX() - entity.xo;
        double dz = entity.getZ() - entity.zo;

        float f4 = Mth.sqrt((float) (dx * dx + dz * dz)) * 4;

        if (f4 > 1)
            f4 = 1;

        wEntity.setLimbSwingAmount(wEntity.getLimbSwingAmount() + (f4 - wEntity.getLimbSwingAmount()) * 0.4f);
        wEntity.setLimbSwing(wEntity.getLimbSwing() + wEntity.getLimbSwingAmount());
    }
}