package be.isach.ultracosmetics.v1_12_R1.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_12_R1.customentities.CustomEntities;

import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

/**
 * @author RadBuilder
 */
public abstract class CustomEntityPet extends Pet {

    public CustomEntityPet(UltraPlayer owner, UltraCosmetics ultraCosmetics, PetType petType) {
        super(owner, ultraCosmetics, petType);
    }

    @Override
    public Entity spawnEntity() {
        entity = getNewEntity().getBukkitEntity();
        double x = getPlayer().getLocation().getX();
        double y = getPlayer().getLocation().getY();
        double z = getPlayer().getLocation().getZ();
        getNMSEntity().setLocation(x, y, z, 0, 0);
        ((CraftWorld) getPlayer().getWorld()).getHandle().addEntity(getNMSEntity());
        CustomEntities.addCustomEntity(getNMSEntity());
        return entity;
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

    public net.minecraft.server.v1_12_R1.Entity getNMSEntity() {
        return ((CraftEntity) entity).getHandle();
    }

    protected abstract net.minecraft.server.v1_12_R1.Entity getNewEntity();
}
