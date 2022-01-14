package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.UCMaterial;

/**
 * Represents an instance of a fox pet summoned by a player.
 *
 * @author Chris6ix
 * @since 14-01-2021
 */
public class PetFox extends Pet {
    public PetFox(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("fox"), ItemFactory.create(UCMaterial.SWEET_BERRIES, UltraCosmeticsData.get().getItemNoPickupString()));
    }
}
