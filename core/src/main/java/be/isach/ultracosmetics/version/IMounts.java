package be.isach.ultracosmetics.version;

import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import org.bukkit.entity.EntityType;

public interface IMounts {
    Class<? extends Mount> getSquidClass();
    Class<? extends Mount> getSpiderClass();
    Class<? extends Mount> getSlimeClass();
    Class<? extends Mount> getHorrorClass();
    Class<? extends Mount> getWalkingDeadClass();
    Class<? extends Mount> getRudolphClass();
    EntityType getHorrorType();
    EntityType getWalkingDeadType();
    EntityType getRudolphType();
}
