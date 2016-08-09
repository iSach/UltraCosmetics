package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.PetType;
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
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sacha on 03/08/15.
 */
public abstract class Pet extends Cosmetic<PetType> {

    /**
     * List of items popping out from Pet.
     */
    public ArrayList<Item> items = new ArrayList<>();

    /**
     * Armor stand which is the name of the pet.
     */
    public ArmorStand armorStand;

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

    public Pet(UltraPlayer owner, UltraCosmetics ultraCosmetics, PetType petType) {
        super(ultraCosmetics, Category.PETS, owner, petType);

        this.pathUpdater = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onEquip() {
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
        armorStand.setRemoveWhenFarAway(true);
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
        ((LivingEntity)entity).setRemoveWhenFarAway(false);
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

    @Override
    protected void onClear() throws Exception {

        // Remove Armor Stand.
        if (armorStand != null) {
            armorStand.remove();
        }

        // Remove Pet Entity.
        removeEntity();

        // Empty current Pet.
        if (getPlayer() != null && UltraCosmetics.getCustomPlayer(getPlayer()) != null) {
            UltraCosmetics.getCustomPlayer(getPlayer()).currentPet = null;
        }

        // Remove items.
        items.stream().filter(Entity::isValid).forEach(Entity::remove);

        // Clear items.
        items.clear();

        // Shutdown path updater.
        pathUpdater.shutdown();

        if (getPlayer() != null) {
            UltraCosmetics.getCustomPlayer(getPlayer()).currentPet = null;
        }
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

}
