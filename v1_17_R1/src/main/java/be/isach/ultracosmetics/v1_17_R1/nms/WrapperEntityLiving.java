package be.isach.ultracosmetics.v1_17_R1.nms;

import net.minecraft.server.v1_16_R3.EntityLiving;

/**
 * @author RadBuilder
 */
public class WrapperEntityLiving extends WrapperEntity {

    protected EntityLiving handle;

    public WrapperEntityLiving(EntityLiving handle) {
        super(handle);

        this.handle = handle;
    }

    // Corresponds to yHeadRot
    public float getRotationYawHead() {
        return handle.aC;
    }

    // Corresponds to yHeadRot
    public void setRotationYawHead(float rotationYawHead) {
        handle.aC = rotationYawHead;
    }

    // Corresponds to yBodyRot
    public float getRenderYawOffset() {
        return handle.aA;
    }

    // Corresponds to yBodyRot
    public void setRenderYawOffset(float renderYawOffset) {
        handle.aA = renderYawOffset;
    }

    // Corresponds to xxa
    public float getMoveStrafing() {
        return handle.aR;
    }

    // Corresponds to xxa
    public void setMoveStrafing(float moveStrafing) {
        handle.aR = moveStrafing;
    }

    // Corresponds to zza
    public float getMoveForward() {
        return handle.aT;
    }

    // Corresponds to zza
    public void setMoveForward(float moveForward) {
        handle.aT = moveForward;
    }

    // Corresponds to jumping
    public boolean isJumping() {
        return getField("jumping", EntityLiving.class, Boolean.class);
    }

    // Corresponds to jumping
    public void setJumping(boolean jumping) {
        setField("jumping", EntityLiving.class, jumping);
    }

    // Corresponds to yBodyRotO
    public float getJumpMovementFactor() {
        return handle.aB;
    }

    // Corresponds to yBodyRotO
    public void setJumpMovementFactor(float jumpMovementFactor) {
        handle.aB = jumpMovementFactor;
    }

    // Corresponds to animationSpeedOld
    public float getPrevLimbSwingAmount() {
        return handle.au;
    }

    // Corresponds to animationSpeedOld
    public void setPrevLimbSwingAmount(float prevLimbSwingAmount) {
        handle.au = prevLimbSwingAmount;
    }

    // Corresponds to animationSpeed
    public float getLimbSwingAmount() {
        return handle.av;
    }

    // Corresponds to animationSpeed
    public void setLimbSwingAmount(float limbSwingAmount) {
        handle.av = limbSwingAmount;
    }

    // Corresponds to animationPosition
    public float getLimbSwing() {
        return handle.aw;
    }

    // Corresponds to animationPosition
    public void setLimbSwing(float limbSwing) {
        handle.aw = limbSwing;
    }

    // Corresponds to getSpeed
    public float getMoveSpeed() {
        return handle.dN();
    }

    // Corresponds to setSpeed
    public void setMoveSpeed(float moveSpeed) {
        handle.q(moveSpeed);
    }

    @Override
    public EntityLiving getHandle() {
        return handle;
    }
}
