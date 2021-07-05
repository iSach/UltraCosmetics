package be.isach.ultracosmetics.v1_17_R1.nms;

import net.minecraft.server.v1_16_R3.Entity;

/**
 * @author RadBuilder
 */
public class WrapperEntity extends WrapperBase {

    // Corresponds to net.minecraft.world.entity.Entity
    protected Entity handle;

    public WrapperEntity(Entity handle) {
        super(handle);

        this.handle = handle;
    }

    // Corresponds to maxUpStep
    public float getStepHeight() {
        return handle.G;
    }

    // Corresponds to maxUpStep
    public void setStepHeight(float stepHeight) {
        handle.G = stepHeight;
    }

    // Corresponds to onlyOpCanSetNbt
    public boolean canPassengerSteer() {
        return handle.ci();
    }

    public Entity getHandle() {
        return handle;
    }

}
