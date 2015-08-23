package me.isach.ultracosmetics.cosmetics.treasurechests;

import org.bukkit.Effect;
import org.bukkit.Material;

import java.util.UUID;

/**
 * Created by sacha on 22/08/15.
 */
public class TreasureChestDesert extends TreasureChest {

    public TreasureChestDesert(UUID owner) {
        super(owner, Material.COBBLE_WALL, Material.getMaterial(98), Material.COBBLESTONE, Material.STONE, Material.SEA_LANTERN, Effect.HAPPY_VILLAGER);
    }
}

