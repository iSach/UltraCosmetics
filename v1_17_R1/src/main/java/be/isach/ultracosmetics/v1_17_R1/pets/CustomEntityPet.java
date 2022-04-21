package be.isach.ultracosmetics.v1_17_R1.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import be.isach.ultracosmetics.v1_17_R1.customentities.CustomEntities;
import be.isach.ultracosmetics.v1_17_R1.customentities.Pumpling;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import net.minecraft.world.entity.EntityType;

/**
 * @author RadBuilder
 */
public abstract class CustomEntityPet extends Pet {

    /**
     * Custom Entity.
     */
    protected Entity customEntity;

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
            customEntity = new Pumpling(EntityType.ZOMBIE, ((CraftPlayer) getPlayer()).getHandle().getLevel(), this).getBukkitEntity();
            EntitySpawningManager.setBypass(false);
        }
        CustomEntities.addCustomEntity(getNMSEntity());
        getNMSEntity().moveTo(x, y, z, 0, 0);
        Location spawnLoc = customEntity.getLocation();
        armorStand = (ArmorStand) customEntity.getWorld().spawnEntity(spawnLoc, org.bukkit.entity.EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setCustomNameVisible(true);
        FixedMetadataValue metadataValue = new FixedMetadataValue(getUltraCosmetics(), "C_AD_ArmorStand");
        armorStand.setMetadata("C_AD_ArmorStand", metadataValue);
        updateName();

        customEntity.addPassenger(armorStand);
        EntitySpawningManager.setBypass(true);
        ((CraftWorld) getPlayer().getWorld()).getHandle().addFreshEntity(getNMSEntity());
        EntitySpawningManager.setBypass(false);

        if (getPlayer().getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            getOwner().sendMessage("§c§lUltraCosmetics > Monsters can't spawn here!");
            getOwner().removePet();
        }
    }

    @Override
    protected void removeEntity() {
        getNMSEntity().discard();
        CustomEntities.removeCustomEntity(getNMSEntity());
    }

    @Override
    public boolean isCustomEntity() {
        return true;
    }

    @Override
    public Entity getEntity() {
        return customEntity;
    }

    public net.minecraft.world.entity.Entity getNMSEntity() {
        return ((CraftEntity) customEntity).getHandle();
    }
}
