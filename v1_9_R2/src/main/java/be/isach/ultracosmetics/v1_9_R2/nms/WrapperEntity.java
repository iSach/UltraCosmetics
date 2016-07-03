package be.isach.ultracosmetics.v1_9_R2.nms;

import net.minecraft.server.v1_9_R2.Entity;

public class WrapperEntity extends WrapperBase {

    protected Entity handle;

    public WrapperEntity(Entity handle) {
        super(handle);

        this.handle = handle;
    }

    /*
     * 1_9_R2 : P
     */
    public float getStepHeight() { return handle.P; }
    public void setStepHeight(float stepHeight) { handle.P = stepHeight; }

    /*
     * 1_9_R2 : by()
     */
    public boolean canPassengerSteer() { return handle.by(); }

    public Entity getHandle() { return handle; }

}
