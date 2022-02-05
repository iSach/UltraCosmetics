package be.isach.ultracosmetics.v1_17_R1.customentities;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;

import be.isach.ultracosmetics.v1_17_R1.EntityBase;
import be.isach.ultracosmetics.v1_17_R1.nms.EntityWrapper;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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
            throw new IllegalArgumentException("The entity field should implements EntityBase");
        }

        EntityBase entityBase = (EntityBase) mob;
        Entity entity = mob;

        EntityWrapper wEntity = new EntityWrapper(mob);
        EntityWrapper wPassenger = new EntityWrapper(passenger);

        if (passenger != null) {
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

                /*float f2 = MathHelper.sin(entity.yaw * 0.017453292f);
                float f3 = MathHelper.cos(entity.yaw * 0.017453292f);
                entity.setMot(entity.getMot().add(-0.4f * f2, upMot, 0.4f * f3));*/
            }

            wEntity.setStepHeight(1.0f);
            wEntity.setJumpMovementFactor(wEntity.getMoveSpeed() * 0.1f);

            wEntity.setRotationYawHead(entity.getYRot());

            entityBase.g_(sideMot, forMot);


            wEntity.setPrevLimbSwingAmount(wEntity.getLimbSwingAmount());

            double dx = entity.getX() - entity.xo;
            double dz = entity.getZ() - entity.zo;

            float f4 = Mth.sqrt((float) (dx * dx + dz * dz)) * 4;

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
}
