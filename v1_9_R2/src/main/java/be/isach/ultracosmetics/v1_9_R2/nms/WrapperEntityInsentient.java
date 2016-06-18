package be.isach.ultracosmetics.v1_9_R2.nms;

import net.minecraft.server.v1_9_R2.EntityInsentient;

public class WrapperEntityInsentient extends WrapperEntityLiving {

    protected EntityInsentient handle;

    public WrapperEntityInsentient(EntityInsentient handle) {
        super(handle);

        this.handle = handle;
    }

    @Override
    public EntityInsentient getHandle() { return handle; }

}
