package be.isach.ultracosmetics.v1_16_R1.nms;

import net.minecraft.server.v1_16_R1.EntityLiving;

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
        return handle.aJ;
    }

    // Corresponds to yHeadRot
    public void setRotationYawHead(float rotationYawHead) {
        handle.aJ = rotationYawHead;
    }

    // Corresponds to yBodyRot
    public float getRenderYawOffset() {
        return handle.aH;
    }

    // Corresponds to yBodyRot
    public void setRenderYawOffset(float renderYawOffset) {
        handle.aH = renderYawOffset;
    }

    // Corresponds to xxa
    public float getMoveStrafing() {
        return handle.aY;
    }

    // Corresponds to xxa
    public void setMoveStrafing(float moveStrafing) {
        handle.aY = moveStrafing;
    }

    // Corresponds to zza
    public float getMoveForward() {
        return handle.ba;
    }

    // Corresponds to zza
    public void setMoveForward(float moveForward) {
        handle.ba = moveForward;
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
        return handle.aI;
    }

    // Corresponds to yBodyRotO
    public void setJumpMovementFactor(float jumpMovementFactor) {
        handle.aI = jumpMovementFactor;
    }

    // Corresponds to animationSpeedOld
    public float getPrevLimbSwingAmount() {
        return handle.aB;
    }

    // Corresponds to animationSpeedOld
    public void setPrevLimbSwingAmount(float prevLimbSwingAmount) {
        handle.aB = prevLimbSwingAmount;
    }

    // Corresponds to animationSpeed
    public float getLimbSwingAmount() {
        return handle.aC;
    }

    // Corresponds to animationSpeed
    public void setLimbSwingAmount(float limbSwingAmount) {
        handle.aC = limbSwingAmount;
    }

    // Corresponds to animationPosition
    public float getLimbSwing() {
        return handle.aD;
    }

    // Corresponds to animationPosition
    public void setLimbSwing(float limbSwing) {
        handle.aD = limbSwing;
    }

    // Corresponds to getSpeed
    public float getMoveSpeed() {
        return handle.dM();
    }

    // Corresponds to setSpeed
    public void setMoveSpeed(float moveSpeed) {
        handle.n(moveSpeed);
    }

    @Override
    public EntityLiving getHandle() {
        return handle;
    }
}
