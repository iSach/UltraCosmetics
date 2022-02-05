package be.isach.ultracosmetics.v1_8_R3.mount;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Spider;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_8_R3.customentities.RideableSpider;
import net.minecraft.server.v1_8_R3.EntityInsentient;

/**
 * Created by Sacha on 18/10/15.
 */
public class MountSpider extends MountCustomEntity<Spider> {
    public MountSpider(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.valueOf("spider"), ultraCosmetics);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public EntityInsentient getNewEntity() {
        return new RideableSpider(((CraftWorld) getPlayer().getWorld()).getHandle());
    }
}
