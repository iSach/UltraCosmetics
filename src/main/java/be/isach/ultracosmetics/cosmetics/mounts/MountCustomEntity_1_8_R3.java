package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.mounts.customentities.v1_8_R3.CustomEntities_1_8_R3;
import be.isach.ultracosmetics.cosmetics.mounts.customentities.v1_8_R3.CustomSlime_1_8_R3;
import be.isach.ultracosmetics.cosmetics.mounts.customentities.v1_8_R3.FlyingSquid_1_8_R3;
import be.isach.ultracosmetics.cosmetics.mounts.customentities.v1_8_R3.RideableSpider_1_8_R3;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Created by Sacha on 15/03/16.
 */
public class MountCustomEntity_1_8_R3 extends Mount {

    /**
     * Custom Entity.
     */
    public IMountCustomEntity customEntity;

    public MountCustomEntity_1_8_R3(UUID owner, MountType type) {
        super(owner, type);
    }

    @Override
    public void equip() {
        if (getType() == MountType.SKYSQUID)
            customEntity = new FlyingSquid_1_8_R3(((CraftPlayer) getPlayer()).getHandle().getWorld());
        else if (getType() == MountType.SLIME)
            customEntity = new CustomSlime_1_8_R3(((CraftPlayer) getPlayer()).getHandle().getWorld());
        else if (getType() == MountType.SPIDER)
            customEntity = new RideableSpider_1_8_R3(((CraftPlayer) getPlayer()).getHandle().getWorld());
        double x = getPlayer().getLocation().getX();
        double y = getPlayer().getLocation().getY();
        double z = getPlayer().getLocation().getZ();
        getCustomEntity().setLocation(x, y + 2, z, 0, 0);

        EntitySpawningManager.setBypass(true);
        ((CraftWorld) getPlayer().getWorld()).getHandle().addEntity(getCustomEntity());
        EntitySpawningManager.setBypass(false);
        UltraCosmetics.getInstance().getEntityUtil().setPassenger(getEntity(), getPlayer());
        CustomEntities_1_8_R3.customEntities.add(getCustomEntity());
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
        CustomEntities_1_8_R3.customEntities.remove(customEntity);
    }


    @Override
    public org.bukkit.entity.Entity getEntity() {
        return customEntity.getEntity();
    }

    public Entity getCustomEntity() {
        return ((CraftEntity) customEntity.getEntity()).getHandle();
    }

    @Override
    void onUpdate() {

    }
}
