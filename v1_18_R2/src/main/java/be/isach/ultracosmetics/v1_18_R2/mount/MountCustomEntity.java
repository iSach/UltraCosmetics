package be.isach.ultracosmetics.v1_18_R2.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_18_R2.customentities.CustomEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;

/**
 * @author RadBuilder
 */
public abstract class MountCustomEntity<E extends org.bukkit.entity.Entity> extends Mount<E> {

    protected LivingEntity customEntity;

    public MountCustomEntity(UltraPlayer owner, MountType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @Override
    public E spawnEntity() {
        customEntity = getNewEntity();
        double x = getPlayer().getLocation().getX();
        double y = getPlayer().getLocation().getY();
        double z = getPlayer().getLocation().getZ();
        ((Entity)customEntity).moveTo(x, y + 2, z, 0, 0);
        ((CraftWorld) getPlayer().getWorld()).getHandle().addFreshEntity(getCustomEntity());
        CustomEntities.addCustomEntity(getCustomEntity());
        customEntity.setSpeed((float) getType().getMovementSpeed());
        return getEntity();
    }

    @Override
    protected void removeEntity() {
        getCustomEntity().discard();
        CustomEntities.removeCustomEntity(customEntity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E getEntity() {
        return (E) customEntity.getBukkitEntity();
    }

    public LivingEntity getCustomEntity() {
        return customEntity;
    }

    public abstract LivingEntity getNewEntity();
}
