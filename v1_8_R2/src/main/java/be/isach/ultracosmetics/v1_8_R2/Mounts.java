package be.isach.ultracosmetics.v1_8_R2;

import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.v1_8_R2.mount.*;
import be.isach.ultracosmetics.version.IMounts;
import org.bukkit.entity.EntityType;

/**
 * @author RadBuilder
 */
public class Mounts implements IMounts {
    @Override
    public Class<? extends Mount> getSpiderClass() {
        return MountSpider.class;
    }

    @Override
    public Class<? extends Mount> getSlimeClass() {
        return MountSlime.class;
    }

    @Override
    public Class<? extends Mount> getHorrorClass() {
        return MountInfernalHorror.class;
    }

    @Override
    public Class<? extends Mount> getWalkingDeadClass() {
        return MountWalkingDead.class;
    }

    @Override
    public Class<? extends Mount> getRudolphClass() {
        return MountRudolph.class;
    }

    @Override
    public EntityType getHorrorType() {
        return EntityType.HORSE;
    }

    @Override
    public EntityType getWalkingDeadType() {
        return EntityType.HORSE;
    }

    @Override
    public EntityType getRudolphType() {
        return EntityType.HORSE;
    }
}
