package be.isach.ultracosmetics.v1_11_R1.nms;

import net.minecraft.server.v1_11_R1.EntityLiving;

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
        return handle.aP;
    }

    public void setRotationYawHead(float rotationYawHead) {
        handle.aP = rotationYawHead;
    }

    public float getRenderYawOffset() {
        return handle.aN;
    }

    public void setRenderYawOffset(float renderYawOffset) {
        handle.aN = renderYawOffset;
    }

    public float getMoveStrafing() {
        return handle.be;
    }

    public void setMoveStrafing(float moveStrafing) {
        handle.be = moveStrafing;
    }

    public float getMoveForward() {
        return handle.bf;
    }

    public void setMoveForward(float moveForward) {
        handle.bf = moveForward;
    }

    public boolean isJumping() {
        return getField("bd", EntityLiving.class, Boolean.class);
    }

    public void setJumping(boolean jumping) {
        setField("bd", EntityLiving.class, jumping);
    }

    public float getJumpMovementFactor() {
        return handle.aQ;
    }

    public void setJumpMovementFactor(float jumpMovementFactor) {
        handle.aQ = jumpMovementFactor;
    }

    public float getPrevLimbSwingAmount() {
        return handle.aF;
    }

    public void setPrevLimbSwingAmount(float prevLimbSwingAmount) {
        handle.aF = prevLimbSwingAmount;
    }

    public float getLimbSwingAmount() {
        return handle.aG;
    }

    public void setLimbSwingAmount(float limbSwingAmount) {
        handle.aG = limbSwingAmount;
    }

    public float getLimbSwing() {
        return handle.aH;
    }

    public void setLimbSwing(float limbSwing) {
        handle.aH = limbSwing;
    }

    public float getMoveSpeed() {
        return handle.cq();
    }

    public void setMoveSpeed(float moveSpeed) {
        handle.l(moveSpeed);
    }

    @Override
    public EntityLiving getHandle() {
        return handle;
    }
}
