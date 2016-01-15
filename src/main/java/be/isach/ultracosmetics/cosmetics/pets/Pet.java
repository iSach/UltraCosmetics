package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.pets.customentities.Pumpling;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PathEntity;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sacha on 03/08/15.
 */
public abstract class Pet implements Listener {

    /**
     * Static list of all the custom entities.
     */
    public static List<net.minecraft.server.v1_8_R3.Entity> customEntities = new ArrayList();

    /**
     * List of items popping out from Pet.
     */
    public ArrayList<Item> items = new ArrayList<>();

    /**
     * Pet Type of the pet.
     */
    private PetType type;

    /**
     * Armor stand which is the name of the pet.
     */
    public ArmorStand armorStand;

    /**
     * Current owner of this pet.
     */
    private UUID owner;

    /**
     * Event listener.
     * Listens for pet damage.
     */
    private Listener listener;
    
    /**
     * Runs the task for pets following players
     */
    private ExecutorService pathUpdater;
    
    /**
     * Task that forces pets to follow player
     */
    private Runnable followTask;

    /**
     * If Pet is a normal entity, it will be stored here.
     */
    public Entity entity;

    /**
     * If the pet is a custom entity, it'll be stored here.
     */
    public net.minecraft.server.v1_8_R3.Entity customEnt;

    public Pet(final UUID owner, final PetType type) {
        this.type = type;
        this.pathUpdater = Executors.newSingleThreadExecutor();
        this.followTask = new FollowPlayer();

        if (owner == null) return;

        this.owner = owner;
        if (!getPlayer().hasPermission(getType().getPermission())) {
            getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
            return;
        }
        if (Core.getCustomPlayer(getPlayer()).currentPet != null)
            Core.getCustomPlayer(getPlayer()).removePet();
        Core.getCustomPlayer(getPlayer()).currentPet = this;

        final Pet pet = this;
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (getType().getEntityType() == EntityType.ZOMBIE) {
                        if (!customEnt.valid) {
                            if (armorStand != null)
                                armorStand.remove();
                            customEnt.dead = true;
                            if (getPlayer() != null)
                                Core.getCustomPlayer(getPlayer()).currentPet = null;
                            for (Item i : items)
                                i.remove();
                            items.clear();
                            try {
                                HandlerList.unregisterAll(pet);
                                HandlerList.unregisterAll(listener);
                            } catch (Exception exc) {
                            }
                            cancel();
                            return;
                        }
                    } else {
                        if (!entity.isValid()) {
                            if (armorStand != null)
                                armorStand.remove();
                            entity.remove();
                            if (getPlayer() != null)
                                Core.getCustomPlayer(getPlayer()).currentPet = null;
                            for (Item i : items)
                                i.remove();
                            items.clear();
                            try {
                                HandlerList.unregisterAll(pet);
                                HandlerList.unregisterAll(listener);
                            } catch (Exception exc) {
                            }
                            cancel();
                            return;
                        }
                    }
                    if (Bukkit.getPlayer(owner) != null
                            && Core.getCustomPlayer(Bukkit.getPlayer(owner)).currentPet != null
                            && Core.getCustomPlayer(Bukkit.getPlayer(owner)).currentPet.getType() == type) {
                        if (SettingsManager.getConfig().getBoolean("Pets-Drop-Items"))
                            onUpdate();
                        pathUpdater.submit(followTask);
                    } else {
                        cancel();
                        if (armorStand != null)
                            armorStand.remove();
                        for (Item i : items)
                            i.remove();
                        items.clear();
                        clear();
                        return;
                    }
                    if (armorStand != null) {
                        if (getType().getEntityType() == EntityType.ZOMBIE)
                            customEnt.getBukkitEntity().setPassenger(armorStand);
                        else
                            entity.setPassenger(armorStand);
                        new Thread() {
                            @Override
                            public void run() {
                                for (Player player : getPlayer().getWorld().getPlayers())
                                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
                                            new PacketPlayOutEntityTeleport(((CraftArmorStand) armorStand).getHandle()));
                            }
                        }.run();
                    }
                } catch (NullPointerException exc) {
                    exc.printStackTrace();
                    cancel();
                    if (armorStand != null)
                        armorStand.remove();
                    for (Item i : items)
                        i.remove();
                    items.clear();
                    clear();
                }
            }
        };
        runnable.runTaskTimer(Core.getPlugin(), 0, 6);
        listener = new PetListener(this);

        if (getType().getEntityType() != EntityType.ZOMBIE) {
            EntitySpawningManager.setBypass(true);
            this.entity = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), getType().getEntityType());
            EntitySpawningManager.setBypass(false);
            if (entity instanceof Ageable) {
                if (SettingsManager.getConfig().getBoolean("Pets-Are-Babies"))
                    ((Ageable) entity).setBaby();
                else
                    ((Ageable) entity).setAdult();
                ((Ageable) entity).setAgeLock(true);
            }
            net.minecraft.server.v1_8_R3.Entity entity = ((CraftEntity) this.entity).getHandle();
            try {
                Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
                bField.setAccessible(true);
                Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
                cField.setAccessible(true);
                bField.set(((EntityInsentient) entity).goalSelector, new UnsafeList<PathfinderGoalSelector>());
                bField.set(((EntityInsentient) entity).targetSelector, new UnsafeList<PathfinderGoalSelector>());
                cField.set(((EntityInsentient) entity).goalSelector, new UnsafeList<PathfinderGoalSelector>());
                cField.set(((EntityInsentient) entity).targetSelector, new UnsafeList<PathfinderGoalSelector>());
            } catch (Exception exc) {
                exc.printStackTrace();
            }

            if (getType().getEntityType() != EntityType.WITHER) {

                armorStand = (ArmorStand) this.entity.getWorld().spawnEntity(this.entity.getLocation(), EntityType.ARMOR_STAND);
                armorStand.setVisible(false);
                armorStand.setSmall(true);
                armorStand.setCustomName(getType().getEntityName(getPlayer()));
                armorStand.setCustomNameVisible(true);

                if (Core.getCustomPlayer(getPlayer()).getPetName(getType().getConfigName()) != null)
                    armorStand.setCustomName(Core.getCustomPlayer(getPlayer()).getPetName(getType().getConfigName()));

                this.entity.setPassenger(armorStand);
            } else {
                this.entity.setCustomName(getType().getEntityName(getPlayer()));
                this.entity.setCustomNameVisible(true);

                if (Core.getCustomPlayer(getPlayer()).getPetName(getType().getConfigName()) != null)
                    this.entity.setCustomName(Core.getCustomPlayer(getPlayer()).getPetName(getType().getConfigName()));
            }
            this.entity.setMetadata("Pet", new FixedMetadataValue(Core.getPlugin(), "UltraCosmetics"));

        } else {
            customEnt = new Pumpling(((CraftPlayer) getPlayer()).getHandle().getWorld());
            customEntities.add(customEnt);
            double x = getPlayer().getLocation().getX();
            double y = getPlayer().getLocation().getY();
            double z = getPlayer().getLocation().getZ();
            customEnt.setLocation(x, y, z, 0, 0);
            armorStand = (ArmorStand) customEnt.getBukkitEntity().getWorld().spawnEntity(customEnt.getBukkitEntity().getLocation(), EntityType.ARMOR_STAND);
            armorStand.setVisible(false);
            armorStand.setSmall(true);
            armorStand.setCustomName(getType().getEntityName(getPlayer()));
            armorStand.setCustomNameVisible(true);

            if (Core.getCustomPlayer(getPlayer()).getPetName(getType().getConfigName()) != null)
                armorStand.setCustomName(Core.getCustomPlayer(getPlayer()).getPetName(getType().getConfigName()));

            customEnt.getBukkitEntity().setPassenger(armorStand);
            EntitySpawningManager.setBypass(true);
            ((CraftWorld) getPlayer().getWorld()).getHandle().addEntity(customEnt);
            EntitySpawningManager.setBypass(false);
        }
        getPlayer().sendMessage(MessageManager.getMessage("Pets.Spawn").replace("%petname%", (Core.placeHolderColor)
                ? getType().getMenuName() : Core.filterColor(getType().getMenuName())));
    }

    /**
     * Get the pet type.
     *
     * @return The pet type.
     */
    public PetType getType() {
        return type;
    }

    /**
     * Called each tick.
     */
    abstract void onUpdate();

    /**
     * Called when a player gets his pet cleared.
     */
    public void clear() {
        if (armorStand != null)
            armorStand.remove();
        if (getType().getEntityType() != EntityType.ZOMBIE)
            entity.remove();
        else {
            customEnt.dead = true;
            customEntities.remove(customEnt);
        }
        if (getPlayer() != null && Core.getCustomPlayer(getPlayer()) != null)
            Core.getCustomPlayer(getPlayer()).currentPet = null;
        for (Item i : items)
            i.remove();
        items.clear();
        pathUpdater.shutdown();
        try {
            HandlerList.unregisterAll(this);
            HandlerList.unregisterAll(listener);
        } catch (Exception exc) {
        }
        if (getPlayer() != null) {
            getPlayer().sendMessage(MessageManager.getMessage("Pets.Despawn").replace("%petname%", (Core.placeHolderColor)
                    ? getType().getMenuName() : Core.filterColor(getType().getMenuName())));
            Core.getCustomPlayer(getPlayer()).currentPet = null;
        }
        owner = null;
    }

    /**
     * Get the pet owner.
     *
     * @return the UUID of the owner.
     */
    protected final UUID getOwner() {
        return owner;
    }

    /**
     * Get the player owner.
     *
     * @return The player from getOwner.
     */
    protected final Player getPlayer() {
        return Bukkit.getPlayer(owner);
    }
    
    /**
     * Force pet to follow player
     */
    private class FollowPlayer implements Runnable {
    	@Override
        public void run() {
            if (getPlayer() == null)
                return;
            if (Core.getCustomPlayer(getPlayer()).currentTreasureChest != null)
                return;

            net.minecraft.server.v1_8_R3.Entity petEntity = getType().getEntityType() == EntityType.ZOMBIE ? customEnt : ((CraftEntity) entity).getHandle();
            ((EntityInsentient) petEntity).getNavigation().a(2);
            Location targetLocation = getPlayer().getLocation();
            PathEntity path;
            path = ((EntityInsentient) petEntity).getNavigation().a(targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ() + 1);
            try {
                int distance = (int) Bukkit.getPlayer(getPlayer().getName()).getLocation().distance(petEntity.getBukkitEntity().getLocation());
                if (distance > 10 && petEntity.valid && getPlayer().isOnGround()) {
                    petEntity.setLocation(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ(), 0, 0);
                }
                if (path != null && distance > 3.3) {
                    double speed = 1.05d;
                    if (getType().getEntityType() == EntityType.ZOMBIE)
                        speed *= 1.5;
                    ((EntityInsentient) petEntity).getNavigation().a(path, speed);
                    ((EntityInsentient) petEntity).getNavigation().a(speed);
                }
            } catch (IllegalArgumentException exception) {
                petEntity.setLocation(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ(), 0, 0);
            }
        }
    }
    

    /**
     * Event Listener.
     * listens for pets damage.
     */
    public class PetListener implements Listener {
        private Pet pet;

        public PetListener(Pet pet) {
            this.pet = pet;
            Core.registerListener(this);
        }

        @EventHandler
        public void onEntityDamage(EntityDamageEvent event) {
            if (pet.getType().getEntityType() == EntityType.ZOMBIE) {
                if (event.getEntity() == pet.customEnt.getBukkitEntity())
                    event.setCancelled(true);
            } else {
                if (event.getEntity() == pet.entity)
                    event.setCancelled(true);
            }
        }

        @EventHandler
        public void onPlayerTeleport(PlayerTeleportEvent event) {
            if (event.getPlayer() == getPlayer()) {
                if (getType().getEntityType() == EntityType.ZOMBIE)
                    customEnt.getBukkitEntity().teleport(getPlayer());
                else
                    entity.teleport(getPlayer());
            }
        }


    }

}
