package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.type.SuitType;

/**
 * Represents an instance of an astronaut suit summoned by a player.
 * 
 * @author 	iSach
 * @since 	12-20-2015
 */
public class SuitAstronaut extends Suit {

    public SuitAstronaut(UltraPlayer owner, ArmorSlot armorSlot, UltraCosmetics ultraCosmetics) {
        super(owner, armorSlot, SuitType.ASTRONAUT, ultraCosmetics);
    }
}
