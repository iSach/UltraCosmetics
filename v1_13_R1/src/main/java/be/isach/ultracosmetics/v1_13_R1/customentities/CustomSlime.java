package be.isach.ultracosmetics.v1_13_R1.customentities;

import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.entity.Entity;
import be.isach.ultracosmetics.v1_13_R1.EntityBase;
import be.isach.ultracosmetics.v1_13_R1.nms.WrapperEntityHuman;
import be.isach.ultracosmetics.v1_13_R1.nms.WrapperEntityInsentient;
import be.isach.ultracosmetics.v1_13_R1.ridable.*;
import net.minecraft.server.v1_13_R1.*;

/**
 * @author BillyGalbreath
 *
 * Author of plugin: "Ridables"
 * Thanks for authorizing using Ridables code to make UC work!
 */
public class CustomSlime extends EntitySlime implements IMountCustomEntity, RidableEntity {

	public CustomSlime(World world) {
		super(world);
		moveController = new SlimeWASDController(this);
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
		getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.4D);
		getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(20d);
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
		checkMove();
	}

	// processInteract
	@Override
	public boolean a(EntityHuman entityhuman, EnumHand hand) {
		if (super.a(entityhuman, hand)) {
			return true; // handled by vanilla action
		}
		return false;
	}

	// initAI - override vanilla AI
	@Override
	protected void n() {
		goalSelector.a(1, new AISlimeSwim(this));
		goalSelector.a(2, new AISlimeAttack(this));
		goalSelector.a(3, new AISlimeFaceRandom(this));
		goalSelector.a(5, new AISlimeHop(this));
		targetSelector.a(1, new AIFindNearestPlayer(this));
		targetSelector.a(3, new AIFindNearestEntity(this, EntityIronGolem.class));
	}

	// getAttackStrength
	@Override
	protected int dv() {
		if(CustomEntities.customEntities.contains(this)) {
			return 0;
		}
		return super.dv();
	}

	@Override
	public Entity getEntity() {
		return getBukkitEntity();
	}

	@Override
	public void removeAi() {
		// setNoAI(true);
	}

	public static class SlimeWASDController extends ControllerWASD {
		private final CustomSlime slime;
		private float yRot;
		private int jumpDelay;
		private boolean isAggressive;

		public SlimeWASDController(CustomSlime slime) {
			super(slime);
			this.slime = slime;
			yRot = slime.yaw * (180 / MathUtils.PI);
		}

		public void setDirection(float yRot, boolean isAggressive) {
			this.yRot = yRot;
			this.isAggressive = isAggressive;
		}

		public void setSpeed(double speed) {
			e = speed;
			h = ControllerMove.Operation.MOVE_TO;
		}

		@Override
		public void tick() {
			slime.aQ = slime.aS = slime.yaw = a(slime.yaw, yRot, 90.0F);
			if (h != ControllerMove.Operation.MOVE_TO) {
				slime.r(0.0F); // forward
				return;
			}
			h = ControllerMove.Operation.WAIT;
			if (slime.onGround) {
				slime.o((float) (e * a.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()));
				if (jumpDelay-- <= 0) {
					jumpDelay = slime.ds(); // getJumpDelay
					if (isAggressive) {
						jumpDelay /= 3;
					}
					slime.getControllerJump().a(); // setJumping
					if (slime.dz()) { // makeSoundOnJump
						slime.a(slime.dw(), slime.cD(), ((slime.getRandom().nextFloat() - slime.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F); // playSound
					}
				} else {
					slime.bh = 0.0F; // moveStrafing
					slime.bj = 0.0F; // moveForward
					slime.o(0.0F); // setSpeed
				}
				return;
			}
			slime.o((float) (e * a.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()));
		}
	}
}
