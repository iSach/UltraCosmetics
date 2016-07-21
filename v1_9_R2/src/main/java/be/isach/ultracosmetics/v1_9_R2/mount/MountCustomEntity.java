package be.isach.ultracosmetics.v1_9_R2.mount;

import be.isach.ultracosmetics.v1_9_R2.customentities.CustomEntities;
import be.isach.ultracosmetics.v1_9_R2.customentities.CustomSlime;
import be.isach.ultracosmetics.v1_9_R2.customentities.FlyingSquid;
import be.isach.ultracosmetics.v1_9_R2.customentities.RideableSpider;
import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.mounts.MountType;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import net.minecraft.server.v1_9_R2.Entity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Created by Sacha on 15/03/16.
 */
public abstract class MountCustomEntity extends Mount {

    /**
     * Custom Entity.
     */
    public IMountCustomEntity customEntity;

    public MountCustomEntity(UUID owner, MountType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @Override
    public void equip() {
        if (getType() == MountType.SKYSQUID)
            customEntity = new FlyingSquid(((CraftPlayer) getPlayer()).getHandle().getWorld());
        else if (getType() == MountType.SLIME)
            customEntity = new CustomSlime(((CraftPlayer) getPlayer()).getHandle().getWorld());
        else if (getType() == MountType.SPIDER)
            customEntity = new RideableSpider(((CraftWorld) getPlayer().getWorld()).getHandle());
        double x = getPlayer().getLocation().getX();
        double y = getPlayer().getLocation().getY();
        double z = getPlayer().getLocation().getZ();
        getCustomEntity().setLocation(x, y + 2, z, 0, 0);

        EntitySpawningManager.setBypass(true);
        ((CraftWorld) getPlayer().getWorld()).getHandle().addEntity(getCustomEntity());
        EntitySpawningManager.setBypass(false);
        UltraCosmetics.getInstance().getEntityUtil().setPassenger(getEntity(), getPlayer());
        CustomEntities.customEntities.add(getCustomEntity());
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (getEntity().getPassenger() != getPlayer() && getCustomEntity().ticksLived > 10) {
                        clear();
                        cancel();
                        return;
                    }
                    if (!getCustomEntity().valid) {
                        cancel();
                        return;
                    }
                    if (owner != null
                            && Bukkit.getPlayer(owner) != null
                            && UltraCosmetics.getCustomPlayer(Bukkit.getPlayer(owner)).currentMount != null
                            && UltraCosmetics.getCustomPlayer(Bukkit.getPlayer(owner)).currentMount.getType() == getType()) {
                        onUpdate();
                    } else {
                        cancel();
                    }

                } catch (NullPointerException exc) {
                    clear();
                    cancel();
                }
            }
        };
        runnable.runTaskTimerAsynchronously(UltraCosmetics.getInstance(), 0, repeatDelay);
        listener = new MountListener(this);

        getPlayer().sendMessage(MessageManager.getMessage("Mounts.Spawn").replace("%mountname%", (UltraCosmetics.getInstance().placeHolderColor) ? getType().getMenuName() : UltraCosmetics.filterColor(getType().getMenuName())));
        UltraCosmetics.getCustomPlayer(getPlayer()).currentMount = this;
    }

    @Override
    protected void removeEntity() {
        getCustomEntity().dead = true;
        CustomEntities.customEntities.remove(customEntity);
    }


    @Override
    public org.bukkit.entity.Entity getEntity() {
        return customEntity.getEntity();
    }

    public Entity getCustomEntity() {
        return ((CraftEntity) customEntity.getEntity()).getHandle();
    }
}
