package be.isach.ultracosmetics.stats;

import be.isach.ultracosmetics.UltraCosmetics;

public abstract class AbstractMetrics {

    private UltraCosmetics plugin;

    public AbstractMetrics(UltraCosmetics plugin) {
        this.plugin = plugin;
    }

    public UltraCosmetics getPlugin() {
        return plugin;
    }
}
