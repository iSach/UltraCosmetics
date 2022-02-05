package be.isach.ultracosmetics.v1_12_R1.nms;

import java.lang.reflect.Field;

import net.minecraft.server.v1_12_R1.EntityLiving;

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
        return handle.P;
    }

    public void setStepHeight(float stepHeight) {
        handle.P = stepHeight;
    }

    public boolean canPassengerSteer() {
        return handle.bI();
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
        return handle.bg;
    }

    public void setMoveForward(float moveForward) {
        handle.bg = moveForward;
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
        return handle.cy();
    }

    public void setMoveSpeed(float moveSpeed) {
        handle.k(moveSpeed);
    }

    public EntityLiving getHandle() {
        return handle;
    }
}
