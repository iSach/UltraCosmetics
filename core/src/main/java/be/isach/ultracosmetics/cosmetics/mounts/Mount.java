package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.run.FallDamageManager;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;


/**
 * Represents an instance of a mount summoned by a player.
 * <p/>
 * TODO:
 * - SubObjects:
 * - HorseMount
 *
 * @author iSach
 * @since 08-03-2015
 */
public abstract class Mount<E extends Entity> extends Cosmetic<MountType> implements Updatable {

    /**
     * The Entity, if it isn't a Custom Entity.
     */
    public E entity;

    protected boolean beingRemoved = false;

    public Mount(UltraPlayer ultraPlayer, MountType type, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.MOUNTS, ultraPlayer, type);
    }

    /**
     * Equips the pet.
     */
    @Override
    public void onEquip() {
        if (getOwner().getCurrentMount() != null) {
            getOwner().removeMount();
        }

        EntitySpawningManager.setBypass(true);
        this.entity = (E) getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), getType().getEntityType());
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
        runTaskTimer(UltraCosmeticsData.get().getPlugin(), 0, getType().getRepeatDelay());
        entity.setMetadata("Mount", new FixedMetadataValue(UltraCosmeticsData.get().getPlugin(), "UltraCosmetics"));
        getOwner().setCurrentMount(this);
    }

    @Override
    public void run() {
        try {
            if (entity.getPassenger() != getPlayer()
                    && entity.getTicksLived() > 10
                    && !beingRemoved) {
                clear();
                cancel();
                return;
            }

            if (!entity.isValid()) {
                cancel();
                return;
            }

            // Prevents players on mounts from being able to fall in the void infinitely.
            if (entity.getLocation().getY() <= -15) {
                clear();
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
            exc.printStackTrace();
            clear();
            cancel();
        }
    }

    @Override
    protected void onClear() {
        if (entity != null) {
			entity.remove();
        }

        if (getOwner() != null)
            getOwner().setCurrentMount(null);

        if (this instanceof MountDragon && !getPlayer().isOnGround())
            FallDamageManager.addNoFall(getPlayer());

        try {
            cancel();
        } catch (Exception exc) {
            //ignore.
        }
    }

    protected void removeEntity() {
        entity.remove();
    }

    public E getEntity() {
        return entity;
    }

    @EventHandler
    public void onPlayerToggleSneakEvent(VehicleExitEvent event) {
        if (event.getVehicle().getType() == EntityType.BOAT
                || event.getVehicle().getType().toString().contains("MINECART")) {
            return;
        }

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
                && event.getVehicle().getCustomName() != null
                && event.getVehicle().getCustomName().equals(name)
                && event.getExited() == getPlayer()
                && !beingRemoved) {
            beingRemoved = true;
            clear();
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() == getEntity()) {
            event.setCancelled(true);
        }

        if (event.getEntity() == getPlayer()
                && getOwner().getCurrentMount() != null
                && getOwner().getCurrentMount().getType() == getType()
                && !getUltraCosmetics().getConfig().getBoolean("allow-damage-to-players-on-mounts")) {
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
                //clear();
            }
        }
    }

    @EventHandler
    public void openInv(InventoryOpenEvent event) {
        if (getType() == MountType.valueOf("druggedhorse")
                || getType() == MountType.valueOf("ecologisthorse")
                || getType() == MountType.valueOf("glacialsteed")
                || getType() == MountType.valueOf("infernalhorror")
                || getType() == MountType.valueOf("mountoffire")
                || getType() == MountType.valueOf("mountofwater")
                || getType() == MountType.valueOf("walkingdead")
                || getType() == MountType.valueOf("rudolph")) {
            if (getOwner() != null
                    && getPlayer() != null
                    && event.getPlayer() == getPlayer()
                    && event.getInventory().equals(((InventoryHolder) entity).getInventory())) {
                event.setCancelled(true);
            }
        }
    }

    public void setBeingRemoved(boolean beingRemoved) {
        this.beingRemoved = beingRemoved;
    }
}