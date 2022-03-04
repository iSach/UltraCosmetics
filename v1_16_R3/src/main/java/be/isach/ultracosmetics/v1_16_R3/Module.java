package be.isach.ultracosmetics.v1_16_R3;

import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.v1_16_R3.customentities.CustomEntities;
import be.isach.ultracosmetics.v1_16_R3.mount.MountSlime;
import be.isach.ultracosmetics.v1_16_R3.mount.MountSpider;
import be.isach.ultracosmetics.version.IModule;

/**
 * @author RadBuilder
 */
public class Module implements IModule {
    @Override
    public boolean enable() {
        CustomEntities.registerEntities();
        return true;
    }

    @Override
    public void disable() {
        CustomEntities.unregisterEntities();
    }

    @Override
    public Class<? extends Mount<?>> getSpiderClass() {
        return MountSpider.class;
    }

    @Override
    public Class<? extends Mount<?>> getSlimeClass() {
        return MountSlime.class;
    }
}
