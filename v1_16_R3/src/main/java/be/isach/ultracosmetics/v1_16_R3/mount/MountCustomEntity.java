package be.isach.ultracosmetics.v1_16_R3.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_16_R3.customentities.CustomEntities;
import be.isach.ultracosmetics.v1_16_R3.nms.EntityWrapper;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityLiving;

import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;

/**
 * @author RadBuilder
 */
public abstract class MountCustomEntity extends Mount {

    public MountCustomEntity(UltraPlayer owner, MountType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @Override
    public org.bukkit.entity.Entity spawnEntity() {
        entity = getNewEntity().getBukkitEntity();
        double x = getPlayer().getLocation().getX();
        double y = getPlayer().getLocation().getY();
        double z = getPlayer().getLocation().getZ();
        getCustomEntity().setLocation(x, y + 2, z, 0, 0);
        ((CraftWorld) getPlayer().getWorld()).getHandle().addEntity(getCustomEntity());
        CustomEntities.addCustomEntity(getCustomEntity());
        new EntityWrapper((EntityLiving)getCustomEntity()).setMoveSpeed((float) getType().getMovementSpeed());
        return getEntity();
    }

    @Override
    protected void removeEntity() {
        getCustomEntity().dead = true;
        CustomEntities.removeCustomEntity(getCustomEntity());
    }

    public Entity getCustomEntity() {
        return ((CraftEntity)entity).getHandle();
    }

    public abstract EntityLiving getNewEntity();
}
