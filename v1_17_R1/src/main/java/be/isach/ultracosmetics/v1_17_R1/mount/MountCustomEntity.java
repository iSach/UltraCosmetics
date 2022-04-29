package be.isach.ultracosmetics.v1_17_R1.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_17_R1.customentities.CustomEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;

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
        getNMSEntity().moveTo(x, y + 2, z, 0, 0);
        ((CraftWorld) getPlayer().getWorld()).getHandle().addFreshEntity(getNMSEntity());
        CustomEntities.addCustomEntity(getNMSEntity());
        ((LivingEntity)getNMSEntity()).setSpeed((float) getType().getMovementSpeed());
        return getEntity();
    }

    @Override
    protected void removeEntity() {
        getNMSEntity().discard();
        CustomEntities.removeCustomEntity(getNMSEntity());
    }

    public Entity getNMSEntity() {
        return ((CraftEntity)entity).getHandle();
    }

    public abstract LivingEntity getNewEntity();
}
