package be.isach.ultracosmetics.v1_10_R1.nms;

import net.minecraft.server.v1_10_R1.EntityLiving;

public class WrapperEntityLiving extends WrapperEntity {

    protected EntityLiving handle;

    public WrapperEntityLiving(EntityLiving handle) {
        super(handle);

        this.handle = handle;
    }

    /*
     * 1_9_R2 : aP
     */
    public float getRotationYawHead() { return handle.aQ; }
    public void setRotationYawHead(float rotationYawHead) { handle.aQ = rotationYawHead; }

    /*
     * 1_9_R2 : aN
     */
    public float getRenderYawOffset() { return handle.aO; }
    public void setRenderYawOffset(float renderYawOffset) { handle.aO = renderYawOffset; }

    /*
     * 1_9_R2 : be
     */
    public float getMoveStrafing() { return handle.bf; }
    public void setMoveStrafing(float moveStrafing) { handle.bf = moveStrafing; }

    /*
     * 1_9_R2 : bf
     */
    public float getMoveForward() { return handle.bg; }
    public void setMoveForward(float moveForward) { handle.bg = moveForward; }

    /*
     * 1_9_R2 : bd
     */
    public boolean isJumping() { return getField("be", EntityLiving.class, Boolean.class); }
    public void setJumping(boolean jumping) { setField("be", EntityLiving.class, jumping); }

    /*
     * 1_9_R2 : aR
     */
    public float getJumpMovementFactor() { return handle.aS; }
    public void setJumpMovementFactor(float jumpMovementFactor) { handle.aS = jumpMovementFactor; }

    /*
     * 1_9_R2 : aF
     */
    public float getPrevLimbSwingAmount() { return handle.aG; }
    public void setPrevLimbSwingAmount(float prevLimbSwingAmount) { handle.aG = prevLimbSwingAmount; }

    /*
     * 1_9_R2 : aG
     */
    public float getLimbSwingAmount() { return handle.aH; }
    public void setLimbSwingAmount(float limbSwingAmount) { handle.aH = limbSwingAmount; }

    /*
     * 1_9_R2 : aH
     */
    public float getLimbSwing() { return handle.aI; }
    public void setLimbSwing(float limbSwing) { handle.aI = limbSwing; }

    /*
     * 1_9_R2 : cl() - l(float)
     */
    public float getMoveSpeed() { return handle.cp(); }
    public void setMoveSpeed(float moveSpeed) { handle.l(moveSpeed); }

    @Override
    public EntityLiving getHandle() { return handle; }

}
