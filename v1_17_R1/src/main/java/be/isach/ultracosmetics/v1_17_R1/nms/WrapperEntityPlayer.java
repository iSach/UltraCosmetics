package be.isach.ultracosmetics.v1_17_R1.nms;

import net.minecraft.server.level.ServerPlayer;

/**
 * @author RadBuilder
 */
public class WrapperEntityPlayer extends WrapperEntityHuman {

    protected ServerPlayer handle;

    public WrapperEntityPlayer(ServerPlayer handle) {
        super(handle);

        this.handle = handle;
    }

    @Override
    public ServerPlayer getHandle() {
        return handle;
    }

}
