package be.isach.ultracosmetics.v1_13_R2.nms;

import java.lang.reflect.Field;

import net.minecraft.server.v1_13_R2.EntityLiving;

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
        return handle.Q;
    }

    public void setStepHeight(float stepHeight) {
        handle.Q = stepHeight;
    }

    public boolean canPassengerSteer() {
        return handle.bT();
    }

    public float getRotationYawHead() {
        return handle.aS;
    }

    public void setRotationYawHead(float rotationYawHead) {
        handle.aS = rotationYawHead;
    }

    public float getRenderYawOffset() {
        return handle.aQ;
    }

    public void setRenderYawOffset(float renderYawOffset) {
        handle.aQ = renderYawOffset;
    }

    public float getMoveStrafing() {
        return handle.bh;
    }

    public void setMoveStrafing(float moveStrafing) {
        handle.bh = moveStrafing;
    }

    public float getMoveForward() {
        return handle.bj;
    }

    public void setMoveForward(float moveForward) {
        handle.bj = moveForward;
    }

    public boolean isJumping() {
        return getField("bg", EntityLiving.class, Boolean.class);
    }

    public void setJumping(boolean jumping) {
        setField("bg", EntityLiving.class, jumping);
    }

    public float getJumpMovementFactor() {
        return handle.aT;
    }

    public void setJumpMovementFactor(float jumpMovementFactor) {
        handle.aT = jumpMovementFactor;
    }

    public float getPrevLimbSwingAmount() {
        return handle.aI;
    }

    public void setPrevLimbSwingAmount(float prevLimbSwingAmount) {
        handle.aI = prevLimbSwingAmount;
    }

    public float getLimbSwingAmount() {
        return handle.aJ;
    }

    public void setLimbSwingAmount(float limbSwingAmount) {
        handle.aJ = limbSwingAmount;
    }

    public float getLimbSwing() {
        return handle.aK;
    }

    public void setLimbSwing(float limbSwing) {
        handle.aK = limbSwing;
    }

    public float getMoveSpeed() {
        return handle.cK();
    }

    public void setMoveSpeed(float moveSpeed) {
        handle.o(moveSpeed);
    }

    public EntityLiving getHandle() {
        return handle;
    }
}
