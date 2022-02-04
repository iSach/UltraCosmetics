package be.isach.ultracosmetics.version;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/*
 * A collection of things that are only in Spigot API on MC >= 1.9
 */

public interface IAncientUtil {
    public void setSpeed(LivingEntity entity, double speed);
    public void sendActionBarMessage(Player player, String message);
}
