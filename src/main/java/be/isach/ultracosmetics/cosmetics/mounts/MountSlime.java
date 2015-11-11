package be.isach.ultracosmetics.cosmetics.mounts;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * Created by Sacha on 17/10/15.
 */
public class MountSlime extends Mount {

    public MountSlime(UUID owner) {
        super(EntityType.SLIME, Material.SLIME_BALL, (byte) 0, "Slime", "ultracosmetics.mounts.slime", owner, MountType.SLIME, "&7&oSplat Splat");
    }

    @Override
    void onUpdate() {
    }
}
