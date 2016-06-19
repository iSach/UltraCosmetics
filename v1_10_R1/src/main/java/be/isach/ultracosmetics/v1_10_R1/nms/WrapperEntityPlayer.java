package be.isach.ultracosmetics.v1_10_R1.nms;

import net.minecraft.server.v1_10_R1.EntityPlayer;

public class WrapperEntityPlayer extends WrapperEntityHuman {

    protected EntityPlayer handle;

    public WrapperEntityPlayer(EntityPlayer handle) {
        super(handle);

        this.handle = handle;
    }

    @Override
    public EntityPlayer getHandle() { return handle; }

}
