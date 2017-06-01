package be.isach.ultracosmetics.v1_9_R1.nms;

import net.minecraft.server.v1_9_R1.EntityLiving;

public class WrapperEntityLiving extends WrapperEntity {

	protected EntityLiving handle;

	public WrapperEntityLiving(EntityLiving handle) {
		super(handle);

		this.handle = handle;
	}

	/*
	 * 1_9_R2 : aP
	 */
	public float getRotationYawHead() {
		return handle.aO;
	}

	public void setRotationYawHead(float rotationYawHead) {
		handle.aO = rotationYawHead;
	}

	/*
	 * 1_9_R2 : aN
	 */
	public float getRenderYawOffset() {
		return handle.aM;
	}

	public void setRenderYawOffset(float renderYawOffset) {
		handle.aM = renderYawOffset;
	}

	/*
	 * 1_9_R2 : be
	 */
	public float getMoveStrafing() {
		return handle.bd;
	}

	public void setMoveStrafing(float moveStrafing) {
		handle.bd = moveStrafing;
	}

	/*
	 * 1_9_R2 : bf
	 */
	public float getMoveForward() {
		return handle.be;
	}

	public void setMoveForward(float moveForward) {
		handle.be = moveForward;
	}

	/*
	 * 1_9_R2 : bd
	 */
	public boolean isJumping() {
		return getField("bc", EntityLiving.class, Boolean.class);
	}

	public void setJumping(boolean jumping) {
		setField("bc", EntityLiving.class, jumping);
	}

	/*
	 * 1_9_R2 : aR
	 */
	public float getJumpMovementFactor() {
		return handle.aQ;
	}

	public void setJumpMovementFactor(float jumpMovementFactor) {
		handle.aQ = jumpMovementFactor;
	}

	/*
	 * 1_9_R2 : aF
	 */
	public float getPrevLimbSwingAmount() {
		return handle.aE;
	}

	public void setPrevLimbSwingAmount(float prevLimbSwingAmount) {
		handle.aE = prevLimbSwingAmount;
	}

	/*
	 * 1_9_R2 : aG
	 */
	public float getLimbSwingAmount() {
		return handle.aF;
	}

	public void setLimbSwingAmount(float limbSwingAmount) {
		handle.aF = limbSwingAmount;
	}

	/*
	 * 1_9_R2 : aH
	 */
	public float getLimbSwing() {
		return handle.aG;
	}

	public void setLimbSwing(float limbSwing) {
		handle.aG = limbSwing;
	}

	/*
	 * 1_9_R2 : cl() - l(float)
	 */
	public float getMoveSpeed() {
		return handle.ck();
	}

	public void setMoveSpeed(float moveSpeed) {
		handle.l(moveSpeed);
	}

	@Override
	public EntityLiving getHandle() {
		return handle;
	}

}
