package be.isach.ultracosmetics.v1_8_R3.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.pets.IPetCustomEntity;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.cosmetics.pets.PetType;
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

import java.util.UUID;

/**
 * Created by Sacha on 7/03/16.
 */
public class CustomEntityPet extends Pet {

    /**
     * Custom Entity.
     */
    public IPetCustomEntity customEntity;

    public CustomEntityPet(UUID owner, PetType petType) {
        super(owner, petType);
    }

    @Override
    public void equip() {
        followTask = new PlayerFollower(this, getPlayer());
        if (UltraCosmetics.getCustomPlayer(getPlayer()).currentPet != null)
            UltraCosmetics.getCustomPlayer(getPlayer()).removePet();
        UltraCosmetics.getCustomPlayer(getPlayer()).currentPet = this;

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
                            UltraCosmetics.getCustomPlayer(getPlayer()).currentPet = null;
                        for (Item i : items)
                            i.remove();
                        items.clear();
                        try {
                            HandlerList.unregisterAll(instance);
                            HandlerList.unregisterAll(instance.listener);
                        } catch (Exception exc) {
                        }
                        cancel();
                        return;
                    }
                    if (Bukkit.getPlayer(getOwner()) != null
                            && UltraCosmetics.getCustomPlayer(Bukkit.getPlayer(getOwner())).currentPet != null
                            && UltraCosmetics.getCustomPlayer(Bukkit.getPlayer(getOwner())).currentPet.getType() == getType()) {
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
        runnable.runTaskTimer(UltraCosmetics.getInstance(), 0, 6);
        listener = new PetListener(this);

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
        armorStand.setMetadata("C_AD_ArmorStand", new FixedMetadataValue(UltraCosmetics.getInstance(), "C_AD_ArmorStand"));
        if (UltraCosmetics.getCustomPlayer(getPlayer()).getPetName(getType().getConfigName()) != null)
            armorStand.setCustomName(UltraCosmetics.getCustomPlayer(getPlayer()).getPetName(getType().getConfigName()));

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
    protected void onUpdate() {

    }

    @Override
    public org.bukkit.entity.Entity getEntity() {
        return customEntity.getEntity();
    }

    public Entity getCustomEntity() {
        return ((CraftEntity) customEntity.getEntity()).getHandle();
    }


}
