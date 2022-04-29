package be.isach.ultracosmetics.v1_12_R1.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_12_R1.customentities.CustomEntities;
import be.isach.ultracosmetics.v1_12_R1.nms.EntityWrapper;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityLiving;

import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

/**
 * @author RadBuilder
 */
public abstract class MountCustomEntity extends Mount {

    protected EntityLiving customEntity;

    public MountCustomEntity(UltraPlayer owner, MountType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @Override
    public org.bukkit.entity.Entity spawnEntity() {
        customEntity = getNewEntity();
        double x = getPlayer().getLocation().getX();
        double y = getPlayer().getLocation().getY();
        double z = getPlayer().getLocation().getZ();
        getCustomEntity().setLocation(x, y + 2, z, 0, 0);
        ((CraftWorld) getPlayer().getWorld()).getHandle().addEntity(getCustomEntity());
        CustomEntities.addCustomEntity(getCustomEntity());
        new EntityWrapper(customEntity).setMoveSpeed((float) getType().getMovementSpeed());
        return getEntity();
    }

    @Override
    protected void removeEntity() {
        getCustomEntity().dead = true;
        CustomEntities.removeCustomEntity(customEntity);
    }

    @Override
    public org.bukkit.entity.Entity getEntity() {
        return customEntity.getBukkitEntity();
    }

    public Entity getCustomEntity() {
        return customEntity;
    }

    public abstract EntityLiving getNewEntity();
}
