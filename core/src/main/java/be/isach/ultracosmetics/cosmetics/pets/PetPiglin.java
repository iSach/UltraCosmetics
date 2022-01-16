package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.UCMaterial;

/**
 * Represents an instance of a piglin pet summoned by a player.
 *
 * @author Chris6ix
 * @since 16-01-2021
 */
public class PetPiglin extends Pet {
    public PetPiglin(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("piglin"), ItemFactory.create(UCMaterial.GOLD_INGOT, UltraCosmeticsData.get().getItemNoPickupString()));
    }
}
