package be.isach.ultracosmetics.v1_16_R3.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * @author RadBuilder
 */
public class MountSpider extends MountCustomEntity {
    public MountSpider(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.valueOf("spider"), ultraCosmetics);
    }

    @Override
    public void onUpdate() {
    }
}
