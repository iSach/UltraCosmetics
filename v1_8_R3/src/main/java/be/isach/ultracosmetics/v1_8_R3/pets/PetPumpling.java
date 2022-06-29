package be.isach.ultracosmetics.v1_8_R3.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.v1_8_R3.customentities.Pumpling;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import net.minecraft.server.v1_8_R3.Entity;

/**
 * Created by Sacha on 18/10/15.
 */
public class PetPumpling extends CustomEntityPet {
    public PetPumpling(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("pumpling"));
    }

    @Override
    protected Entity getNewEntity() {
        return new Pumpling(((CraftPlayer) getPlayer()).getHandle().getWorld(), getOwner().getBukkitPlayer());
    }
}
