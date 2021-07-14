package be.isach.ultracosmetics.v1_17_R1.nms;

import net.minecraft.world.entity.Mob;

/**
 * @author RadBuilder
 */
public class WrapperEntityInsentient extends WrapperEntityLiving {

    protected Mob handle;

    public WrapperEntityInsentient(Mob handle) {
        super(handle);

        this.handle = handle;
    }

    @Override
    public Mob getHandle() {
        return handle;
    }

}
