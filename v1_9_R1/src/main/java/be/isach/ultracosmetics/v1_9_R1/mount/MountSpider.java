package be.isach.ultracosmetics.v1_9_R1.mount;

import be.isach.ultracosmetics.cosmetics.mounts.MountType;

import java.util.UUID;

/**
 * Created by Sacha on 18/10/15.
 */
public class MountSpider extends MountCustomEntity {
    public MountSpider(UUID owner) {
        super(owner, MountType.SPIDER);
    }

    @Override
    protected void onUpdate() {
    }
}
