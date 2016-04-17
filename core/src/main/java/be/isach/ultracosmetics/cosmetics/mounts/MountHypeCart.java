package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.PlayerUtils;

import java.util.UUID;

public class MountHypeCart extends Mount {

    public MountHypeCart(UUID owner) {
        super(
                owner, MountType.HYPECART
        );
    }

    @Override
    protected void onUpdate() {
        if (entity.isOnGround())
            entity.setVelocity(PlayerUtils.getHorizontalDirection(getPlayer(), 7.6));
        UltraCosmetics.getInstance().getEntityUtil().setClimb(entity);
    }

    @Override
    public void onClear() {
    }
}
