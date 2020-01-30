package be.isach.ultracosmetics.v1_15_R1.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.mounts.IMountCustomEntity;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import be.isach.ultracosmetics.v1_15_R1.customentities.CustomEntities;
import be.isach.ultracosmetics.v1_15_R1.customentities.CustomSlime;
import be.isach.ultracosmetics.v1_15_R1.customentities.RideableSpider;
import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;

/**
 * @author RadBuilder
 */
public abstract class MountCustomEntity<E extends org.bukkit.entity.Entity> extends Mount<E> {

    /**
     * Custom Entity.
     */
    public IMountCustomEntity customEntity;

    public MountCustomEntity(UltraPlayer owner, MountType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @Override
    public void onEquip() {

        if (getType() == MountType.valueOf("slime"))
            customEntity = new CustomSlime(EntityTypes.SLIME, ((CraftPlayer) getPlayer()).getHandle().getWorld());
        else if (getType() == MountType.valueOf("spider"))
            customEntity = new RideableSpider(EntityTypes.SPIDER, ((CraftWorld) getPlayer().getWorld()).getHandle());
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

        this.entity = (E) customEntity.getEntity();
        runTaskTimerAsynchronously(UltraCosmeticsData.get().getPlugin(), 0, getType().getRepeatDelay());

        getOwner().setCurrentMount(this);

        if(getType() == MountType.valueOf("slime") || getType() == MountType.valueOf("spider")) {
            if(getPlayer().getWorld().getDifficulty() == Difficulty.PEACEFUL) {
                getOwner().sendMessage("§c§lUltraCosmetics > Monsters can't spawn here!");
                getOwner().removeMount();
            }
        }
    }

    @Override
    public void onUpdate() {
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
        return ((CraftEntity) customEntity.getEntity()).getHandle();
    }
}
