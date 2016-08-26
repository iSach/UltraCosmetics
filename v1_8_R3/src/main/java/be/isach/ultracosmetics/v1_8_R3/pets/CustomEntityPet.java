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
public class CustomEntityPet extends Pet {

    /**
     * Custom Entity.
     */
    public IPetCustomEntity customEntity;

    public CustomEntityPet(UltraPlayer owner, PetType petType, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, petType);
    }

    @Override
    public void onEquip() {
        followTask = new PlayerFollower(this, getPlayer());
        if (getOwner().getCurrentPet() != null)
            getOwner().removePet();
        getOwner().setCurrentPet(this);

        final Pet instance = this;

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!getCustomEntity().valid) {
                        if (armorStand != null)
                            armorStand.remove();
                        getCustomEntity().dead = true;
                        if (getPlayer() != null)
                            getOwner().setCurrentPet(null);
                        for (Item i : items)
                            i.remove();
                        items.clear();
                        try {
                            HandlerList.unregisterAll(instance);
                        } catch (Exception exc) {
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
                        for (Item i : items)
                            i.remove();
                        items.clear();
                        clear();
                        return;
                    }

                    if (armorStand != null)
                        getCustomEntity().passenger = ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand) armorStand).getHandle();
                } catch (Exception exc) {

                }
            }
        };
        runnable.runTaskTimer(getUCInstance(), 0, 6);

        double x = getPlayer().getLocation().getX();
        double y = getPlayer().getLocation().getY();
        double z = getPlayer().getLocation().getZ();

        if (this instanceof PetPumpling) {
            customEntity = new Pumpling(((CraftPlayer) getPlayer()).getHandle().getWorld(), getPlayer());
        }
        CustomEntities.customEntities.add(((org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity) customEntity.getEntity()).getHandle());
        getCustomEntity().setLocation(x, y, z, 0, 0);
        armorStand = (ArmorStand) customEntity.getEntity().getWorld().spawnEntity(customEntity.getEntity().getLocation(), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setCustomName(getType().getEntityName(getPlayer()));
        armorStand.setCustomNameVisible(true);
        armorStand.setMetadata("C_AD_ArmorStand", new FixedMetadataValue(getUCInstance(), "C_AD_ArmorStand"));
        if (getOwner().getPetName(getType().getConfigName()) != null)
            armorStand.setCustomName(getOwner().getPetName(getType().getConfigName()));

        customEntity.getEntity().setPassenger(armorStand);
        EntitySpawningManager.setBypass(true);
        ((org.bukkit.craftbukkit.v1_8_R3.CraftWorld) getPlayer().getWorld()).getHandle()
                .addEntity(getCustomEntity());
        EntitySpawningManager.setBypass(false);
    }

    @Override
    protected void removeEntity() {
        getCustomEntity().dead = true;
        CustomEntities.customEntities.remove(customEntity);
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public org.bukkit.entity.Entity getEntity() {
        return customEntity.getEntity();
    }

    public Entity getCustomEntity() {
        return ((CraftEntity) customEntity.getEntity()).getHandle();
    }


}
