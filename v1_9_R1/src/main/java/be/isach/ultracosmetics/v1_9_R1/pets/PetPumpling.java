package be.isach.ultracosmetics.v1_9_R1.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.UCMaterial;

/**
 * Created by Sacha on 18/10/15.
 */
public class PetPumpling extends CustomEntityPet {
    public PetPumpling(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("pumpling"), ItemFactory.create(UCMaterial.JACK_O_LANTERN, UltraCosmeticsData.get().getItemNoPickupString()));
    }
}
