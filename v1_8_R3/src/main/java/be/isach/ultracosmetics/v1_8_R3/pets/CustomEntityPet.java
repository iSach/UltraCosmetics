package be.isach.ultracosmetics.v1_8_R3.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.pets.IPetCustomEntity;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import be.isach.ultracosmetics.v1_8_R3.customentities.CustomEntities;
import be.isach.ultracosmetics.v1_8_R3.customentities.Pumpling;
import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.HandlerList;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Sacha on 7/03/16.
 */
public abstract class CustomEntityPet extends Pet {

    /**
     * Custom Entity.
     */
    public IPetCustomEntity customEntity;

    public CustomEntityPet(UltraPlayer owner, UltraCosmetics ultraCosmetics, PetType petType) {
        super(owner, ultraCosmetics, petType);

    }

    @Override
    public void onEquip() {
        followTask = new PlayerFollower(this, getPlayer());
        if (getOwner().getCurrentPet() != null)
            getOwner().removePet();

        getOwner().setCurrentPet(this);

        double x = getPlayer().getLocation().getX();
        double y = getPlayer().getLocation().getY();
        double z = getPlayer().getLocation().getZ();

        if (this instanceof PetPumpling) {
            customEntity = new Pumpling(((CraftPlayer) getPlayer()).getHandle().getWorld(), getOwner().getBukkitPlayer());
        }
        CustomEntities.customEntities.add(((CraftEntity) customEntity.getEntity()).getHandle());
        getCustomEntity().setLocation(x, y, z, 0, 0);
        Location spawnLoc = customEntity.getEntity().getLocation();
        armorStand = (ArmorStand) customEntity.getEntity().getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setCustomName(getType().getEntityName(getPlayer()));
        armorStand.setCustomNameVisible(true);
        FixedMetadataValue metadataValue = new FixedMetadataValue(getUltraCosmetics(), "C_AD_ArmorStand");
        armorStand.setMetadata("C_AD_ArmorStand", metadataValue);

        if (getOwner().getPetName(getType()) != null) {
            armorStand.setCustomName(getOwner().getPetName(getType()));
        }

        customEntity.getEntity().setPassenger(armorStand);
        EntitySpawningManager.setBypass(true);
        ((CraftWorld) getPlayer().getWorld()).getHandle().addEntity(getCustomEntity());
        EntitySpawningManager.setBypass(false);
    }

    @Override
    protected void removeEntity() {
        getCustomEntity().dead = true;
        CustomEntities.customEntities.remove(customEntity);
    }

    @Override
    public boolean isCustomEntity() {
        return true;
    }

    @Override
    public org.bukkit.entity.Entity getEntity() {
        return customEntity.getEntity();
    }

    public Entity getCustomEntity() {
        return ((CraftEntity) customEntity.getEntity()).getHandle();
    }
}
