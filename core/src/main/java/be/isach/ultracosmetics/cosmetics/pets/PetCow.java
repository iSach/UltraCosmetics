package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.UCMaterial;

/**
 * Represents an instance of a cow pet summoned by a player.
 *
 * @author iSach
 * @since 08-12-2015
 */
public class PetCow extends Pet {
    public PetCow(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("cow"), ItemFactory.create(UCMaterial.MILK_BUCKET, UltraCosmeticsData.get().getItemNoPickupString()));
    }
}