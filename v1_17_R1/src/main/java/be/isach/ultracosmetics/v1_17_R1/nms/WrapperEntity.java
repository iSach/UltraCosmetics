package be.isach.ultracosmetics.v1_17_R1.nms;

import net.minecraft.world.entity.Entity;

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
        return handle.maxUpStep;
    }

    public void setStepHeight(float stepHeight) {
        handle.maxUpStep = stepHeight;
    }

    public boolean canPassengerSteer() {
        return handle.onlyOpCanSetNbt();
    }

    public Entity getHandle() {
        return handle;
    }

}
