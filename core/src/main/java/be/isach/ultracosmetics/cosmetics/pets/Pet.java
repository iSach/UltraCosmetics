package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Package: ${PACKAGE_NAME}
 * Created by: sacha
 * Date: 03/08/15
 * Project: UltraCosmetics
 */
public abstract class Pet extends Cosmetic<PetType> implements Updatable {

    /**
     * List of items popping out from Pet.
     */
    public ArrayList<Item> items = new ArrayList<>();

    /**
     * Armor stand which is the name of the pet.
     */
    public ArmorStand armorStand;

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
        this.followTask = UltraCosmeticsData.get().getVersionManager().newPlayerFollower(this, getPlayer());
        if (getOwner().getCurrentPet() != null)
            getOwner().removePet();
        getOwner().setCurrentPet(this);

        final Pet pet = this;

        armorStand = (ArmorStand) this.getPlayer().getWorld().spawnEntity(this.getPlayer().getLocation(), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        armorStand.setCustomName(getType().getEntityName(getPlayer()));
        armorStand.setCustomNameVisible(true);
        armorStand.setMetadata("C_AD_ArmorStand", new FixedMetadataValue(getUltraCosmetics(), "C_AD_ArmorStand"));
        armorStand.setRemoveWhenFarAway(true);
        if (getOwner().getPetName(getType()) != null) {
            armorStand.setCustomName(getOwner().getPetName(getType()));
        }

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!entity.isValid()) {
                        if (armorStand != null)
                            armorStand.remove();
                        entity.remove();
                        if (getPlayer() != null)
                            getOwner().setCurrentPet(null);
                        items.forEach(Entity::remove);
                        items.clear();
                        try {
                            HandlerList.unregisterAll(pet);
                        } catch (Exception ignored) {
                        }
                        cancel();
                        return;
                    }
                    if (Bukkit.getPlayer(getOwnerUniqueId()) != null
                            && getOwner().getCurrentPet() != null
                            && getOwner().getCurrentPet().getType() == getType()) {
                        if (SettingsManager.getConfig().getBoolean("Pets-Drop-Items"))
                            onUpdate();
                        pathUpdater.submit(followTask.getTask());
                    } else {
                        cancel();
                        if (armorStand != null)
                            armorStand.remove();
                        items.forEach(Entity::remove);
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
                    items.forEach(Entity::remove);
                    items.clear();
                    clear();
                }
            }
        };
        runnable.runTaskTimer(getUltraCosmetics(), 0, 3);

        EntitySpawningManager.setBypass(true);
        this.entity = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), getType().getEntityType());
        EntitySpawningManager.setBypass(false);
        if (entity instanceof Ageable) {
            if (SettingsManager.getConfig().getBoolean("Pets-Are-Babies")) ((Ageable) entity).setBaby();
            else ((Ageable) entity).setAdult();
            ((Ageable) entity).setAgeLock(true);
        }
        ((LivingEntity) entity).setRemoveWhenFarAway(false);
        UltraCosmeticsData.get().getVersionManager().getPathfinderUtil().removePathFinders(entity);


//        this.entity.setPassenger(armorStand);
        if (getType() == PetType.WITHER) {
            this.entity.setCustomName(getType().getEntityName(getPlayer()));
            this.entity.setCustomNameVisible(true);

            if (getOwner().getPetName(getType()) != null) {
                this.entity.setCustomName(getOwner().getPetName(getType()));
            }
            armorStand.remove();
        }
        this.entity.setMetadata("Pet", new FixedMetadataValue(getUltraCosmetics(), "UltraCosmetics"));
    }

    @Override
    protected void onClear() {

        // Remove Armor Stand.
        if (armorStand != null) {
            armorStand.remove();
        }

        // Remove Pet Entity.
        removeEntity();

        // Remove items.
        items.stream().filter(Entity::isValid).forEach(Entity::remove);

        // Clear items.
        items.clear();

        // Shutdown path updater.
        pathUpdater.shutdown();

        // Empty current Pet.
        if (getPlayer() != null && getOwner() != null) {
            getOwner().setCurrentPet(null);
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

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() == getEntity())
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getPlayer() == getPlayer())
            getEntity().teleport(getPlayer());
    }

}
