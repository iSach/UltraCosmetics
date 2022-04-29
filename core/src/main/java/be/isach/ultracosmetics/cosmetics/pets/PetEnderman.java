package be.isach.ultracosmetics.cosmetics.pets;

import com.cryptomorin.xseries.XMaterial;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * Represents an instance of a enderman pet summoned by a player.
 *
 * @author Chris6ix
 * @since 12-04-2022
 */
public class PetEnderman extends Pet {
    public PetEnderman(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("enderman"), XMaterial.ENDER_PEARL);
    }
}
