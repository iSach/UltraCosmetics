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
import org.bukkit.ChatColor;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Represents an instance of a pet summoned by a player.
 *
 * @author iSach
 * @since 03-08-2015
 */
public abstract class Pet extends Cosmetic<PetType> implements Updatable {
    /**
     * List of items popping out from Pet.
     */
    protected List<Item> items = new ArrayList<>();

    /**
     * ArmorStand for nametags. Only custom entity pets use this.
     */
    protected ArmorStand armorStand;

    /**
     * Runs the task for pets following players
     */
    protected ExecutorService pathUpdater;
    //protected int followTaskId;

    /**
     * Task that forces pets to follow player
     */
    protected IPlayerFollower followTask;

    /**
     * If Pet is a normal entity, it will be stored here.
     */
    protected Entity entity;

    /**
     * The {@link org.bukkit.inventory.ItemStack ItemStack} this pet drops, null if none.
     */
    protected ItemStack dropItem;

    protected Random random = new Random();

    public Pet(UltraPlayer owner, UltraCosmetics ultraCosmetics, PetType petType, ItemStack dropItem) {
        super(ultraCosmetics, Category.PETS, owner, petType);

        this.dropItem = dropItem;
        this.pathUpdater = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onEquip() {
        if (getOwner().getCurrentPet() != null) {
            getOwner().removePet();
        }

        this.followTask = UltraCosmeticsData.get().getVersionManager().newPlayerFollower(this, getPlayer());

        getOwner().setCurrentPet(this);

        runTaskTimer(getUltraCosmetics(), 0, 3);

        // Bypass WorldGuard protection.
        EntitySpawningManager.setBypass(true);
        entity = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), getType().getEntityType());
        EntitySpawningManager.setBypass(false);

        UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearPathfinders(entity);

        if (entity instanceof Ageable) {
            Ageable ageable = (Ageable) entity;
            if (SettingsManager.getConfig().getBoolean("Pets-Are-Babies")) {
                ageable.setBaby();
            } else {
                ageable.setAdult();
            }
            ageable.setAgeLock(true);
        }

        if (entity instanceof Tameable) {
            ((Tameable)entity).setTamed(true);
        }

        getEntity().setCustomNameVisible(true);

        updateName();

        ((LivingEntity) entity).setRemoveWhenFarAway(false);
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearPathfinders(entity);
        if (SettingsManager.getConfig().getBoolean("Pets-Are-Silent", false)) {
            UltraCosmeticsData.get().getVersionManager().getAncientUtil().setSilent(entity, true);
        }

        this.entity.setMetadata("Pet", new FixedMetadataValue(getUltraCosmetics(), "UltraCosmetics"));
    }

    @Override
    public void run() {
        if (entity != null && !entity.isValid()) {
            clear();
            return;
        }

        if (Bukkit.getPlayer(getOwnerUniqueId()) != null
                && getOwner().getCurrentPet() != null
                && getOwner().getCurrentPet().getType() == getType()) {
            onUpdate();

            pathUpdater.submit(followTask.getTask());
        } else {
            clear();
        }
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
        if (entity != null) {
            entity.remove();
        }
    }

    public IPlayerFollower getFollowTask() {
        return followTask;
    }

    public Entity getEntity() {
        return entity;
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public boolean hasArmorStand() {
        return armorStand != null;
    }

    public List<Item> getItems() {
        return items;
    }

    public void updateName() {
        Entity rename;
        if (armorStand == null) {
            rename = entity;
        } else {
            rename = armorStand;
        }
        if (getOwner().getPetName(getType()) != null) {
            rename.setCustomName(getOwner().getPetName(getType()));
        } else {
            rename.setCustomName(getType().getEntityName(getPlayer()));
        }
    }

    /**
     * This method is overridden by custom entity mobs that don't use the mobs own name tag for the hologram.
     * @return the entity that should be renamed
     */
    protected Entity getNamedEntity() {
        return entity;
    }

    @Override
    public void onUpdate() {
        if (SettingsManager.getConfig().getBoolean("Pets-Drop-Items")) {
            dropItem();
        }
    }

    public void dropItem() {
        final Item drop = entity.getWorld().dropItem(((LivingEntity) entity).getEyeLocation(), dropItem);
        drop.setPickupDelay(30000);
        drop.setVelocity(new Vector(random.nextDouble() - 0.5, random.nextDouble() / 2.0 + 0.3, random.nextDouble() - 0.5).multiply(0.4));
        items.add(drop);
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            drop.remove();
            items.remove(drop);
        }, 5);
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

    @Override
    protected String filterPlaceholders(String message) {
        String filtered = super.filterPlaceholders(message);
        String name = getOwner().getPetName(getType());
        if (name != null) {
            filtered += " " + ChatColor.GRAY + "(" + name + ChatColor.GRAY + ")";
        }
        return filtered; 
    }
}
