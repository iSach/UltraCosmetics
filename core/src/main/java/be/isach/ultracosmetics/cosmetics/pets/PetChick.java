package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import com.cryptomorin.xseries.XMaterial;

/**
 * Represents an instance of a chick pet summoned by a player.
 *
 * @author iSach
 * @since 08-12-2015
 */
public class PetChick extends Pet {
    public PetChick(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("chick"), ItemFactory.create(XMaterial.EGG, UltraCosmeticsData.get().getItemNoPickupString()));
    }
}