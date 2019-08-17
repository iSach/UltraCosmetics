package be.isach.ultracosmetics.v1_9_R1;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.v1_9_R1.customentities.CustomEntities;
import be.isach.ultracosmetics.v1_9_R1.customentities.RideableSpider;
import be.isach.ultracosmetics.version.IModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
