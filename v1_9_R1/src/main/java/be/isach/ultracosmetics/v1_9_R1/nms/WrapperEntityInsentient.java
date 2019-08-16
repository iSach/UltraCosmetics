package be.isach.ultracosmetics.v1_9_R1.nms;

import net.minecraft.server.v1_9_R1.EntityInsentient;

public class WrapperEntityInsentient extends WrapperEntityLiving {

    protected EntityInsentient handle;

    public WrapperEntityInsentient(EntityInsentient handle) {
        super(handle);

        this.handle = handle;
    }

    @Override
    public EntityInsentient getHandle() {
        return handle;
    }

}
