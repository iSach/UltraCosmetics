package be.isach.ultracosmetics.version;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/*
 * A collection of things that are only in Spigot API on MC >= 1.9
 */

public interface IAncientUtil {
    public void setSpeed(LivingEntity entity, double speed);
    public void setSilent(Entity entity, boolean silent);
}
