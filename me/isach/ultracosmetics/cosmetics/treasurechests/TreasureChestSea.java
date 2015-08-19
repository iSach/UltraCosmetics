package me.isach.ultracosmetics.cosmetics.treasurechests;

import org.bukkit.Effect;
import org.bukkit.Material;

import java.util.UUID;

/**
 * Created by sacha on 19/08/15.
 */
public class TreasureChestSea extends TreasureChest {

    public TreasureChestSea(UUID owner) {
        super(owner, Material.COBBLE_WALL, Material.PRISMARINE, Material.PRISMARINE, Material.PRISMARINE, Material.SEA_LANTERN, Effect.COLOURED_DUST);
        this.b1data = 1;
        this.b3data = 2;
    }
}
