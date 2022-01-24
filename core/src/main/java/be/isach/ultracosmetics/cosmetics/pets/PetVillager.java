package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.XMaterial;

/**
 * Represents an instance of a villager pet summoned by a player.
 *
 * @author RadBuilder
 * @since 07-02-2017
 */
public class PetVillager extends Pet {
    public PetVillager(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("villager"),
                ItemFactory.create(XMaterial.EMERALD, UltraCosmeticsData.get().getItemNoPickupString()));
    }
}