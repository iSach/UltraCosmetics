package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * Represents an instance of a diamond suit summoned by a player.
 *
 * @author iSach
 * @since 12-20-2015
 */
public class SuitDiamond extends Suit {
	public SuitDiamond(UltraPlayer owner, ArmorSlot armorSlot, UltraCosmetics ultraCosmetics) {
		super(owner, armorSlot, SuitType.valueOf("diamond"), ultraCosmetics);
	}

	@Override
	public void onUpdate() {
	}
}
