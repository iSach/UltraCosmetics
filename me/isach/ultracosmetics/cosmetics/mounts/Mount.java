package me.isach.ultracosmetics.cosmetics.mounts;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.config.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
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
 */
public abstract class Mount implements Listener {

    private Material material;
    private Byte data;
    private String name;

    private MountType type = MountType.DEFAULT;

    public EntityType entityType = EntityType.HORSE;

    private String permission;

    private Listener listener;

    private UUID owner;

    Horse.Variant variant;
    Horse.Color color;

    public Entity ent;

    public int repeatDelay = 2;

    public Mount(EntityType entityType, Material material, Byte data, String configName, String permission, final UUID owner, final MountType type) {
        this.material = material;
        this.data = data;
        this.name = configName;
        this.permission = permission;
        this.type = type;
        this.entityType = entityType;
        if (owner != null) {
            this.owner = owner;
            if (!getPlayer().hasPermission(permission)) {
                getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
                return;
            }
            if (type == MountType.NYANSHEEP || type == MountType.DRAGON)
                repeatDelay = 1;
            if (Core.getCustomPlayer(getPlayer()).currentMount != null)
                Core.getCustomPlayer(getPlayer()).removeMount();

            this.ent = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), getEntityType());
            if (ent instanceof Ageable) {
                ((Ageable) ent).setAdult();
            } else {
                if (ent instanceof Slime) {
                    ((Slime) ent).setSize(4);
                }
            }
            ent.setCustomNameVisible(true);
            ent.setCustomName(getName());
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
            runnable.runTaskTimer(Core.getPlugin(), 0, repeatDelay);
            listener = new MountListener(this);

            getPlayer().sendMessage(MessageManager.getMessage("Mounts.Spawn").replace("%mountname%", getMenuName()));
            Core.getCustomPlayer(getPlayer()).currentMount = this;
            ent.setMetadata("Mount", new FixedMetadataValue(Core.getPlugin(), "UltraCosmetics"));
        }
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getName() {
        return MessageManager.getMessage("Mounts." + name + ".entity-displayname").replace("%playername%", getPlayer().getName());
    }

    public String getMenuName() {
        return MessageManager.getMessage("Mounts." + name + ".menu-name");
    }

    public String getConfigName() {
        return name;
    }

    public Material getMaterial() {
        return this.material;
    }


    public MountType getType() {
        return this.type;
    }

    public Byte getData() {
        return this.data;
    }

    abstract void onUpdate();

    public void clear() {
        if (getPlayer() != null && Core.getCustomPlayer(getPlayer()) != null) {
            Core.getCustomPlayer(getPlayer()).currentMount = null;
            getPlayer().removePotionEffect(PotionEffectType.CONFUSION);
        }
        if (ent.getPassenger() != null)
            ent.getPassenger().eject();
        if (ent != null)
            ent.remove();
        if (getPlayer() != null)
            getPlayer().sendMessage(MessageManager.getMessage("Mounts.Despawn").replace("%mountname%", getMenuName()));
        owner = null;
        HandlerList.unregisterAll(this);
        HandlerList.unregisterAll(listener);
    }

    protected UUID getOwner() {
        return owner;
    }

    protected Player getPlayer() {
        return Bukkit.getPlayer(owner);
    }

    public class MountListener implements Listener {
        private Mount mount;

        public MountListener(Mount mount) {
            this.mount = mount;
            Core.registerListener(this);
        }

        @EventHandler
        public void onPlayerToggleSneakEvent(VehicleExitEvent event) {
            String name = null;
            try {
                name = getName();
            } catch (Exception e) {
            }

            if (name != null && getPlayer() != null /*&& event.getVehicle().getCustomName() != null*/ && event.getVehicle().getCustomName().equals(name) && event.getExited() == getPlayer()) {
                Core.getCustomPlayer(getPlayer()).removeMount();
            }
        }

        @EventHandler
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.getEntity() == ent)
                event.setCancelled(true);
            if (event.getEntity() == getPlayer()
                    && Core.getCustomPlayer(getPlayer()).currentMount != null
                    && Core.getCustomPlayer(getPlayer()).currentMount.getType() == getType()) {
                event.setCancelled(true);
            }
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

    public enum MountType {

        DEFAULT("", ""),
        DRUGGEDHORSE("ultracosmetics.mounts.druggedhorse", "DruggedHorse"),
        INFERNALHORROR("ultracosmetics.mounts.infernalhorror", "InfernalHorror"),
        GLACIALSTEED("ultracosmetics.mounts.glacialsteed", "GlacialSteed"),
        WALKINGDEAD("ultracosmetics.mounts.walkingdead", "WalkingDead"),
        MOUNTOFFIRE("ultracosmetics.mounts.mountoffire", "MountOfFire"),
        MOUNTOFWATER("ultracosmetics.mounts.mountofwater", "MountOfWater"),
        ECOLOGISTHORSE("ultracosmetics.mounts.ecologisthorse", "EcologistHorse"),
        SNAKE("ultracosmetics.mounts.snake", "Snake"),
        NYANSHEEP("ultracosmetics.mounts.nyansheep", "NyanSheep"),
        DRAGON("ultracosmetics.mounts.dragon", "Dragon");


        String permission;
        String configName;

        MountType(String permission, String configName) {
            this.permission = permission;
            this.configName = configName;
        }

        public String getPermission() {
            return permission;
        }

        public boolean isEnabled() {
            return SettingsManager.getConfig().get("Mounts." + configName + ".Enabled");
        }

    }

}
