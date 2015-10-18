package be.isach.ultracosmetics.cosmetics.treasurechests;

import org.bukkit.Effect;
import org.bukkit.Material;

import java.util.UUID;

/**
 * Created by sacha on 22/08/15.
 */
public class TreasureChestEnd extends TreasureChest {

    public TreasureChestEnd(UUID owner) {
        super(owner, Material.STAINED_GLASS_PANE, Material.ENDER_STONE, Material.OBSIDIAN, Material.OBSIDIAN, Material.SEA_LANTERN, Effect.PORTAL);
        enderChests = true;
        barrierData = 10;
    }
}

