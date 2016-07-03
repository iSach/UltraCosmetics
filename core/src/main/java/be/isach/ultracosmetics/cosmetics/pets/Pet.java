package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sacha on 03/08/15.
 */
public abstract class Pet implements Listener {

    public static final List<ArmorStand> PET_NAMES = new ArrayList<>();

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
    protected UUID owner;

    /**
     * Event listener.
     * Listens for pet damage.
     */
    public Listener listener;

    /**
     * Runs the task for pets following players
     */
    protected ExecutorService pathUpdater;

    /**
     * Task that forces pets to follow player
     */
    protected IPlayerFollower followTask;

    /**
     * If Pet is a normal entity, it will be stored here.
     */
    public Entity entity;

    public Pet(final UUID owner, final PetType type) {
        this.type = type;

        if (owner == null) return;

        this.owner = owner;

        if (!getPlayer().hasPermission(getType().getPermission())) {
            getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
            return;
        }
        this.pathUpdater = Executors.newSingleThreadExecutor();
    }

    /**
     * Equips the pet.
     */
    public void equip() {
        this.followTask = UltraCosmetics.getInstance().newPlayerFollower(this, getPlayer());
        if (UltraCosmetics.getCustomPlayer(getPlayer()).currentPet != null)
            UltraCosmetics.getCustomPlayer(getPlayer()).removePet();
        UltraCosmetics.getCustomPlayer(getPlayer()).currentPet = this;

        final Pet pet = this;

        armorStand = (ArmorStand) this.getPlayer().getWorld().spawnEntity(this.getPlayer().getLocation(), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        armorStand.setCustomName(getType().getEntityName(getPlayer()));
        armorStand.setCustomNameVisible(true);
        armorStand.setMetadata("C_AD_ArmorStand", new FixedMetadataValue(UltraCosmetics.getInstance(), "C_AD_ArmorStand"));
        PET_NAMES.add(armorStand);
        if (UltraCosmetics.getCustomPlayer(getPlayer()).getPetName(getType().getConfigName()) != null)
            armorStand.setCustomName(UltraCosmetics.getCustomPlayer(getPlayer()).getPetName(getType().getConfigName()));

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!entity.isValid()) {
                        if (armorStand != null)
                            armorStand.remove();
                        entity.remove();
                        if (getPlayer() != null)
                            UltraCosmetics.getCustomPlayer(getPlayer()).currentPet = null;
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
                    if (Bukkit.getPlayer(owner) != null
                            && UltraCosmetics.getCustomPlayer(Bukkit.getPlayer(owner)).currentPet != null
                            && UltraCosmetics.getCustomPlayer(Bukkit.getPlayer(owner)).currentPet.getType() == type) {
                        if (SettingsManager.getConfig().getBoolean("Pets-Drop-Items"))
                            onUpdate();
                        pathUpdater.submit(followTask.getTask());
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
                    if (armorStand != null && getType() != PetType.WITHER) {
                        armorStand.teleport(getEntity().getLocation().add(0, -0.7, 0));
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
        runnable.runTaskTimer(UltraCosmetics.getInstance(), 0, 3);
        listener = new PetListener(this);

        EntitySpawningManager.setBypass(true);
        this.entity = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), getType().getEntityType());
        EntitySpawningManager.setBypass(false);
        if (entity instanceof Ageable) {
            if (SettingsManager.getConfig().getBoolean("Pets-Are-Babies")) ((Ageable) entity).setBaby();
            else ((Ageable) entity).setAdult();
            ((Ageable) entity).setAgeLock(true);
        }
        UltraCosmetics.getInstance().getPathfinderUtil().removePathFinders(entity);


//        this.entity.setPassenger(armorStand);
        if (getType() == PetType.WITHER) {
            this.entity.setCustomName(getType().getEntityName(getPlayer()));
            this.entity.setCustomNameVisible(true);

            if (UltraCosmetics.getCustomPlayer(getPlayer()).getPetName(getType().getConfigName()) != null)
                this.entity.setCustomName(UltraCosmetics.getCustomPlayer(getPlayer()).getPetName(getType().getConfigName()));
            armorStand.remove();
        }
        this.entity.setMetadata("Pet", new FixedMetadataValue(UltraCosmetics.getInstance(), "UltraCosmetics"));

        getPlayer().sendMessage(MessageManager.getMessage("Pets.Spawn").replace("%petname%", (UltraCosmetics.getInstance().placeholdersHaveColor())
                ? getType().getMenuName() : UltraCosmetics.filterColor(getType().getMenuName())));
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
    protected abstract void onUpdate();

    /**
     * Called when a player gets his pet cleared.
     */
    public void clear() {
        if (armorStand != null)
            armorStand.remove();
        removeEntity();
        if (getPlayer() != null && UltraCosmetics.getCustomPlayer(getPlayer()) != null)
            UltraCosmetics.getCustomPlayer(getPlayer()).currentPet = null;
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
            getPlayer().sendMessage(MessageManager.getMessage("Pets.Despawn").replace("%petname%", (UltraCosmetics.getInstance().placeholdersHaveColor())
                    ? getType().getMenuName() : UltraCosmetics.filterColor(getType().getMenuName())));
            UltraCosmetics.getCustomPlayer(getPlayer()).currentPet = null;
        }
        owner = null;
    }

    public boolean isCustomEntity() {
        return false;
    }

    protected void removeEntity() {
        entity.remove();
    }

    public Entity getEntity() {
        return entity;
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
     * Event Listener.
     * listens for pets damage.
     */
    public class PetListener implements Listener {
        private Pet pet;

        public PetListener(Pet pet) {
            this.pet = pet;
            UltraCosmetics.getInstance().registerListener(this);
        }

        @EventHandler
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.getEntity() == pet.getEntity())
                event.setCancelled(true);
        }

        @EventHandler
        public void onPlayerTeleport(PlayerTeleportEvent event) {
            if (event.getPlayer() == getPlayer())
                pet.getEntity().teleport(getPlayer());
        }
    }

    public static void purgeNames() {
        synchronized (PET_NAMES) {
            for(ArmorStand armorStand : PET_NAMES) {
                if(armorStand.isValid()) {
                    armorStand.remove();
                }
            }
            PET_NAMES.clear();
        }
    }

}
