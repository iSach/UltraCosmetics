package be.isach.ultracosmetics.v1_9_R2.nms;

import net.minecraft.server.v1_9_R2.EntityLiving;

public class WrapperEntityLiving extends WrapperEntity {

    protected EntityLiving handle;

    public WrapperEntityLiving(EntityLiving handle) {
        super(handle);

        this.handle = handle;
    }

    /*
     * 1_9_R2 : aP
     */
    public float getRotationYawHead() { return handle.aP; }
    public void setRotationYawHead(float rotationYawHead) { handle.aP = rotationYawHead; }

    /*
     * 1_9_R2 : aN
     */
    public float getRenderYawOffset() { return handle.aN; }
    public void setRenderYawOffset(float renderYawOffset) { handle.aN = renderYawOffset; }

    /*
     * 1_9_R2 : be
     */
    public float getMoveStrafing() { return handle.be; }
    public void setMoveStrafing(float moveStrafing) { handle.be = moveStrafing; }

    /*
     * 1_9_R2 : bf
     */
    public float getMoveForward() { return handle.bf; }
    public void setMoveForward(float moveForward) { handle.bf = moveForward; }

    /*
     * 1_9_R2 : bd
     */
    public boolean isJumping() { return getField("bd", EntityLiving.class, Boolean.class); }
    public void setJumping(boolean jumping) { setField("bd", EntityLiving.class, jumping); }

    /*
     * 1_9_R2 : aR
     */
    public float getJumpMovementFactor() { return handle.aR; }
    public void setJumpMovementFactor(float jumpMovementFactor) { handle.aR = jumpMovementFactor; }

    /*
     * 1_9_R2 : aF
     */
    public float getPrevLimbSwingAmount() { return handle.aF; }
    public void setPrevLimbSwingAmount(float prevLimbSwingAmount) { handle.aF = prevLimbSwingAmount; }

    /*
     * 1_9_R2 : aG
     */
    public float getLimbSwingAmount() { return handle.aG; }
    public void setLimbSwingAmount(float limbSwingAmount) { handle.aG = limbSwingAmount; }

    /*
     * 1_9_R2 : aH
     */
    public float getLimbSwing() { return handle.aH; }
    public void setLimbSwing(float limbSwing) { handle.aH = limbSwing; }

    /*
     * 1_9_R2 : cl() - l(float)
     */
    public float getMoveSpeed() { return handle.cl(); }
    public void setMoveSpeed(float moveSpeed) { handle.l(moveSpeed); }

    @Override
    public EntityLiving getHandle() { return handle; }

}
