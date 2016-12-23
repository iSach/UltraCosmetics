package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.type.SuitType;

/**
 * Created by Sacha on 20/12/15.
 */
public class SuitAstronaut extends Suit {

    public SuitAstronaut(UltraPlayer owner, ArmorSlot armorSlot, UltraCosmetics ultraCosmetics) {
        super(owner, armorSlot, SuitType.ASTRONAUT, ultraCosmetics);
    }

}
