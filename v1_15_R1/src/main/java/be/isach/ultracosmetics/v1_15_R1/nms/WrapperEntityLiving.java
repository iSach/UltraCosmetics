package be.isach.ultracosmetics.v1_15_R1.nms;

import net.minecraft.server.v1_15_R1.EntityLiving;

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
        return handle.aK;
    }

    // Corresponds to yHeadRot
    public void setRotationYawHead(float rotationYawHead) {
        handle.aK = rotationYawHead;
    }

    // Corresponds to yBodyRot
    public float getRenderYawOffset() {
        return handle.aI;
    }

    // Corresponds to yBodyRot
    public void setRenderYawOffset(float renderYawOffset) {
        handle.aI = renderYawOffset;
    }

    // Corresponds to xxa
    public float getMoveStrafing() {
        return handle.aZ;
    }

    // Corresponds to xxa
    public void setMoveStrafing(float moveStrafing) {
        handle.aZ = moveStrafing;
    }

    // Corresponds to zza
    public float getMoveForward() {
        return handle.bb;
    }

    // Corresponds to zza
    public void setMoveForward(float moveForward) {
        handle.bb = moveForward;
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
        return handle.aJ;
    }

    // Corresponds to yBodyRotO
    public void setJumpMovementFactor(float jumpMovementFactor) {
        handle.aJ = jumpMovementFactor;
    }

    // Corresponds to animationSpeedOld
    public float getPrevLimbSwingAmount() {
        return handle.aC;
    }

    // Corresponds to animationSpeedOld
    public void setPrevLimbSwingAmount(float prevLimbSwingAmount) {
        handle.aC = prevLimbSwingAmount;
    }

    // Corresponds to animationSpeed
    public float getLimbSwingAmount() {
        return handle.aD;
    }

    // Corresponds to animationSpeed
    public void setLimbSwingAmount(float limbSwingAmount) {
        handle.aD = limbSwingAmount;
    }

    // Corresponds to animationPosition
    public float getLimbSwing() {
        return handle.aE;
    }

    // Corresponds to animationPosition
    public void setLimbSwing(float limbSwing) {
        handle.aE = limbSwing;
    }

    // Corresponds to getSpeed
    public float getMoveSpeed() {
        return handle.dt();
    }

    // Corresponds to setSpeed
    public void setMoveSpeed(float moveSpeed) {
        handle.o(moveSpeed);
    }

    @Override
    public EntityLiving getHandle() {
        return handle;
    }
}
