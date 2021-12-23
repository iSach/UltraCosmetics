package be.isach.ultracosmetics.v1_18_R1.nms;

import net.minecraft.world.entity.player.Player;

/**
 * @author RadBuilder
 */
public class WrapperEntityHuman extends WrapperEntityLiving {

    protected Player handle;

    public WrapperEntityHuman(Player handle) {
        super(handle);

        this.handle = handle;
    }

    @Override
    public Player getHandle() {
        return handle;
    }

}
