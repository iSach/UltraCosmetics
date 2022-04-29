package be.isach.ultracosmetics.cosmetics.pets;

import com.cryptomorin.xseries.XMaterial;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * Represents an instance of a blaze pet summoned by a player.
 *
 * @author Chris6ix
 * @since 06-04-2022
 */
public class PetBlaze extends Pet {
    public PetBlaze(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("blaze"), XMaterial.BLAZE_ROD);
    }
}
