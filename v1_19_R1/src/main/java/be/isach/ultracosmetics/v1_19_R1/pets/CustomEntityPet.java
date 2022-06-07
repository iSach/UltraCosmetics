package be.isach.ultracosmetics.v1_19_R1.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_19_R1.customentities.CustomEntities;

import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.inventory.ItemStack;
import net.minecraft.world.entity.Entity;

/**
 * @author RadBuilder
 */
public abstract class CustomEntityPet extends Pet {

    public CustomEntityPet(UltraPlayer owner, UltraCosmetics ultraCosmetics, PetType petType, ItemStack dropItem) {
        super(owner, ultraCosmetics, petType, dropItem);
    }

    @Override
    public org.bukkit.entity.Entity spawnEntity() {
        entity = getNewEntity().getBukkitEntity();
        double x = getPlayer().getLocation().getX();
        double y = getPlayer().getLocation().getY();
        double z = getPlayer().getLocation().getZ();
        // must refer to entity as an Entity
        getNMSEntity().moveTo(x, y + 2, z, 0, 0);
        ((CraftWorld) getPlayer().getWorld()).getHandle().addFreshEntity(getNMSEntity());
        CustomEntities.addCustomEntity(getNMSEntity());
        return getEntity();
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

    public Entity getNMSEntity() {
        return ((CraftEntity)entity).getHandle();
    }

    public abstract Entity getNewEntity();
}
