package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.XMaterial;

/**
 * Represents an instance of a skeleton pet summoned by a player.
 *
 * @author Chris6ix
 * @since 12-04-2022
 */
public class PetSkeleton extends Pet {
    public PetSkeleton(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("skeleton"), ItemFactory.create(XMaterial.BOW, UltraCosmeticsData.get().getItemNoPickupString()));
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        entity.setFireTicks(0);
    }
}
