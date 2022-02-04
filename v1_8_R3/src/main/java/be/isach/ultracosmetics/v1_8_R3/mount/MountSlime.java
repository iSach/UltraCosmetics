package be.isach.ultracosmetics.v1_8_R3.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_8_R3.customentities.CustomSlime;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Slime;

/**
 * Created by Sacha on 17/10/15.
 */
public class MountSlime extends MountCustomEntity<Slime> {
    public MountSlime(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.valueOf("slime"), ultraCosmetics);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public IMountCustomEntity getNewEntity() {
        return new CustomSlime(((CraftPlayer) getPlayer()).getHandle().getWorld());
    }
}
