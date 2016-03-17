package be.isach.ultracosmetics.util.v1_8_R3;

import be.isach.ultracosmetics.util.IEntityUtil;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftWither;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Wither;

/**
 * Created by Sacha on 14/03/16.
 */
public class EntityUtil_1_8_R3 implements IEntityUtil {

    @Override
    public void setPassenger(Entity vehicle, Entity passenger) {
        net.minecraft.server.v1_8_R3.Entity craftVehicle = ((CraftEntity)vehicle).getHandle();
        net.minecraft.server.v1_8_R3.Entity craftPassenger = ((CraftEntity)passenger).getHandle();
        craftVehicle.passenger = craftPassenger;
    }

    @Override
    public void resetWitherSize(Wither wither) {
        ((CraftWither) wither).getHandle().r(600);
    } @Override

    public void setHorseSpeed(Horse horse, double speed) {
        ((CraftHorse)horse).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed);
    }
}
