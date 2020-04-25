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
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public ArrayList<Item> items = new ArrayList<>();

    /**
     * ArmorStand for nametags. Most pets don't use this.
     */
    public ArmorStand armorStand;

    /**
     * Runs the task for pets following players |: Problems with async??
     */
    //protected ExecutorService pathUpdater;
    protected int followTaskId;

    /**
     * Task that forces pets to follow player
     */
    protected IPlayerFollower followTask;

    /**
     * If Pet is a normal entity, it will be stored here.
     */
    public Entity entity;

    /**
     * The {@link org.bukkit.inventory.ItemStack ItemStack} this pet drops, null if none.
     */
    private ItemStack dropItem;

    private Random r = new Random();

    /**
     * If Pet has color variants, it will be stored here as a String.
     */
    private String colorVariantStr;

    /**
     * Pet name.
     */
    private String petName;

    public Pet(UltraPlayer owner, UltraCosmetics ultraCosmetics, PetType petType, ItemStack dropItem) {
        super(ultraCosmetics, Category.PETS, owner, petType);

        this.dropItem = dropItem;
        //this.pathUpdater = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onEquip() {

        // Remove existing pet without altering the cosmetics profile
        if (getOwner().getCurrentPet() != null) {
            getOwner().removePetWithoutSaving();
        }

        // Create the PlayerFollower that controls this pet's movements
        followTask = UltraCosmeticsData.get().getVersionManager().newPlayerFollower(this, getPlayer());
        followTaskId = Bukkit.getScheduler().runTaskTimer(getUltraCosmetics(), followTask.getTask(), 0, 4).getTaskId();

        // Bypass WorldGuard spawning restrictions
        EntitySpawningManager.setBypass(true);
        try {
            this.entity = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), getType().getEntityType());
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        EntitySpawningManager.setBypass(false);

        if (entity instanceof Ageable) {
            if (SettingsManager.getConfig().getBoolean("Pets-Are-Babies")) {
                ((Ageable) entity).setBaby();
            } else {
                ((Ageable) entity).setAdult();
            }
            ((Ageable) entity).setAgeLock(true);
        }

        getEntity().setCustomNameVisible(true);
        getEntity().setCustomName(getType().getEntityName(getPlayer()));

        if (getOwner().getPetName(getType()) != null) {
            getEntity().setCustomName(getOwner().getPetName(getType()));
        }

        ((LivingEntity) entity).setRemoveWhenFarAway(false);
        UltraCosmeticsData.get().getVersionManager().getPathfinderUtil().removePathFinders(entity);

        // Check for pre-existing color variant metadata
        try {
            String colorVariant = getColorVariantFromFile();
            String thisPetType = getType().getConfigName();
            String filePetType = getPetTypeFromFile();
            if( !colorVariant.equals("none") && thisPetType.equals(filePetType)) {
                if (entity instanceof Llama) ((Llama) entity).setColor(Llama.Color.valueOf(colorVariant));
                else if (entity instanceof Sheep) ((Sheep) entity).setColor(DyeColor.valueOf(colorVariant));
                else if (entity instanceof Parrot) ((Parrot) entity).setVariant(Parrot.Variant.valueOf(colorVariant));
                else if (entity instanceof Cat) ((Cat) entity).setCatType(Cat.Type.valueOf(colorVariant));
                else if (entity instanceof Rabbit) ((Rabbit) entity).setRabbitType(Rabbit.Type.valueOf(colorVariant));
                else if (entity instanceof MushroomCow) ((MushroomCow) entity).setVariant(MushroomCow.Variant.valueOf(colorVariant));
            }
            // Check for pre-existing pet name
            String petName = getPetNameFromFile();
            if(!petName.equals("none")) {
                getEntity().setCustomName(petName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set custom metadata
        entity.setMetadata("Pet", new FixedMetadataValue(getUltraCosmetics(), "UltraCosmetics"));
        setColorVariantString(extractColor(entity)); // Extract current color variant as string
        setPetName(entity.getCustomName()); // Extract current pet name

        // Set this pet as the UltraPlayer's current pet
        getOwner().setCurrentPet(this);
        getOwner().saveCosmeticsProfile();
    }

    public String getColorVariantFromFile() {
        SettingsManager sm = SettingsManager.getData(getOwnerUniqueId());
        ConfigurationSection s = sm.fileConfiguration.getConfigurationSection("enabled");
        String color = s.getString("pet.color");
        return color;
    }

    public String getPetTypeFromFile() {
        SettingsManager sm = SettingsManager.getData(getOwnerUniqueId());
        ConfigurationSection s = sm.fileConfiguration.getConfigurationSection("enabled");
        return s.getString("pet.type");
    }

    public String getPetNameFromFile() {
        SettingsManager sm = SettingsManager.getData(getOwnerUniqueId());
        ConfigurationSection s = sm.fileConfiguration.getConfigurationSection("enabled");
        return s.getString("pet.name");
    }

    public String getColorVariantString() {
        return this.colorVariantStr;
    }

    public void setColorVariantString(String color) {
        this.colorVariantStr = color;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String name) {
        this.petName = name;
    }

    private String extractColor(Entity entity) {
        if(entity instanceof Llama) return ((Llama) entity).getColor().toString();
        else if (entity instanceof Sheep) return ((Sheep) entity).getColor().toString();
        else if (entity instanceof Parrot) return ((Parrot) entity).getVariant().toString();
        else if (entity instanceof Cat) return ((Cat) entity).getCatType().toString();
        else if (entity instanceof Rabbit) return ((Rabbit) entity).getRabbitType().toString();
        else if (entity instanceof MushroomCow) return ((MushroomCow) entity).getVariant().toString();
        return null;
    }

    @Override
    public void run() {
        try {
            if (!entity.isValid()) {
                if (armorStand != null) {
                    armorStand.remove();
                }

                entity.remove();

                if (getPlayer() != null) {
                    getOwner().setCurrentPet(null);
                }

                items.forEach(Entity::remove);
                items.clear();

                try {
                    HandlerList.unregisterAll(this);
                } catch (Exception ignored) {
                    // Ignored.
                }

                cancel();
                return;
            }

            if (Bukkit.getEntity(getOwnerUniqueId()) != null
                    && getOwner().getCurrentPet() != null
                    && getOwner().getCurrentPet().getType() == getType()) {
                if (SettingsManager.getConfig().getBoolean("Pets-Drop-Items")) {
                    onUpdate();
                }

            } else {
                cancel();

                if (armorStand != null) {
                    armorStand.remove();
                }

                items.forEach(Entity::remove);
                items.clear();
                clear();
            }
        } catch (NullPointerException exc) {
            exc.printStackTrace();
            Bukkit.getScheduler().cancelTask(followTaskId);
            cancel();

            if (armorStand != null) {
                armorStand.remove();
            }

            items.forEach(Entity::remove);
            items.clear();
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
        //pathUpdater.shutdown();
        Bukkit.getScheduler().cancelTask(followTaskId);

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

    @Override
    public void onUpdate() {
        final Item drop = entity.getWorld().dropItem(((LivingEntity) entity).getEyeLocation(), dropItem);
        drop.setPickupDelay(30000);
        drop.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2.0 + 0.3, r.nextDouble() - 0.5).multiply(0.4));
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
}
