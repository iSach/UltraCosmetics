package be.isach.ultracosmetics.v1_12_R1.mount;

import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Spider;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_12_R1.customentities.RideableSpider;

/**
 * @author RadBuilder
 */
public class MountSpider extends MountCustomEntity<Spider> {
    public MountSpider(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.valueOf("spider"), ultraCosmetics);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public IMountCustomEntity getNewEntity() {
        return new RideableSpider(((CraftWorld) getPlayer().getWorld()).getHandle());
    }
}
