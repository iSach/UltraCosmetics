package be.isach.ultacosmetics.v1_9_R1;

import be.isach.ultacosmetics.v1_9_R1.mount.MountSkySquid;
import be.isach.ultacosmetics.v1_9_R1.mount.MountSlime;
import be.isach.ultacosmetics.v1_9_R1.mount.MountSpider;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
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
