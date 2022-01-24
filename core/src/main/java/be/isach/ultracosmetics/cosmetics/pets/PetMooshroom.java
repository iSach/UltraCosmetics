package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.XMaterial;

/**
 * Represents an instance of a mooshroom pet summoned by a player.
 *
 * @author ataranlen
 * @since 06-26-2015
 */
public class PetMooshroom extends Pet {
    public PetMooshroom(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("mooshroom"), ItemFactory.create(XMaterial.RED_MUSHROOM, UltraCosmeticsData.get().getItemNoPickupString()));
    }
}
