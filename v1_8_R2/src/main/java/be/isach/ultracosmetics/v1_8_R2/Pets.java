package be.isach.ultracosmetics.v1_8_R2;

import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.v1_8_R2.pets.PetPumpling;
import be.isach.ultracosmetics.version.IPets;

/**
 * @author RadBuilder
 */
public class Pets implements IPets {
	@Override
	public Class<? extends Pet> getPumplingClass() {
		return PetPumpling.class;
	}
}
