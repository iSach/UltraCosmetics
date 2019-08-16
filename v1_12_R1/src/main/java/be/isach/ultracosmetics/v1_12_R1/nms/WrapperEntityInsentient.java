package be.isach.ultracosmetics.v1_12_R1.nms;

import net.minecraft.server.v1_12_R1.EntityInsentient;

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
