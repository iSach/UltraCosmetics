package be.isach.ultracosmetics.cosmetics.pets;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftWither;

import java.util.UUID;

/**
 * Created by Sacha on 12/10/15.
 */
public class PetWither extends Pet {

    public PetWither(UUID owner) {
        super(owner, PetType.WITHER);
    }

    @Override
    void onUpdate() {
        ((CraftWither) entity).getHandle().r(600);
    }

}
