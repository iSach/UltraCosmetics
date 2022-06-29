package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * Represents an instance of a kitten pet summoned by a player.
 *
 * @author iSach
 * @since 08-12-2015
 */
public class PetKitty extends Pet {

    public PetKitty(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("kitty"));
        // cat.setCatType(Cat.Type.RED_CAT); TODO, Ocelot.Type.RED_CAT in >= 1.12.2, Cat.Type.RED in >= 1.13
    }
}
