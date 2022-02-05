package be.isach.ultracosmetics.v1_14_R1.nms;

import java.lang.reflect.Field;

import net.minecraft.server.v1_14_R1.EntityLiving;

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
        return handle.K;
    }

    public void setStepHeight(float stepHeight) {
        handle.K = stepHeight;
    }

    public boolean canPassengerSteer() {
        return handle.bT();
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

    public EntityLiving getHandle() {
        return handle;
    }
}
