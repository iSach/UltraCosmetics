package be.isach.ultracosmetics.v1_17_R1.customentities;

import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import be.isach.ultracosmetics.v1_17_R1.EntityBase;
import be.isach.ultracosmetics.v1_17_R1.nms.WrapperEntityHuman;
import be.isach.ultracosmetics.v1_17_R1.nms.WrapperEntityInsentient;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * @author iSach
 */
public class RideableSpider extends Spider implements IMountCustomEntity, EntityBase {

    boolean isOnGround;

    public RideableSpider(EntityType<? extends Spider> entitytypes, Level world) {
        super(entitytypes, world);
    }

    static void ride(float sideMot, float forMot, Player passenger, Mob mob) {
    	if (!(mob instanceof EntityBase)) {
            throw new IllegalArgumentException("The entity field should implements EntityBase");
        }

        EntityBase entityBase = (EntityBase) mob;
        Entity entity = mob;

        WrapperEntityInsentient wEntity = new WrapperEntityInsentient(mob);
        WrapperEntityHuman wPassenger = new WrapperEntityHuman(passenger);

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

            if (wPassenger.isJumping() && (entity.isOnGround() || entityBase.canFly())) {
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

            wEntity.setMoveSpeed(0.35f * entityBase.getSpeed_());
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

    @Override
    //public void a(float sideMot, float forMot, float f2) {
    public void travel(Vec3 vec3D) {
    	if (!CustomEntities.customEntities.contains(this)) {
            super.tickHeadTurn((float) vec3D.x, (float) vec3D.y);
            return;
        }
        Player passenger = null;
        if (!getPassengers().isEmpty()) {
            passenger = (Player) getPassengers().get(0);
        }
        ride((float) vec3D.x, (float) vec3D.y, passenger, this);
    }

    @Override
    public org.bukkit.entity.Entity getEntity() {
        return getBukkitEntity();
    }

    @Override
    public void g_(float sideMot, float forMot) {
        super.travel(new Vec3(sideMot, 0, forMot));
    }
    
    @Override
    public float getSpeed() {
    	return getSpeed_();
    }

    @Override
    public float getSpeed_() {
        return 1;
    }

    @Override
    public boolean canFly() {
        return false;
    }

    @Override
    public TextComponent getName() {
    	return new TextComponent(Language.getInstance().getOrDefault("entity.Spider.name"));
    }

    @Override
    public void removeAi() {
        //setNoAI(true);
    }
}
