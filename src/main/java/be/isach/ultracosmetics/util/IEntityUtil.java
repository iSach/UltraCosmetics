package be.isach.ultracosmetics.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Wither;

/**
 * Created by Sacha on 14/03/16.
 */
public interface IEntityUtil {

    void setPassenger(Entity vehicle, Entity passenger);

    void resetWitherSize(Wither wither);

    void setHorseSpeed(Horse horse, double speed);
}
