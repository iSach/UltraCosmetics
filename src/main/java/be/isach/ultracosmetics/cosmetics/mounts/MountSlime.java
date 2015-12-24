package be.isach.ultracosmetics.cosmetics.mounts;

import java.util.UUID;

/**
 * Created by Sacha on 17/10/15.
 */
public class MountSlime extends Mount {

    public MountSlime(UUID owner) {
        super(owner, MountType.SLIME);
    }

    @Override
    void onUpdate() {
    }
}
