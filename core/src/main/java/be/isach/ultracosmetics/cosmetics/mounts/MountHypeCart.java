package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.PlayerUtils;

import java.util.UUID;

public class MountHypeCart extends Mount {

    public MountHypeCart(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.HYPECART, ultraCosmetics);
    }

    @Override
    public void onUpdate() {
        if (entity.isOnGround())
            entity.setVelocity(PlayerUtils.getHorizontalDirection(getPlayer(), 7.6));
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().setClimb(entity);
    }

    @Override
    public void onClear() {
    }
}
