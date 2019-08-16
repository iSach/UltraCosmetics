package be.isach.ultracosmetics.v1_14_R1.nms;

import net.minecraft.server.v1_14_R1.EntityLiving;

/**
 * @author RadBuilder
 */
public class WrapperEntityLiving extends WrapperEntity {

    protected EntityLiving handle;

    public WrapperEntityLiving(EntityLiving handle) {
        super(handle);

        this.handle = handle;
    }

    public float getRotationYawHead() {
        return handle.aM;
    }

    public void setRotationYawHead(float rotationYawHead) {
        handle.aM = rotationYawHead;
    }

    public float getRenderYawOffset() {
        return handle.aK;
    }

    public void setRenderYawOffset(float renderYawOffset) {
        handle.aK = renderYawOffset;
    }

    public float getMoveStrafing() {
        return handle.bb;
    }

    public void setMoveStrafing(float moveStrafing) {
        handle.bb = moveStrafing;
    }

    public float getMoveForward() {
        return handle.bd;
    }

    public void setMoveForward(float moveForward) {
        handle.bd = moveForward;
    }

    public boolean isJumping() {
        return getField("jumping", EntityLiving.class, Boolean.class);
    }

    public void setJumping(boolean jumping) {
        setField("jumping", EntityLiving.class, jumping);
    }

    public float getJumpMovementFactor() {
        return handle.aN;
    }

    public void setJumpMovementFactor(float jumpMovementFactor) {
        handle.aN = jumpMovementFactor;
    }

    public float getPrevLimbSwingAmount() {
        return handle.aE;
    }

    public void setPrevLimbSwingAmount(float prevLimbSwingAmount) {
        handle.aE = prevLimbSwingAmount;
    }

    public float getLimbSwingAmount() {
        return handle.aF;
    }

    public void setLimbSwingAmount(float limbSwingAmount) {
        handle.aF = limbSwingAmount;
    }

    public float getLimbSwing() {
        return handle.aG;
    }

    public void setLimbSwing(float limbSwing) {
        handle.aG = limbSwing;
    }

    public float getMoveSpeed() {
        return handle.db();
    }

    public void setMoveSpeed(float moveSpeed) {
        handle.o(moveSpeed);
    }

    @Override
    public EntityLiving getHandle() {
        return handle;
    }
}
