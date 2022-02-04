package be.isach.ultracosmetics.v1_13_R2.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_13_R2.customentities.CustomEntities;
import net.minecraft.server.v1_13_R2.Entity;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;

/**
 * @author RadBuilder
 */
public abstract class MountCustomEntity<E extends org.bukkit.entity.Entity> extends Mount<E> {

    protected IMountCustomEntity customEntity;

    public MountCustomEntity(UltraPlayer owner, MountType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @Override
    public E spawnEntity() {
        customEntity = getNewEntity();
        double x = getPlayer().getLocation().getX();
        double y = getPlayer().getLocation().getY();
        double z = getPlayer().getLocation().getZ();
        getCustomEntity().setLocation(x, y + 2, z, 0, 0);
        ((CraftWorld) getPlayer().getWorld()).getHandle().addEntity(getCustomEntity());
        CustomEntities.customEntities.add(getCustomEntity());
        customEntity.removeAi();
        return getEntity();
    }

    @Override
    protected void removeEntity() {
        getCustomEntity().dead = true;
        CustomEntities.customEntities.remove(customEntity);
    }

    @Override
    public E getEntity() {
        return (E) customEntity.getEntity();
    }

    public Entity getCustomEntity() {
        return (Entity)customEntity;
    }

    public abstract IMountCustomEntity getNewEntity();
}
