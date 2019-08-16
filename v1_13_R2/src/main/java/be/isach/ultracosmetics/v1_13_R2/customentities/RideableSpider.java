package be.isach.ultracosmetics.v1_13_R2.customentities;

import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import be.isach.ultracosmetics.v1_13_R2.ridable.ControllerWASD;
import be.isach.ultracosmetics.v1_13_R2.ridable.LookController;
import be.isach.ultracosmetics.v1_13_R2.ridable.RidableEntity;
import net.minecraft.server.v1_13_R2.*;

/**
 * @author RadBuilder
 */
public class RideableSpider extends EntitySpider implements IMountCustomEntity, RidableEntity {

    public RideableSpider(World world) {
        super(world);
        moveController = new ControllerWASD(this);
        lookController = new LookController(this);
    }

    // canDespawn
    @Override
    public boolean isTypeNotPersistent() {
        return !hasCustomName() && !isLeashed();
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        getAttributeMap().b(ControllerWASD.RIDING_SPEED); // registerAttribute
        reloadAttributes();
    }

    @Override
    public void reloadAttributes() {
        getAttributeInstance(ControllerWASD.RIDING_SPEED).setValue(0.4D);
        getAttributeInstance(GenericAttributes.maxHealth).setValue(16d);
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.3d);
        getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(0D);
        getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(20D);
    }

    // canBeRiddenInWater
    @Override
    public boolean aY() {
        return false;
    }

    // travel
    @Override
    public void a(float strafe, float vertical, float forward) {
        super.a(strafe, vertical, forward);
        if (positionChanged && z_() && getRider() != null) {
            motY = 0.2D /** CONFIG.RIDING_CLIMB_SPEED*/;
        }
        checkMove();
    }

    // processInteract
    @Override
    public boolean a(EntityHuman entityhuman, EnumHand hand) {
        if (super.a(entityhuman, hand)) {
            return true; // handled by vanilla action
        }
        if (hand == EnumHand.MAIN_HAND && !entityhuman.isSneaking() && passengers.isEmpty() && !entityhuman.isPassenger()) {
            return tryRide(entityhuman, false, false);
        }
        return false;
    }

    @Override
    public boolean removePassenger(Entity passenger) {
        if (passenger instanceof EntityPlayer && !passengers.isEmpty() && passenger == passengers.get(0)) {
			/*if (!new RidableDismountEvent(this, (Player) passenger.getBukkitEntity(), notCancellable).callEvent() && !notCancellable) {
				return false; // cancelled
			}*/
        }
        return super.removePassenger(passenger);
    }

    // isOnLadder
    @Override
    public boolean z_() {
        if (getRider() == null) {
            return l(); // isBesideClimbableBlock
        }
        return l();
    }

    @Override
    public boolean onClick() {
        EntityPlayer rider = getRider();
        if (rider == null || !rider.b(EnumHand.MAIN_HAND).isEmpty()) {
            return false; // must have empty hands to shoot
        }
        return false;
    }

    @Override
    public org.bukkit.entity.Entity getEntity() {
        return getBukkitEntity();
    }

    @Override
    public void removeAi() {
        // setNoAI(true);
    }
}
