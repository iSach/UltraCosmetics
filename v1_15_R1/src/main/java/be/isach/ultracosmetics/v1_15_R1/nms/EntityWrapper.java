package be.isach.ultracosmetics.v1_15_R1.nms;

import java.lang.reflect.Field;

import net.minecraft.server.v1_15_R1.EntityLiving;

/**
 * @author RadBuilder
 */
public class EntityWrapper {

    protected EntityLiving handle;

    public EntityWrapper(EntityLiving handle) {
        this.handle = handle;
    }

    public <T> T getField(String name, Class<?> fieldClass, Class<T> clazz) {
        T value = null;

        try {
            Field field = fieldClass.getDeclaredField(name);
            field.setAccessible(true);

            return clazz.cast(field.get(handle));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return value;
    }

    public <T> void setField(String name, Class<?> fieldClass, T value) {
        try {
            Field field = fieldClass.getDeclaredField(name);
            field.setAccessible(true);

            field.set(handle, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public float getStepHeight() {
        return handle.H;
    }

    public void setStepHeight(float stepHeight) {
        handle.H = stepHeight;
    }

    public boolean canPassengerSteer() {
        return handle.cb();
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

    public EntityLiving getHandle() {
        return handle;
    }
}
