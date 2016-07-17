package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 * <p/>
 * TODO:
 * - SubObjects:
 * - HorseMount
 */
public abstract class Mount implements Listener {

    /**
     * The Mount Owner's UUID.
     */
    public UUID owner;
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
    /**
     * The delay between each mount ticking.
     */
    public int repeatDelay = 2;
    /**
     * The Type of the Mount.
     */
    private MountType type;
    /**
     * The Event Listener.
     */
    protected Listener listener;

    private UltraCosmetics ultraCosmetics;

    public Mount(final UUID owner, final MountType type, UltraCosmetics ultraCosmetics) {
        this.type = type;
        this.owner = owner;
        this.ultraCosmetics = ultraCosmetics;
        if (owner != null) {
            if (!getPlayer().hasPermission(type.getPermission())) {
                getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
                return;
            }
        }
        if (type == MountType.NYANSHEEP || type == MountType.DRAGON
                || type == MountType.HYPECART
                || type == MountType.MOLTENSNAKE)
            repeatDelay = 1;
        if (type == MountType.SKYSQUID)
            repeatDelay = 4;
        if (UltraCosmetics.getCustomPlayer(getPlayer()).currentMount != null)
            UltraCosmetics.getCustomPlayer(getPlayer()).removeMount();
    }

    /**
     * Equips the pet.
     */
    public void equip() {
        EntitySpawningManager.setBypass(true);
        this.entity = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), type.getEntityType());
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
                    if (owner != null
                            && Bukkit.getPlayer(owner) != null
                            && UltraCosmetics.getCustomPlayer(Bukkit.getPlayer(owner)).currentMount != null
                            && UltraCosmetics.getCustomPlayer(Bukkit.getPlayer(owner)).currentMount.getType() == type) {
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
        entity.setMetadata("Mount", new FixedMetadataValue(UltraCosmetics.getInstance(), "UltraCosmetics"));
        listener = new MountListener(this);

        getPlayer().sendMessage(MessageManager.getMessage("Mounts.Spawn").replace("%mountname%", (UltraCosmetics.getInstance().placeHolderColor) ? getType().getMenuName() : UltraCosmetics.filterColor(getType().getMenuName())));
        UltraCosmetics.getCustomPlayer(getPlayer()).currentMount = this;

        onEquip();
    }

    protected void onEquip() {}

    /**
     * Gets the Mount Type.
     *
     * @return The Mount Type.
     */
    public MountType getType() {
        return this.type;
    }

    /**
     * Called with an interval of {repeatDelay} ticks.
     */
    protected abstract void onUpdate();

    /**
     * Clears the Mount.
     */
    public void clear() {
        if (getPlayer() != null && UltraCosmetics.getCustomPlayer(getPlayer()) != null) {
            UltraCosmetics.getCustomPlayer(getPlayer()).currentMount = null;
            getPlayer().removePotionEffect(PotionEffectType.CONFUSION);
        }
        removeEntity();
        if (getPlayer() != null)
            getPlayer().sendMessage(MessageManager.getMessage("Mounts.Despawn").replace("%mountname%", (UltraCosmetics.getInstance().placeHolderColor) ? type.getMenuName() : UltraCosmetics.filterColor(type.getMenuName())));
        owner = null;
        HandlerList.unregisterAll(this);
        HandlerList.unregisterAll(listener);
        onClear();
    }

    /**
     * Called when mount is cleared.
     */
    public void onClear() {
    }

    protected void removeEntity() {
        entity.remove();
    }

    public Entity getEntity() {
        return entity;
    }

    /**
     * Gets the Owner as a UUID.
     *
     * @return The Owner as a UUID.
     */
    protected UUID getOwner() {
        return owner;
    }

    /**
     * Gets the owner as a player.
     *
     * @return the owner as a player.
     */
    protected Player getPlayer() {
        return Bukkit.getPlayer(owner);
    }

    protected UltraCosmetics getUltraCosmetics() {
        return ultraCosmetics;
    }

    /**
     * The Event Listener.
     */
    public class MountListener implements Listener {
        private Mount mount;

        public MountListener(Mount mount) {
            this.mount = mount;
            UltraCosmetics.getInstance().registerListener(this);
        }

        @EventHandler
        public void onPlayerToggleSneakEvent(VehicleExitEvent event) {
            if (event.getVehicle().getType() == EntityType.BOAT
                    || event.getVehicle().getType().toString().contains("MINECART"))
                return;
            String name = null;
            try {
                name = type.getName(getPlayer());
            } catch (Exception e) {
            }

            if (name != null
                    && owner != null
                    && getPlayer() != null
                    && UltraCosmetics.getCustomPlayer(getPlayer()) != null
                    && event.getVehicle() != null
                    && event.getExited() != null
                    && event.getVehicle().getCustomName().equals(name)
                    && event.getExited() == getPlayer()) {
                UltraCosmetics.getCustomPlayer(getPlayer()).removeMount();
            }
        }

        @EventHandler
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.getEntity() == mount.getEntity())
                event.setCancelled(true);
            if (event.getEntity() == getPlayer()
                    && UltraCosmetics.getCustomPlayer(getPlayer()).currentMount != null
                    && UltraCosmetics.getCustomPlayer(getPlayer()).currentMount.getType() == getType()) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
            if (event.getEntity() == mount.getEntity())
                event.setCancelled(true);
        }

        @EventHandler
        public void teleportEvent(PlayerTeleportEvent event) {
            if (owner != null && getPlayer() != null && UltraCosmetics.getCustomPlayer(getPlayer()).currentMount == mount && event.getPlayer() == getPlayer()) {
                if ((event.getFrom().getBlockX() != event.getTo().getBlockX()
                        || event.getFrom().getBlockY() != event.getTo().getBlockY()
                        || event.getFrom().getBlockZ() != event.getTo().getBlockZ()
                        || !event.getFrom().getWorld().getName().equalsIgnoreCase(event.getTo().getWorld().getName()))) {
                    clear();
                }
            }
        }
    }

}
