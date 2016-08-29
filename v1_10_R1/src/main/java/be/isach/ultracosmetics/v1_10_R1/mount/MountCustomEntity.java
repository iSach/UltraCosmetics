package be.isach.ultracosmetics.v1_10_R1.mount;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.TextUtil;
import be.isach.ultracosmetics.v1_10_R1.customentities.CustomSlime;
import be.isach.ultracosmetics.v1_10_R1.customentities.FlyingSquid;
import be.isach.ultracosmetics.v1_10_R1.customentities.RideableSpider;
import be.isach.ultracosmetics.v1_10_R1.customentities.CustomEntities;
import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import net.minecraft.server.v1_10_R1.Entity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Sacha on 15/03/16.
 */
public abstract class MountCustomEntity extends Mount {

    /**
     * Custom Entity.
     */
    public IMountCustomEntity customEntity;

    public MountCustomEntity(UltraPlayer owner, MountType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @Override
    public void onEquip() {
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
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().setPassenger(getEntity(), getPlayer());
        CustomEntities.customEntities.add(getCustomEntity());
        customEntity.removeAi();
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
                    if (getOwner() != null
                            && Bukkit.getPlayer(getOwnerUniqueId()) != null
                            && getOwner().getCurrentMount() != null
                            && getOwner().getCurrentMount().getType() == getType()) {
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
        runnable.runTaskTimerAsynchronously(getUltraCosmetics(), 0, getType().getRepeatDelay());

        getPlayer().sendMessage(MessageManager.getMessage("Mounts.Spawn").replace("%mountname%", TextUtil.filterPlaceHolder(getType().getMenuName(), getUltraCosmetics())));
        getOwner().setCurrentMount(this);
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
