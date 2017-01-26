package be.isach.ultracosmetics.v1_8_R2;

import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.v1_8_R2.mount.MountSkySquid;
import be.isach.ultracosmetics.v1_8_R2.mount.MountSlime;
import be.isach.ultracosmetics.v1_8_R2.mount.MountSpider;
import be.isach.ultracosmetics.version.IMounts;

/**
 * @author RadBuilder
 */
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
