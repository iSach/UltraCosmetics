package be.isach.ultracosmetics.v1_16_R3.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import be.isach.ultracosmetics.v1_16_R3.customentities.CustomEntities;
import be.isach.ultracosmetics.v1_16_R3.customentities.Pumpling;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import net.minecraft.server.v1_16_R3.EntityTypes;

/**
 * @author RadBuilder
 */
public abstract class CustomEntityPet extends Pet {

    /**
     * Custom Entity.
     */
    public Entity customEntity;

    public CustomEntityPet(UltraPlayer owner, UltraCosmetics ultraCosmetics, PetType petType, ItemStack dropItem) {
        super(owner, ultraCosmetics, petType, dropItem);

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
            EntitySpawningManager.setBypass(true);
            customEntity = new Pumpling(EntityTypes.ZOMBIE, ((CraftPlayer) getPlayer()).getHandle().getWorld(), this).getBukkitEntity();
            EntitySpawningManager.setBypass(false);
        }
        CustomEntities.addCustomEntity(getNMSEntity());
        getNMSEntity().setLocation(x, y, z, 0, 0);
        Location spawnLoc = customEntity.getLocation();
        armorStand = (ArmorStand) customEntity.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setCustomNameVisible(true);
        FixedMetadataValue metadataValue = new FixedMetadataValue(getUltraCosmetics(), "C_AD_ArmorStand");
        armorStand.setMetadata("C_AD_ArmorStand", metadataValue);
        updateName();

        customEntity.addPassenger(armorStand);
        EntitySpawningManager.setBypass(true);
        ((CraftWorld) getPlayer().getWorld()).getHandle().addEntity(getNMSEntity());
        EntitySpawningManager.setBypass(false);

        if (getPlayer().getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            getOwner().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "UltraCosmetics > Monsters can't spawn here!");
            getOwner().removePet();
        }
    }

    @Override
    protected void removeEntity() {
        getNMSEntity().dead = true;
        CustomEntities.removeCustomEntity(getNMSEntity());
    }

    @Override
    public boolean isCustomEntity() {
        return true;
    }

    @Override
    public org.bukkit.entity.Entity getEntity() {
        return customEntity;
    }

    public net.minecraft.server.v1_16_R3.Entity getNMSEntity() {
        return ((CraftEntity) customEntity).getHandle();
    }
}
