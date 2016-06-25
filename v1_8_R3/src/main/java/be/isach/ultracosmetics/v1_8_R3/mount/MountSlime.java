package be.isach.ultracosmetics.v1_8_R3.mount;

import be.isach.ultracosmetics.cosmetics.mounts.MountType;
import org.bukkit.entity.Slime;

import java.util.UUID;

/**
 * Created by Sacha on 17/10/15.
 */
public class MountSlime extends MountCustomEntity {

    public MountSlime(UUID owner) {
        super(owner, MountType.SLIME);
    }

    @Override
    protected void onUpdate() {
        ((Slime)getEntity()).setSize(3);
    }
}
