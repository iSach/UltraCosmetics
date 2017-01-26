package be.isach.ultracosmetics.v1_8_R3;

import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.v1_8_R3.mount.MountSkySquid;
import be.isach.ultracosmetics.v1_8_R3.mount.MountSlime;
import be.isach.ultracosmetics.v1_8_R3.mount.MountSpider;
import be.isach.ultracosmetics.version.IMounts;

public class Mounts implements IMounts{
    @Override
    public Class<? extends Mount> getSquidClass() {
        return MountSkySquid.class;
    }

    @Override
    public Class<? extends Mount> getSpiderClass() {
        return MountSpider.class;
    }

    @Override
    public Class<? extends Mount> getSlimeClass() {
        return MountSlime.class;
    }
}
