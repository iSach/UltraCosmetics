package be.isach.ultracosmetics.cosmetics.suits;

import java.util.UUID;

/**
 * Created by Sacha on 20/12/15.
 */
public class SuitAstronaut extends Suit {

    public SuitAstronaut(UUID owner, ArmorSlot armorSlot) {
        super(owner, armorSlot, SuitType.ASTRONAUT);
    }

}
