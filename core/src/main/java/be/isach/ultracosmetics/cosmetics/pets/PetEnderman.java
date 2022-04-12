package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.XMaterial;

/**
 * Represents an instance of a enderman pet summoned by a player.
 *
 * @author Chris6ix
 * @since 12-04-2022
 */
public class PetEnderman extends Pet {
    public PetEnderman(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("enderman"), ItemFactory.create(XMaterial.ENDER_PEARL, UltraCosmeticsData.get().getItemNoPickupString()));
    }
}
