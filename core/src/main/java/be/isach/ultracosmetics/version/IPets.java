package be.isach.ultracosmetics.version;

import be.isach.ultracosmetics.cosmetics.pets.Pet;

public interface IPets {
    Class<? extends Pet> getPumplingClass();
}
