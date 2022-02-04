package be.isach.ultracosmetics.v1_16_R3.mount;

import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Spider;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_16_R3.customentities.RideableSpider;
import net.minecraft.server.v1_16_R3.EntityTypes;

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
        return new RideableSpider(EntityTypes.SPIDER, ((CraftWorld) getPlayer().getWorld()).getHandle());
    }
}
