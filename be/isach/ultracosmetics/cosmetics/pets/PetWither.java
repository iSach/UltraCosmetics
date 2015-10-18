package be.isach.ultracosmetics.cosmetics.pets;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftWither;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;

import java.util.UUID;

/**
 * Created by Sacha on 12/10/15.
 */
public class PetWither extends Pet {

    public PetWither(UUID owner) {
        super(EntityType.WITHER, Material.SKULL_ITEM, (byte) 0x1, "Wither", "ultracosmetics.pets.wither", owner, PetType.WITHER);
    }

    @Override
    void onUpdate() {
        ((CraftWither)ent).getHandle().r(600);
    }

}
