package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * Represents an instance of an astronaut suit summoned by a player.
 *
 * @author iSach
 * @since 12-20-2015
 */
public class SuitAstronaut extends Suit {
    public SuitAstronaut(UltraPlayer owner, SuitType suitType, UltraCosmetics ultraCosmetics) {
        super(owner, suitType, ultraCosmetics);
    }
}
