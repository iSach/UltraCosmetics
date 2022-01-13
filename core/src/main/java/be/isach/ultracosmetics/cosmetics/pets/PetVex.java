package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.UCMaterial;

/**
 * Represents an instance of a vex pet summoned by a player.
 *
 * @author Chris6ix
 * @since 13-01-2021
 */
public class PetVex extends Pet {
    public PetVex(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("vex"), ItemFactory.create(UCMaterial.IRON_SWORD, UltraCosmeticsData.get().getItemNoPickupString()));
    }
}
