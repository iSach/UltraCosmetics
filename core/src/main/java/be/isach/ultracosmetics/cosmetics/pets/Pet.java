package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.EntityCosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.ServerVersion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XMaterial;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance of a pet summoned by a player.
 *
 * @author iSach
 * @since 03-08-2015
 */
public abstract class Pet extends EntityCosmetic<PetType> implements Updatable {
    /**
     * List of items popping out from Pet.
     */
    protected List<Item> items = new ArrayList<>();

    /**
     * ArmorStand for nametags. Only custom entity pets use this.
     */
    protected ArmorStand armorStand;

    /**
     * Task that forces pets to follow player
     */
    protected final APlayerFollower followTask;

    /**
     * The {@link org.bukkit.inventory.ItemStack ItemStack} this pet drops, null if none.
     * Sometimes modified before dropping to change what is dropped
     */
    protected ItemStack dropItem;

    public Pet(UltraPlayer owner, UltraCosmetics ultraCosmetics, PetType petType, ItemStack dropItem) {
        super(ultraCosmetics, Category.PETS, owner, petType);
        this.dropItem = dropItem;
        this.followTask = UltraCosmeticsData.get().getVersionManager().newPlayerFollower(this, getPlayer());
    }

    public Pet(UltraPlayer owner, UltraCosmetics ultraCosmetics, PetType petType, XMaterial dropItemType) {
        this(owner, ultraCosmetics, petType, dropItemType.parseItem());
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onEquip() {

        // Bypass WorldGuard protection.
        EntitySpawningManager.setBypass(true);
        entity = spawnEntity();
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

        // setCustomNameVisible(true) doesn't seem to work on 1.8, so we'll just use armor stands in that case
        if (isCustomEntity() || UltraCosmeticsData.get().getServerVersion() == ServerVersion.v1_8_R3) {
            armorStand = (ArmorStand) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.ARMOR_STAND);
            armorStand.setVisible(false);
            armorStand.setSmall(true);
            armorStand.setMarker(true);
            armorStand.setCustomNameVisible(true);
            FixedMetadataValue metadataValue = new FixedMetadataValue(getUltraCosmetics(), "C_AD_ArmorStand");
            armorStand.setMetadata("C_AD_ArmorStand", metadataValue);
            entity.setPassenger(armorStand);
        } else {
            getEntity().setCustomNameVisible(true);
        }

        updateName();

        ((LivingEntity) entity).setRemoveWhenFarAway(false);
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearPathfinders(entity);
        if (SettingsManager.getConfig().getBoolean("Pets-Are-Silent", false)) {
            UltraCosmeticsData.get().getVersionManager().getAncientUtil().setSilent(entity, true);
        }

        entity.setMetadata("Pet", new FixedMetadataValue(getUltraCosmetics(), "UltraCosmetics"));
        setupEntity();
    }

    @Override
    protected void scheduleTask() {
        runTaskTimer(getUltraCosmetics(), 0, 3);
    }

    @Override
    public boolean tryEquip() {
        if (getType().isMonster() && getPlayer().getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            getOwner().sendMessage(MessageManager.getMessage("Mounts.Cant-Spawn"));
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        if (entity != null && !entity.isValid()) {
            clear();
            return;
        }

        if (getOwner().isOnline() && getOwner().getCurrentPet() == this) {
            onUpdate();

            followTask.run();
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
    }

    public APlayerFollower getFollowTask() {
        return followTask;
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
        // Not using the ItemFactory variance method for this one
        // because we want to bump the Y velocity a bit between calcs.
        Vector velocity = new Vector(RANDOM.nextDouble() - 0.5, RANDOM.nextDouble() / 2.0 + 0.3, RANDOM.nextDouble() - 0.5).multiply(0.4);
        final Item drop = ItemFactory.spawnUnpickableItem(dropItem, ((LivingEntity)entity).getEyeLocation(), velocity);
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

    public boolean isCustomEntity() {
        return false;
    }
}
