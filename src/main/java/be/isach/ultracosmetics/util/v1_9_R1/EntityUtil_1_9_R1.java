package be.isach.ultracosmetics.util.v1_9_R1;

import be.isach.ultracosmetics.util.IEntityUtil;
import net.minecraft.server.v1_9_R1.GenericAttributes;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftHorse;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftWither;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Wither;

/**
 * Created by Sacha on 14/03/16.
 */
public class EntityUtil_1_9_R1 implements IEntityUtil {

    @Override
    public void setPassenger(Entity vehicle, Entity passenger) {
        net.minecraft.server.v1_9_R1.Entity craftVehicle = ((CraftEntity) vehicle).getHandle();
        net.minecraft.server.v1_9_R1.Entity craftPassenger = ((CraftEntity) passenger).getHandle();
        if (craftVehicle.passengers.size() >= 1)
            craftVehicle.passengers.set(0, craftPassenger);
        else craftVehicle.passengers.add(craftPassenger);
    }

    @Override
    public void resetWitherSize(Wither wither) {
        ((CraftWither) wither).getHandle().l(600);
    }

    @Override
    public void setHorseSpeed(Horse horse, double speed) {
        ((CraftHorse)horse).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed);
    }
}
