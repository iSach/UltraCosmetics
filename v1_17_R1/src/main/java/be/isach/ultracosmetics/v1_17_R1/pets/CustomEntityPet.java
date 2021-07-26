package be.isach.ultracosmetics.v1_17_R1.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.pets.IPetCustomEntity;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import be.isach.ultracosmetics.v1_17_R1.customentities.CustomEntities;
import be.isach.ultracosmetics.v1_17_R1.customentities.Pumpling;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * @author RadBuilder
 */
public abstract class CustomEntityPet extends Pet {

    /**
     * Custom Entity.
     */
    public IPetCustomEntity customEntity;

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
            /**customEntity = CustomEntities.typesLoc.b(((CraftPlayer) getPlayer()).getHandle().getWorld(),
             null,
             null,
             null,
             new BlockPosition(x, y, z),
             null, false, false);*/
            EntitySpawningManager.setBypass(true);
            customEntity = new Pumpling(EntityType.ZOMBIE, ((CraftPlayer) getPlayer()).getHandle().getLevel(), this);
            EntitySpawningManager.setBypass(false);
        }
        Bukkit.getScheduler().runTask(getUltraCosmetics(), () -> {
            CustomEntities.customEntities.add(((CraftEntity) customEntity.getEntity()).getHandle());
            getCustomEntity().moveTo(x, y, z, 0, 0);
            Location spawnLoc = customEntity.getEntity().getLocation();
            armorStand = (ArmorStand) customEntity.getEntity().getWorld().spawnEntity(spawnLoc, org.bukkit.entity.EntityType.ARMOR_STAND);
            armorStand.setVisible(false);
            armorStand.setSmall(true);
            armorStand.setCustomName(getType().getEntityName(getPlayer()));
            armorStand.setCustomNameVisible(true);
            FixedMetadataValue metadataValue = new FixedMetadataValue(getUltraCosmetics(), "C_AD_ArmorStand");
            armorStand.setMetadata("C_AD_ArmorStand", metadataValue);

            if (getOwner().getPetName(getType()) != null) {
                armorStand.setCustomName(getOwner().getPetName(getType()));
            }

            customEntity.getEntity().addPassenger(armorStand);
            EntitySpawningManager.setBypass(true);
            ((CraftWorld) getPlayer().getWorld()).getHandle().addFreshEntity(getCustomEntity());
            EntitySpawningManager.setBypass(false);
        });

        if (getPlayer().getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            getOwner().sendMessage("§c§lUltraCosmetics > Monsters can't spawn here!");
            getOwner().removePet();
        }
    }

    @Override
    protected void removeEntity() {
        getCustomEntity().discard();
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
