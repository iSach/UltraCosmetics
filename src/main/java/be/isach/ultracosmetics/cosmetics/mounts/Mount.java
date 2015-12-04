package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.mounts.customentities.CustomSlime;
import be.isach.ultracosmetics.cosmetics.mounts.customentities.FlyingSquid;
import be.isach.ultracosmetics.cosmetics.mounts.customentities.RideableSpider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public abstract class Mount implements Listener {

    // TODO: Sub-Object for custom entities mounts.

    private Material material;
    private Byte data;
    private String name;

    private MountType type = MountType.DEFAULT;

    public EntityType entityType = EntityType.HORSE;

    private String permission;

    Listener listener;

    UUID owner;

    Horse.Variant variant;
    Horse.Color color;

    public Entity ent;
    public net.minecraft.server.v1_8_R3.Entity customEnt;

    private String description;

    public static List<net.minecraft.server.v1_8_R3.Entity> customEntities = new ArrayList();

    public int repeatDelay = 2;

    public Mount(EntityType entityType, Material material, Byte data, String configName, String permission, final UUID owner, final MountType type, String defaultDescription) {
        this.material = material;
        this.data = data;
        this.name = configName;
        this.permission = permission;
        this.type = type;
        this.entityType = entityType;
        if (SettingsManager.getConfig().get("Mounts." + configName + ".Description") == null) {
            this.description = defaultDescription;
            SettingsManager.getConfig().set("Mounts." + configName + ".Description", getDescriptionWithColor(), "Description of this mount.");
        } else {
            this.description = fromList(((List<String>) SettingsManager.getConfig().get("Mounts." + configName + ".Description")));
        }
        if (owner != null) {
            this.owner = owner;
            if (!getPlayer().hasPermission(permission)) {
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

            if (entityType != EntityType.SQUID
                    && entityType != EntityType.SLIME
                    && entityType != EntityType.SPIDER) {
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

                ((CraftWorld) getPlayer().getWorld()).getHandle().addEntity(customEnt);
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

            getPlayer().sendMessage(MessageManager.getMessage("Mounts.Spawn").replace("%mountname%", (Core.placeHolderColor) ? getMenuName() : Core.filterColor(getMenuName())));
            Core.getCustomPlayer(getPlayer()).currentMount = this;
        }
    }

    public List<String> getDescriptionWithColor() {
        return Arrays.asList(description.split("\n"));
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
        if (entityType != EntityType.SQUID
                && entityType != EntityType.SLIME
                && entityType != EntityType.SPIDER) {
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
            getPlayer().sendMessage(MessageManager.getMessage("Mounts.Despawn").replace("%mountname%", (Core.placeHolderColor) ? getMenuName() : Core.filterColor(getMenuName())));
        owner = null;
        HandlerList.unregisterAll(this);
        HandlerList.unregisterAll(listener);
        onClear();
    }

    public void onClear() {
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
            if (event.getVehicle().getType() == EntityType.BOAT
                    || event.getVehicle().getType().toString().contains("MINECART"))
                return;
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
            if (event.getEntity() == ent || (customEnt != null && event.getEntity() == customEnt.getBukkitEntity()))
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

    public List<String> getDescription() {
        List<String> desc = new ArrayList<>();
        for (String string : description.split("\n")) {
            desc.add(string.replace('&', '§'));
        }
        return desc;
    }

    private String fromList(List<String> description) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < description.size(); i++) {
            stringBuilder.append(description.get(i) + (i < description.size() - 1 ? "\n" : ""));
        }
        return stringBuilder.toString();
    }

    public boolean showsDescription() {
        return SettingsManager.getConfig().getBoolean("Mounts." + getConfigName() + ".Show-Description");
    }

    public boolean canBeFound() {
        return SettingsManager.getConfig().getBoolean("Mounts." + getConfigName() + ".Can-Be-Found-In-Treasure-Chests");
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
        DRAGON("ultracosmetics.mounts.dragon", "Dragon"),
        SKYSQUID("ultracosmetics.mounts.skysquid", "SkySquid"),
        SLIME("ultracosmetics.mounts.slime", "Slime"),
        HYPECART("ultracosmetics.mounts.hypecart", "HypeCart"),
        SPIDER("ultracosmetics.mounts.spider", "Spider"),
        RUDOLPH("ultracosmetics.mounts.rudolph", "Rudolph"),
        MOLTENSNAKE("ultracosmetics.mounts.moltensnake", "MoltenSnake");


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
            return SettingsManager.getConfig().getBoolean("Mounts." + configName + ".Enabled");
        }

    }

}
