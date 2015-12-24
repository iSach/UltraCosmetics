package be.isach.ultracosmetics.cosmetics.mounts;

import java.util.UUID;

/**
 * Created by Sacha on 18/10/15.
 */
public class MountSpider extends Mount {
    public MountSpider(UUID owner) {
        super(owner, MountType.SPIDER);
    }

    @Override
    void onUpdate() {
    }
}
