package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.mounts.customentities.CustomSlime;
import be.isach.ultracosmetics.cosmetics.mounts.customentities.FlyingSquid;
import be.isach.ultracosmetics.cosmetics.mounts.customentities.RideableSpider;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 * <p/>
 * TODO: SubObjects:
 * - CustomEntityMount
 * - HorseMount
 */
public abstract class Mount implements Listener {

    /**
     * List of all the CustomEntities. (STATIC)
     */
    public static List<net.minecraft.server.v1_8_R3.Entity> customEntities = new ArrayList();

    /**
     * The Type of the Mount.
     */
    private MountType type;

    /**
     * The Event Listener.
     */
    private Listener listener;

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
    public Entity ent;

    /**
     * The CustomEntity if it is a Custom Entity.
     */
    public net.minecraft.server.v1_8_R3.Entity customEnt;

    /**
     * The delay between each mount ticking.
     */
    public int repeatDelay = 2;

    public Mount(final UUID owner, final MountType type) {
        this.type = type;
        if (owner != null) {
            this.owner = owner;
            if (!getPlayer().hasPermission(type.getPermission())) {
                getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
                return;
            }
            if (type == MountType.NYANSHEEP || type == MountType.DRAGON
                    || type == MountType.HYPECART
                    || type == MountType.MOLTENSNAKE)
                repeatDelay = 1;
            if (type == MountType.SKYSQUID)
                repeatDelay = 4;
            if (Core.getCustomPlayer(getPlayer()).currentMount != null)
                Core.getCustomPlayer(getPlayer()).removeMount();

            if (type.getEntityType() != EntityType.SQUID
                    && type.getEntityType() != EntityType.SLIME
                    && type.getEntityType() != EntityType.SPIDER) {
                EntitySpawningManager.setBypass(true);
                this.ent = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), type.getEntityType());
                EntitySpawningManager.setBypass(false);
                if (ent instanceof Ageable) {
                    ((Ageable) ent).setAdult();
                } else {
                    if (ent instanceof Slime) {
                        ((Slime) ent).setSize(4);
                    }
                }
                ent.setCustomNameVisible(true);
                ent.setCustomName(getType().getName(getPlayer()));
                ent.setPassenger(getPlayer());
                if (ent instanceof Horse) {
                    ((Horse) ent).setDomestication(1);
                    ((Horse) ent).getInventory().setSaddle(new ItemStack(Material.SADDLE));
                }
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            if (ent.getPassenger() != getPlayer() && ent.getTicksLived() > 10) {
                                clear();
                                cancel();
                                return;
                            }
                            if (!ent.isValid()) {
                                cancel();
                                return;
                            }
                            if (owner != null
                                    && Bukkit.getPlayer(owner) != null
                                    && Core.getCustomPlayer(Bukkit.getPlayer(owner)).currentMount != null
                                    && Core.getCustomPlayer(Bukkit.getPlayer(owner)).currentMount.getType() == type) {
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
                runnable.runTaskTimerAsynchronously(Core.getPlugin(), 0, repeatDelay);
                ent.setMetadata("Mount", new FixedMetadataValue(Core.getPlugin(), "UltraCosmetics"));
            } else {
                if (getType() == MountType.SKYSQUID)
                    customEnt = new FlyingSquid(((CraftPlayer) getPlayer()).getHandle().getWorld());
                else if (getType() == MountType.SLIME)
                    customEnt = new CustomSlime(((CraftPlayer) getPlayer()).getHandle().getWorld());
                else if (getType() == MountType.SPIDER)
                    customEnt = new RideableSpider(((CraftPlayer) getPlayer()).getHandle().getWorld());
                double x = getPlayer().getLocation().getX();
                double y = getPlayer().getLocation().getY();
                double z = getPlayer().getLocation().getZ();
                customEnt.setLocation(x, y + 2, z, 0, 0);

                EntitySpawningManager.setBypass(true);
                ((CraftWorld) getPlayer().getWorld()).getHandle().addEntity(customEnt);
                EntitySpawningManager.setBypass(false);
                customEnt.getBukkitEntity().setPassenger(getPlayer());
                customEntities.add(customEnt);
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            if (customEnt.getBukkitEntity().getPassenger() != getPlayer() && customEnt.ticksLived > 10) {
                                clear();
                                cancel();
                                return;
                            }
                            if (!customEnt.valid) {
                                cancel();
                                return;
                            }
                            if (owner != null
                                    && Bukkit.getPlayer(owner) != null
                                    && Core.getCustomPlayer(Bukkit.getPlayer(owner)).currentMount != null
                                    && Core.getCustomPlayer(Bukkit.getPlayer(owner)).currentMount.getType() == type) {
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
                runnable.runTaskTimerAsynchronously(Core.getPlugin(), 0, repeatDelay);
            }
            listener = new MountListener(this);

            getPlayer().sendMessage(MessageManager.getMessage("Mounts.Spawn").replace("%mountname%", (Core.placeHolderColor) ? type.getMenuName() : Core.filterColor(type.getMenuName())));
            Core.getCustomPlayer(getPlayer()).currentMount = this;
        }
    }

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
    abstract void onUpdate();

    /**
     * Clears the Mount.
     */
    public void clear() {
        if (getPlayer() != null && Core.getCustomPlayer(getPlayer()) != null) {
            Core.getCustomPlayer(getPlayer()).currentMount = null;
            getPlayer().removePotionEffect(PotionEffectType.CONFUSION);
        }
        if (type.getEntityType() != EntityType.SQUID
                && type.getEntityType() != EntityType.SLIME
                && type.getEntityType() != EntityType.SPIDER) {
            if (ent.getPassenger() != null)
                ent.getPassenger().eject();
            if (ent != null)
                ent.remove();
        } else {
            if (customEnt.passenger != null)
                customEnt.passenger = null;
            if (customEnt != null) {
                customEntities.remove(customEnt);
                customEnt.dead = true;
            }
        }
        if (getPlayer() != null)
            getPlayer().sendMessage(MessageManager.getMessage("Mounts.Despawn").replace("%mountname%", (Core.placeHolderColor) ? type.getMenuName() : Core.filterColor(type.getMenuName())));
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
     * @return the owner as a player.
     */
    protected Player getPlayer() {
        return Bukkit.getPlayer(owner);
    }

    /**
     * The Event Listener.
     */
    public class MountListener implements Listener {
        private Mount mount;

        public MountListener(Mount mount) {
            this.mount = mount;
            Core.registerListener(this);
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
                    && event.getVehicle().getCustomName().equals(name)
                    && event.getExited() == getPlayer()) {
                Core.getCustomPlayer(getPlayer()).removeMount();
            }
        }

        @EventHandler
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.getEntity() == ent || (customEnt != null && event.getEntity() == customEnt.getBukkitEntity()))
                event.setCancelled(true);
            if (event.getEntity() == getPlayer()
                    && Core.getCustomPlayer(getPlayer()).currentMount != null
                    && Core.getCustomPlayer(getPlayer()).currentMount.getType() == getType()) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
            if ((ent != null && event.getDamager() == ent)
                    || (customEnt != null && event.getDamager() == customEnt.getBukkitEntity()))
                event.setCancelled(true);
        }

        @EventHandler
        public void teleportEvent(PlayerTeleportEvent event) {
            if (owner != null && getPlayer() != null && Core.getCustomPlayer(getPlayer()).currentMount == mount && event.getPlayer() == getPlayer()) {
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
