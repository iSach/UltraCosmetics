package be.isach.ultracosmetics.v1_16_R1.nms;

import net.minecraft.server.v1_16_R1.Entity;

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
        return handle.G;
    }

    public void setStepHeight(float stepHeight) {
        handle.G = stepHeight;
    }

    public boolean canPassengerSteer() {
        return handle.ci();
    }

    public Entity getHandle() {
        return handle;
    }

}
