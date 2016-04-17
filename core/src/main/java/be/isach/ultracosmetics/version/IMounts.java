package be.isach.ultracosmetics.version;

import be.isach.ultracosmetics.cosmetics.mounts.Mount;

public interface IMounts {
    Class<? extends Mount> getSquidClass();
    Class<? extends Mount> getSpiderClass();
    Class<? extends Mount> getSlimeClass();
}
