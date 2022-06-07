package be.isach.ultracosmetics.v1_19_R1.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_19_R1.customentities.CustomEntities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;

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
        // must refer to entity as an Entity
        getCustomEntity().moveTo(x, y + 2, z, 0, 0);
        ((CraftWorld) getPlayer().getWorld()).getHandle().addFreshEntity(getCustomEntity());
        CustomEntities.addCustomEntity(getCustomEntity());
        // must refer to entity as a LivingEntity
        ((LivingEntity)getCustomEntity()).setSpeed((float) getType().getMovementSpeed());
        return getEntity();
    }

    @Override
    protected void removeEntity() {
        getCustomEntity().discard();
        CustomEntities.removeCustomEntity(getCustomEntity());
    }

    public Entity getCustomEntity() {
        return ((CraftEntity)entity).getHandle();
    }

    public abstract Entity getNewEntity();
}
