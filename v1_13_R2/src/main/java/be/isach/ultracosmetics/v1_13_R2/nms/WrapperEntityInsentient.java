package be.isach.ultracosmetics.v1_13_R2.nms;

import net.minecraft.server.v1_13_R2.EntityInsentient;

/**
 * @author RadBuilder
 */
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
