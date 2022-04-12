package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.XMaterial;

/**
 * Represents an instance of a creeper pet summoned by a player.
 *
 * @author Chris6ix
 * @since 12-04-2022
 */
public class PetCreeper extends Pet {
    public PetCreeper(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("creeper"), ItemFactory.create(XMaterial.GUNPOWDER, UltraCosmeticsData.get().getItemNoPickupString()));
    }
}
