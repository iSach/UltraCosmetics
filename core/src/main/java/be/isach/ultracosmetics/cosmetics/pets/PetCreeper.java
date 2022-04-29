package be.isach.ultracosmetics.cosmetics.pets;

import com.cryptomorin.xseries.XMaterial;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * Represents an instance of a creeper pet summoned by a player.
 *
 * @author Chris6ix
 * @since 12-04-2022
 */
public class PetCreeper extends Pet {
    public PetCreeper(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("creeper"), XMaterial.GUNPOWDER);
    }
}
