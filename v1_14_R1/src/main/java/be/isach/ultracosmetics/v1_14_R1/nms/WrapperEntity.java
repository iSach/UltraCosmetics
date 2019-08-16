package be.isach.ultracosmetics.v1_14_R1.nms;

import net.minecraft.server.v1_14_R1.Entity;

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
        return handle.K;
    }

    public void setStepHeight(float stepHeight) {
        handle.K = stepHeight;
    }

    public boolean canPassengerSteer() {
        return handle.bT();
    }

    public Entity getHandle() {
        return handle;
    }

}
