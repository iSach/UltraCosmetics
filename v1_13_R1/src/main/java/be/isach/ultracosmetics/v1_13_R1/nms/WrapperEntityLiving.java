package be.isach.ultracosmetics.v1_13_R1.nms;

import net.minecraft.server.v1_13_R1.EntityLiving;

/**
 * @author RadBuilder
 */
public class WrapperEntityLiving extends WrapperEntity {
	
	protected EntityLiving handle;
	
	public WrapperEntityLiving(EntityLiving handle) {
		super(handle);
		
		this.handle = handle;
	}
	
	public float getRotationYawHead() { return handle.aS; }
	
	public void setRotationYawHead(float rotationYawHead) { handle.aS = rotationYawHead; }
	
	public float getRenderYawOffset() { return handle.aQ; }
	
	public void setRenderYawOffset(float renderYawOffset) { handle.aQ = renderYawOffset; }
	
	public float getMoveStrafing() { return handle.bh; }
	
	public void setMoveStrafing(float moveStrafing) { handle.bh = moveStrafing; }
	
	public float getMoveForward() { return handle.bj; }
	
	public void setMoveForward(float moveForward) { handle.bj = moveForward; }
	
	public boolean isJumping() { return getField("bg", EntityLiving.class, Boolean.class); }
	
	public void setJumping(boolean jumping) { setField("bg", EntityLiving.class, jumping); }
	
	public float getJumpMovementFactor() { return handle.aT; }
	
	public void setJumpMovementFactor(float jumpMovementFactor) { handle.aT = jumpMovementFactor; }
	
	public float getPrevLimbSwingAmount() { return handle.aI; }
	
	public void setPrevLimbSwingAmount(float prevLimbSwingAmount) { handle.aI = prevLimbSwingAmount; }
	
	public float getLimbSwingAmount() { return handle.aJ; }
	
	public void setLimbSwingAmount(float limbSwingAmount) { handle.aJ = limbSwingAmount; }
	
	public float getLimbSwing() { return handle.aK; }
	
	public void setLimbSwing(float limbSwing) { handle.aK = limbSwing; }
	
	public float getMoveSpeed() { return handle.cK(); }
	
	public void setMoveSpeed(float moveSpeed) { handle.o(moveSpeed); }
	
	@Override
	public EntityLiving getHandle() { return handle; }
}
