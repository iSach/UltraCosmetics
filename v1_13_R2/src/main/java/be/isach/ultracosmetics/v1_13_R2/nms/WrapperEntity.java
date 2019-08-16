package be.isach.ultracosmetics.v1_13_R2.nms;

import net.minecraft.server.v1_13_R2.Entity;

/**
 * @author RadBuilder
 */
public class WrapperEntity extends WrapperBase {

    protected Entity handle;

    public WrapperEntity(Entity handle) {
        super(handle);

        this.handle = handle;
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

    public Entity getHandle() {
        return handle;
    }

}
