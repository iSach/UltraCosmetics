package be.isach.ultracosmetics.cosmetics.treasurechests;

import org.bukkit.Effect;
import org.bukkit.Material;

import java.util.UUID;

/**
 * Created by sacha on 22/08/15.
 */
public class TreasureChestDirt extends TreasureChest {

    public TreasureChestDirt(UUID owner) {
        super(owner, Material.FENCE, Material.GRASS, Material.DIRT, Material.DIRT, Material.SEA_LANTERN, Effect.HAPPY_VILLAGER);
        this.b2data = 1;
        this.b3data = 1;
    }
}

