package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import com.cryptomorin.xseries.XMaterial;

/**
 * Represents an instance of a frog pet summoned by a player.
 *
 * @author Chris6ix
 * @since 08-06-2022
 */
public class PetFrog extends Pet {
    public PetFrog(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("frog"), XMaterial.LILY_PAD);
    }
}
