package be.isach.ultracosmetics.cosmetics.pets;

import com.cryptomorin.xseries.XMaterial;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * Represents an instance of a zombie pet summoned by a player.
 *
 * @author Chris6ix
 * @since 13-04-2022
 */
public class PetZombie extends Pet {
    public PetZombie(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("zombie"), XMaterial.ROTTEN_FLESH);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        entity.setFireTicks(0);
    }
}
