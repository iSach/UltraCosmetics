package be.isach.ultracosmetics.cosmetics.treasurechests;

import org.bukkit.Effect;
import org.bukkit.Material;

import java.util.UUID;

/**
 * Created by sacha on 19/08/15.
 */
public class TreasureChestIce extends TreasureChest {

    public TreasureChestIce(UUID owner) {
        super(owner, Material.STAINED_GLASS_PANE, Material.PACKED_ICE, Material.ICE, Material.QUARTZ_BLOCK, Material.SEA_LANTERN, Effect.SNOW_SHOVEL);
    }
}
