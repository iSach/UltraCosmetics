package be.isach.ultracosmetics.v1_12_R1.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.v1_12_R1.customentities.Pumpling;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_12_R1.Entity;

/**
 * @author RadBuilder
 */
public class PetPumpling extends CustomEntityPet {
    public PetPumpling(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("pumpling"), new ItemStack(Material.JACK_O_LANTERN));
    }

    @Override
    protected Entity getNewEntity() {
        return new Pumpling(((CraftPlayer) getPlayer()).getHandle().getWorld());
    }

    @Override
    public void setupEntity() {
        // use API when we can
        Zombie zombie = (Zombie) getEntity();
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearPathfinders(zombie);
        zombie.setBaby(true);
        zombie.getEquipment().setHelmet(new ItemStack(Material.PUMPKIN));
    }

    @Override
    public void onUpdate() {
        getNMSEntity().fireTicks = 0;
        Particles.FLAME.display(0.2f, 0.2f, 0.2f, ((Zombie) getEntity()).getEyeLocation(), 3);
        getNMSEntity().setInvisible(true);
    }
}
