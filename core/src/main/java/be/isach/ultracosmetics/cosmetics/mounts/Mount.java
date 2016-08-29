package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by sacha on 03/08/15.
 * <p/>
 * TODO:
 * - SubObjects:
 * - HorseMount
 */
public abstract class Mount extends Cosmetic<MountType> implements Updatable {

    /**
     * If the mount is a horse, its variant.
     */
    public Horse.Variant variant;
    /**
     * If the mount is a horse, its color.
     */
    public Horse.Color color;
    /**
     * The Entity, if it isn't a Custom Entity.
     */
    public Entity entity;

    public Mount(UltraPlayer ultraPlayer, MountType type, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.MOUNTS, ultraPlayer, type);

        if (ultraPlayer.getCurrentMount() != null) {
            ultraPlayer.removeMount();
        }
    }

    /**
     * Equips the pet.
     */
    @Override
    public void onEquip() {
        EntitySpawningManager.setBypass(true);
        this.entity = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), getType().getEntityType());
        EntitySpawningManager.setBypass(false);
        if (entity instanceof Ageable) {
            ((Ageable) entity).setAdult();
        } else {
            if (entity instanceof Slime) {
                ((Slime) entity).setSize(4);
            }
        }
        entity.setCustomNameVisible(true);
        entity.setCustomName(getType().getName(getPlayer()));
        entity.setPassenger(getPlayer());
        if (entity instanceof Horse) {
            ((Horse) entity).setDomestication(1);
            ((Horse) entity).getInventory().setSaddle(new ItemStack(Material.SADDLE));
        }
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (entity.getPassenger() != getPlayer() && entity.getTicksLived() > 10) {
                        clear();
                        cancel();
                        return;
                    }
                    if (!entity.isValid()) {
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
        runnable.runTaskTimerAsynchronously(UltraCosmeticsData.get().getPlugin(), 0, getType().getRepeatDelay());
        entity.setMetadata("Mount", new FixedMetadataValue(UltraCosmeticsData.get().getPlugin(), "UltraCosmetics"));

//        getPlayer().sendMessage(MessageManager.getMessage("Mounts.Spawn").replace("%mountname%", (getUltraCosmetics().placeHolderColor) ? getType().get() : UltraCosmetics.filterColor(getType().getMenuName())));
        getOwner().setCurrentMount(this);
    }

    protected void removeEntity() {
        entity.remove();
    }

    public Entity getEntity() {
        return entity;
    }

    @EventHandler
    public void onPlayerToggleSneakEvent(VehicleExitEvent event) {
        if (event.getVehicle().getType() == EntityType.BOAT
                || event.getVehicle().getType().toString().contains("MINECART"))
            return;
        String name = null;
        try {
            name = getType().getName(getPlayer());
        } catch (Exception e) {
        }

        if (name != null
                && getOwner() != null
                && getPlayer() != null
                && getOwner() != null
                && event.getVehicle() != null
                && event.getExited() != null
                && event.getVehicle().getCustomName().equals(name)
                && event.getExited() == getPlayer()) {
            getOwner().removeMount();
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() == getEntity())
            event.setCancelled(true);
        if (event.getEntity() == getPlayer()
                && getOwner().getCurrentMount() != null
                && getOwner().getCurrentMount().getType() == getType()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() == getEntity())
            event.setCancelled(true);
    }

    @EventHandler
    public void teleportEvent(PlayerTeleportEvent event) {
        if (getOwner() != null
                && getPlayer() != null
                && getOwner().getCurrentMount() == this
                && event.getPlayer() == getPlayer()) {
            if ((event.getFrom().getBlockX() != event.getTo().getBlockX()
                    || event.getFrom().getBlockY() != event.getTo().getBlockY()
                    || event.getFrom().getBlockZ() != event.getTo().getBlockZ()
                    || !event.getFrom().getWorld().getName().equalsIgnoreCase(event.getTo().getWorld().getName()))) {
                clear();
            }
        }
    }
}
