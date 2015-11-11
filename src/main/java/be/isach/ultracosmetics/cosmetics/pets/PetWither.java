package be.isach.ultracosmetics.cosmetics.pets;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftWither;
import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * Created by Sacha on 12/10/15.
 */
public class PetWither extends Pet {

    public PetWither(UUID owner) {
        super(EntityType.WITHER, Material.SKULL_ITEM, (byte) 0x1, "Wither", "ultracosmetics.pets.wither", owner, PetType.WITHER, "&7&oWatch out for me.");
    }

    @Override
    void onUpdate() {
        ((CraftWither)ent).getHandle().r(600);
    }

}
