package be.isach.ultracosmetics.cosmetics.treasurechests;

import org.bukkit.Effect;
import org.bukkit.Material;

import java.util.UUID;

/**
 * Created by Sacha on 18/10/15.
 */
public class HalloweenTreasureChest extends TreasureChest {

    public HalloweenTreasureChest(UUID owner) {
        super(owner, Material.NETHER_FENCE, Material.PUMPKIN, Material.SOUL_SAND, Material.SOUL_SAND, Material.JACK_O_LANTERN, Effect.FLAME);
    }
}
