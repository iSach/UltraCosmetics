package be.isach.ultracosmetics.cosmetics.pets;

import com.cryptomorin.xseries.XMaterial;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * Represents an instance of a skeleton pet summoned by a player.
 *
 * @author Chris6ix
 * @since 12-04-2022
 */
public class PetSkeleton extends Pet {
    public PetSkeleton(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("skeleton"), XMaterial.BOW);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        entity.setFireTicks(0);
    }
}
