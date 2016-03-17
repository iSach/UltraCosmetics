package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.cosmetics.pets.PetType;

import java.util.UUID;

/**
 * Created by Sacha on 7/03/16.
 */
public class CustomEntityPet_1_8_R3 extends Pet {

    public CustomEntityPet_1_8_R3(UUID owner, PetType petType) {
        super(owner, petType);
    }

    @Override
    void onUpdate() {

    }
}
