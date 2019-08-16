package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.UCMaterial;

/**
 * Represents an instance of a polar bear pet summoned by a player.
 *
 * @author RadBuilder
 * @since 10-21-2017
 */
public class PetPolarBear extends Pet {
    public PetPolarBear(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("polarbear"), ItemFactory.create(UCMaterial.SNOW_BLOCK, UltraCosmeticsData.get().getItemNoPickupString()));
    }
}
