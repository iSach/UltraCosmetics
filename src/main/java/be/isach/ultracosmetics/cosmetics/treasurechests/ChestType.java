package be.isach.ultracosmetics.cosmetics.treasurechests;

import org.bukkit.Material;

/**
 * Created by Sacha on 11/11/15.
 */
public enum ChestType {

    NORMAL(Material.CHEST),
    ENDER(Material.ENDER_CHEST),
    TRAPPED(Material.TRAPPED_CHEST);

    private Material type;

    ChestType(Material type) {
        this.type = type;
    }

    public Material getType() {
        return type;
    }
}
