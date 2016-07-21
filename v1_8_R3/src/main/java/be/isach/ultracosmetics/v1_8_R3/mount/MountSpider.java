package be.isach.ultracosmetics.v1_8_R3.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.mounts.MountType;

import java.util.UUID;

/**
 * Created by Sacha on 18/10/15.
 */
public class MountSpider extends MountCustomEntity {
    public MountSpider(UUID owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.SPIDER, ultraCosmetics);
    }

    @Override
    protected void onUpdate() {}
}
