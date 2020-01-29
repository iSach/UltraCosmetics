package be.isach.ultracosmetics.v1_15_R1.nms;

import net.minecraft.server.v1_15_R1.Entity;

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
        return handle.H;
    }

    public void setStepHeight(float stepHeight) {
        handle.H = stepHeight;
    }

    public boolean canPassengerSteer() {
        return handle.cb();
    }

    public Entity getHandle() {
        return handle;
    }

}
