package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.XMaterial;

/**
 * Represents an instance of a horse pet summoned by a player.
 *
 * @author Chris6ix
 * @since 06-04-2022
 */
public class PetHorse extends Pet {
    public PetHorse(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("horse"), ItemFactory.create(XMaterial.LEATHER_HORSE_ARMOR, UltraCosmeticsData.get().getItemNoPickupString()));
    }
}
