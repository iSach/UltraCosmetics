package be.isach.ultracosmetics.v1_19_R1.customentities;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;
import be.isach.ultracosmetics.v1_19_R1.EntityBase;
import be.isach.ultracosmetics.v1_19_R1.ObfuscatedFields;
import be.isach.ultracosmetics.v1_19_R1.nms.EntityWrapper;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.SharedConstants;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
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
        @SuppressWarnings("deprecation")
        Map<String, Type<?>> types = (Map<String, Type<?>>) DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getWorldVersion())).findChoiceType(References.ENTITY).types();

        // true if the registry present is a vanilla registry and not a custom one like
        // Citizens provides
        boolean isRealRegistry = Registry.ENTITY_TYPE.getClass().equals(DefaultedRegistry.class);
        if (isRealRegistry) {
            unfreezeRegistry();
        } else {
            UltraCosmeticsData.get().getPlugin().getSmartLogger().write("Entity registry is not vanilla, skipping unfreeze and refreeze");
        }
        registerEntity("zombie", Pumpling::new, types);
        registerEntity("slime", CustomSlime::new, types);
        registerEntity("spider", RideableSpider::new, types);
        registerEntity("guardian", CustomGuardian::new, types);
        if (isRealRegistry) {
            Registry.ENTITY_TYPE.freeze();
        }
    }

    private static void unfreezeRegistry() {
        /*
         * As of 1.18.2, registries are frozen once NMS is done adding to them, so we
         * have to do some super hacky things to add custom entities now. Basically,
         * when the registry is frozen, the "frozen" field is set to prevent new
         * entries, and a map "intrusiveHolderCache" is set to null (don't really know
         * what it does.) If frozen is true or "intrusiveHolderCache" is null, it will
         * refuse to add entries, so we just have to fix both of those things and it'll
         * let us add entries again. The registry being frozen may be vital to how the
         * registry works (idk), so it is refrozen after adding our entries.
         * 
         * Partial stack trace produced when trying to add entities when the registry is
         * frozen: [Server thread/ERROR]: Registry is already frozen initializing
         * UltraCosmetics v2.6.1-DEV-b5 (Is it up to date?)
         * java.lang.IllegalStateException: Registry is already frozen at
         * net.minecraft.core.RegistryMaterials.e(SourceFile:343)
         * ~[spigot-1.18.2-R0.1-SNAPSHOT.jar:3445-Spigot-fb0dd5f-05a38da] at
         * net.minecraft.world.entity.EntityTypes.<init>(EntityTypes.java:300)
         * ~[spigot-1.18.2-R0.1-SNAPSHOT.jar:3445-Spigot-fb0dd5f-05a38da] at
         * net.minecraft.world.entity.EntityTypes$Builder.a(EntityTypes.java:669)
         * ~[spigot-1.18.2-R0.1-SNAPSHOT.jar:3445-Spigot-fb0dd5f-05a38da] at
         * be.isach.ultracosmetics.v1_19_R1.customentities.CustomEntities.registerEntity
         * (CustomEntities.java:78) ~[?:?]
         */
        Class<?> registryClass = MappedRegistry.class;
        try {
            Field intrusiveHolderCache = registryClass.getDeclaredField(ObfuscatedFields.INTRUSIVE_HOLDER_CACHE);
            intrusiveHolderCache.setAccessible(true);
            intrusiveHolderCache.set(Registry.ENTITY_TYPE, new IdentityHashMap<EntityType<?>, Holder.Reference<EntityType<?>>>());
            Field frozen = registryClass.getDeclaredField(ObfuscatedFields.FROZEN);
            frozen.setAccessible(true);
            frozen.set(Registry.ENTITY_TYPE, false);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
    }

    private static void registerEntity(String type, @SuppressWarnings("rawtypes") EntityFactory customMob, Map<String, Type<?>> types) {
        String customName = "minecraft:ultracosmetics_" + type;
        ResourceLocation key = new ResourceLocation(customName);
        if (Registry.ENTITY_TYPE.containsKey(key)) {
            // Happens when UltraCosmetics is reloaded.
            UltraCosmeticsData.get().getPlugin().getSmartLogger().write(LogLevel.WARNING, "Skipping registration of " + customName + " because it is already registered.");
            return;
        }
        types.put(customName, types.get("minecraft:" + type));
        EntityType.Builder<Entity> a = EntityType.Builder.of(customMob, MobCategory.AMBIENT);
        Registry.register(Registry.ENTITY_TYPE, customName, a.build(customName));
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

        entity.yRotO = ((Entity) passenger).getYRot() % 360f;
        entity.setYRot(entity.yRotO);
        entity.setXRot((((Entity) passenger).getXRot() * 0.5F) % 360f);

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

    public static void addCustomEntity(Entity entity) {
        customEntities.add(entity);
    }

    public static boolean isCustomEntity(Entity entity) {
        return customEntities.contains(entity);
    }

    public static void removeCustomEntity(Entity entity) {
        customEntities.remove(entity);
    }

    public static Component toComponent(String str) {
        return MutableComponent.create(new LiteralContents(str));
    }
}
