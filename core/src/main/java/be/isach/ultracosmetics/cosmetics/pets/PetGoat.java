package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.UCMaterial;

/**
 * Represents an instance of a goat pet summoned by a player.
 *
 * @author Chris6ix
 * @since 18-01-2021
 */

/*Temporarily using WHEAT as material until https://minecraft.fandom.com/wiki/Goat_Horn comes to Java edition*/
public class PetGoat extends Pet {
    public PetGoat(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("goat"), ItemFactory.create(UCMaterial.WHEAT, UltraCosmeticsData.get().getItemNoPickupString()));
    }
}
