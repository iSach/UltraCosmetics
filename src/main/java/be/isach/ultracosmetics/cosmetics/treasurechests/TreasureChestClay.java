package be.isach.ultracosmetics.cosmetics.treasurechests;

import org.bukkit.Effect;
import org.bukkit.Material;

import java.util.UUID;

/**
 * Created by sacha on 22/08/15.
 */
public class TreasureChestClay extends TreasureChest {

    public TreasureChestClay(UUID owner) {
        super(owner, Material.STAINED_GLASS_PANE, Material.STAINED_CLAY, Material.QUARTZ_BLOCK, Material.QUARTZ_BLOCK, Material.SEA_LANTERN, Effect.CLOUD);
        this.b1data = 11;
        this.barrierData = 3;
    }
}

