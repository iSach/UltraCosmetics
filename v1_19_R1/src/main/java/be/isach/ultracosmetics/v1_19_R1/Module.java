package be.isach.ultracosmetics.v1_19_R1;

import org.bukkit.Bukkit;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.log.SmartLogger;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;
import be.isach.ultracosmetics.v1_19_R1.customentities.CustomEntities;
import be.isach.ultracosmetics.v1_19_R1.mount.MountSlime;
import be.isach.ultracosmetics.v1_19_R1.mount.MountSpider;
import be.isach.ultracosmetics.version.IModule;

/**
 * @author RadBuilder
 */
public class Module implements IModule {
    @Override
    public boolean enable() {
        try {
            CustomEntities.registerEntities();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            SmartLogger logger = UltraCosmeticsData.get().getPlugin().getSmartLogger();
            logger.write(LogLevel.ERROR, "Failed to initialize NMS module.");
            if (Bukkit.getPluginManager().getPlugin("Citizens") != null)  {
                logger.write(LogLevel.ERROR, "POSSIBLE CAUSE: You may need to update Citizens to build #2492 or later.");
            }
            return false;
        }
        return true;
    }

    @Override
    public void disable() {}

    @Override
    public Class<? extends Mount> getSpiderClass() {
        return MountSpider.class;
    }

    @Override
    public Class<? extends Mount> getSlimeClass() {
        return MountSlime.class;
    }
}
