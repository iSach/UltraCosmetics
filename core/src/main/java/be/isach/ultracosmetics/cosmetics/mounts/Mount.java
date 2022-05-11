package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.EntityCosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.run.MountRegionChecker;
import be.isach.ultracosmetics.util.Area;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import be.isach.ultracosmetics.util.ItemFactory;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
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

import com.cryptomorin.xseries.XMaterial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Represents an instance of a mount summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public abstract class Mount extends EntityCosmetic<MountType> implements Updatable {
    private BukkitTask mountRegionTask = null;

    protected boolean beingRemoved = false;
    protected final boolean placesBlocks = getType().doesPlaceBlocks();

    public Mount(UltraPlayer ultraPlayer, MountType type, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.MOUNTS, ultraPlayer, type);
    }

    /**
     * Equips the pet.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onEquip() {

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
        setupEntity();

        if (!getUltraCosmetics().worldGuardHooked()) return;
        // Horses trigger PlayerMoveEvent so the standard WG move handler will be sufficient
        if (isHorse(entity.getType())) return;
        mountRegionTask = new MountRegionChecker(getOwner(), getUltraCosmetics()).runTaskTimer(getUltraCosmetics(), 0, 1);
    }

    @Override
    protected boolean tryEquip() {
        // If the entity is a monster and the world is set to peaceful, we can't spawn it
        if (getType().isMonster() && getPlayer().getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            getOwner().sendMessage(MessageManager.getMessage("Mounts.Cant-Spawn"));
            return false;
        }

        Location center = getPlayer().getLocation();
        center.setY(Math.ceil(center.getY()));
        Area area = new Area(center, 1, 1);
        if (!area.isTransparent()) {
            getOwner().sendMessage(MessageManager.getMessage("Mounts.Not-Enough-Room"));
            return false;
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        if (entity.getPassenger() != getPlayer()
                && entity.getTicksLived() > 10) {
            clear();
            return;
        }

        if (!entity.isValid()) {
            cancel();
            return;
        }

        // Prevents players on mounts from being able to fall in the void infinitely.
        if (entity.getLocation().getY() <= UltraCosmeticsData.get().getVersionManager().getWorldMinHeight(entity.getWorld()) - 15) {
            clear();
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
    }

    @Override
    protected void onClear() {
        if (entity != null) {
            entity.remove();
        }

        if (mountRegionTask != null) {
            mountRegionTask.cancel();
        }
    }

    @EventHandler
    public void onPlayerToggleSneakEvent(VehicleExitEvent event) {
        if (event.getVehicle().getType() == EntityType.BOAT
                || event.getVehicle().getType().toString().contains("MINECART")) {
            return;
        }

        String name = getType().getName(getPlayer());;

        if (!beingRemoved
                && name != null
                && getOwner() != null
                && getPlayer() != null
                && getOwner() != null
                && event.getVehicle() != null
                && event.getExited() != null
                && event.getVehicle().getCustomName() != null
                && event.getVehicle().getCustomName().equals(name)
                && event.getExited() == getPlayer()) {
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
            Map<Block,XMaterial> updates = new HashMap<>();
            for (Block b : BlockUtils.getBlocksInRadius(event.getPlayer().getLocation(), 3, false)) {
                if (b.getLocation().getBlockY() == event.getPlayer().getLocation().getBlockY() - 1) {
                    XMaterial mat = mats.get(RANDOM.nextInt(mats.size()));
                    updates.put(b, mat);
                }
            }
            BlockUtils.setToRestore(updates, 20);
        }
    }
}