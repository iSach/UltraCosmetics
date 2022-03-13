package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.run.MountRegionChecker;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.XMaterial;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;


/**
 * Represents an instance of a mount summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public abstract class Mount<E extends Entity> extends Cosmetic<MountType> implements Updatable {
    private BukkitTask mountRegionTask = null;
    /**
     * The Entity, if it isn't a Custom Entity.
     */
    protected E entity;

    protected boolean beingRemoved = false;
    protected final boolean placesBlocks = getType().doesPlaceBlocks();

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

        // If the entity is a monster and the world is set to peaceful, we can't spawn it
        if ((Monster.class.isAssignableFrom(getType().getEntityType().getEntityClass())
                // no idea why Slime doesn't implement Monster but we have to check for it
                || Slime.class.isAssignableFrom(getType().getEntityType().getEntityClass()))
                && getPlayer().getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            getOwner().sendMessage(MessageManager.getMessage("Mounts.Cant-Spawn"));
            clear();
            return;
        }

        EntitySpawningManager.setBypass(true);
        entity = spawnEntity();
        EntitySpawningManager.setBypass(false);
        if (entity instanceof LivingEntity) {
            UltraCosmeticsData.get().getVersionManager().getAncientUtil().setSpeed((LivingEntity)entity, getType().getMovementSpeed());
            if (entity instanceof Ageable) {
                ((Ageable) entity).setAdult();
            } else if (entity instanceof Slime) {
                ((Slime) entity).setSize(3);
            }
        }
        entity.setCustomNameVisible(true);
        entity.setCustomName(getType().getName(getPlayer()));
        entity.setPassenger(getPlayer());
        runTaskTimer(UltraCosmeticsData.get().getPlugin(), 0, getType().getRepeatDelay());
        entity.setMetadata("Mount", new FixedMetadataValue(UltraCosmeticsData.get().getPlugin(), "UltraCosmetics"));
        getOwner().setCurrentMount(this);

        if (!getUltraCosmetics().worldGuardHooked()) return;
        // Horses trigger PlayerMoveEvent so the standard WG move handler will be sufficient
        if (isHorse(entity.getType())) return;
        mountRegionTask = new MountRegionChecker(getOwner(), getUltraCosmetics()).runTaskTimer(getUltraCosmetics(), 0, 1);
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
            if (entity.getLocation().getY() <= UltraCosmeticsData.get().getVersionManager().getWorldMinHeight(entity.getWorld()) - 15) {
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

        if (getOwner() != null) {
            getOwner().setCurrentMount(null);
        }

        if (mountRegionTask != null) {
            mountRegionTask.cancel();
        }
    }

    protected void removeEntity() {
        entity.remove();
    }

    public E getEntity() {
        return entity;
    }

    @SuppressWarnings("unchecked")
    protected E spawnEntity() {
        return (E) getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), getType().getEntityType());
    }

    @EventHandler
    public void onPlayerToggleSneakEvent(VehicleExitEvent event) {
        if (event.getVehicle().getType() == EntityType.BOAT
                || event.getVehicle().getType().toString().contains("MINECART")) {
            return;
        }

        String name = getType().getName(getPlayer());;

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
        if (event.getEntity() == getEntity() || event.getDamager() == getEntity()) {
            event.setCancelled(true);
        }
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
        if (!isHorse(getType().getEntityType())) return;
        if (getOwner() != null
                && getPlayer() != null
                && event.getPlayer() == getPlayer()
                && event.getInventory().equals(((InventoryHolder) entity).getInventory())) {
            event.setCancelled(true);
        }
    }

    public void setBeingRemoved(boolean beingRemoved) {
        this.beingRemoved = beingRemoved;
    }

    private boolean isHorse(EntityType type) {
        return UltraCosmeticsData.get().getVersionManager().getMounts().isAbstractHorse(type);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (placesBlocks
                && event.getPlayer() == getPlayer()
                && getOwner().getCurrentMount() == this
                && SettingsManager.getConfig().getBoolean("Mounts-Block-Trails")) {
            List<XMaterial> mats = ItemFactory.getXMaterialListFromConfig("Mounts." + getType().getConfigName() + ".Blocks-To-Place");
            if (mats.size() == 0) {
                return;
            }
            for (Block b : BlockUtils.getBlocksInRadius(event.getPlayer().getLocation(), 3, false)) {
                if (b.getLocation().getBlockY() == event.getPlayer().getLocation().getBlockY() - 1) {
                    XMaterial mat = mats.get(RANDOM.nextInt(mats.size()));
                    BlockUtils.setToRestore(b, mat, 20);
                }
            }
        }
    }
}