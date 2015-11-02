package be.isach.ultracosmetics.cosmetics.treasurechests;

import org.bukkit.Effect;
import org.bukkit.Material;

import java.util.UUID;

/**
 * Created by sacha on 22/08/15.
 */
public class TreasureChestGlass extends TreasureChest {

    public TreasureChestGlass(UUID owner) {
        super(owner, Material.getMaterial(102), Material.GLASS, Material.STAINED_GLASS, Material.STAINED_GLASS, Material.SEA_LANTERN, Effect.EXPLOSION);
    }
}

