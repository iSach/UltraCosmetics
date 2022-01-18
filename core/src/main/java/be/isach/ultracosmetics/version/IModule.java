package be.isach.ultracosmetics.version;

import be.isach.ultracosmetics.cosmetics.mounts.Mount;

public interface IModule {
    public void enable();

    public void disable();

    public Class<? extends Mount> getSpiderClass();

    public Class<? extends Mount> getSlimeClass();
}
