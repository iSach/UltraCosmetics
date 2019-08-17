package be.isach.ultracosmetics.v1_8_R2;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.v1_8_R2.customentities.CustomEntities;
import be.isach.ultracosmetics.version.IModule;

/**
 * @author RadBuilder
 */
public class Module implements IModule {

    Metrics metrics;

    @Override
    public void enable() {
        UltraCosmetics pl = UltraCosmeticsData.get().getPlugin();
        this.metrics = new Metrics(pl, pl.getSmartLogger());
        UltraCosmeticsData.get().setMetrics(metrics);
        CustomEntities.registerEntities();
    }

    @Override
    public void disable() {
        CustomEntities.unregisterEntities();
    }
}
