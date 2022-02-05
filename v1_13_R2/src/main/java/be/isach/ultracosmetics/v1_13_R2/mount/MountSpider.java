package be.isach.ultracosmetics.v1_13_R2.mount;

import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.entity.Spider;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_13_R2.customentities.RideableSpider;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.EntityTypes;

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
    public EntityLiving getNewEntity() {
        return new RideableSpider(EntityTypes.SPIDER, ((CraftWorld) getPlayer().getWorld()).getHandle());
    }
}
