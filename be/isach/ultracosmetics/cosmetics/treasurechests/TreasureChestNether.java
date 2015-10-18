package be.isach.ultracosmetics.cosmetics.treasurechests;

import org.bukkit.Effect;
import org.bukkit.Material;

import java.util.UUID;

/**
 * Created by sacha on 19/08/15.
 */
public class TreasureChestNether extends TreasureChest {

    public TreasureChestNether(UUID owner) {
        super(owner, Material.NETHER_FENCE, Material.NETHERRACK, Material.NETHER_BRICK, Material.NETHER_BRICK, Material.GLOWSTONE, Effect.FLAME);
    }
}
