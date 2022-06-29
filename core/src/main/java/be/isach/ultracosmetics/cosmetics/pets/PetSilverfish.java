package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * Represents an instance of a silverfish pet summoned by a player.
 *
 * @author Chris6ix
 * @since 19-01-2022
 */
public class PetSilverfish extends Pet {
    public PetSilverfish(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("silverfish"));
    }
}
