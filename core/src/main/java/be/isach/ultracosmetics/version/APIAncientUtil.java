package be.isach.ultracosmetics.version;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class APIAncientUtil implements IAncientUtil {
    @Override
    public void setSpeed(LivingEntity entity, double speed) {
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
    }

    @Override
    public void setSilent(Entity entity, boolean silent) {
        entity.setSilent(silent);
    }
}
