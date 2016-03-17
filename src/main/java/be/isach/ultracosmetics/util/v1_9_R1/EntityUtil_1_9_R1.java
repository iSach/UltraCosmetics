package be.isach.ultracosmetics.util.v1_9_R1;

import be.isach.ultracosmetics.util.IEntityUtil;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

/**
 * Created by Sacha on 14/03/16.
 */
public class EntityUtil_1_9_R1 implements IEntityUtil {

    @Override
    public void setPassenger(Entity vehicle, Entity passenger) {
        net.minecraft.server.v1_8_R3.Entity craftVehicle = ((CraftEntity)vehicle).getHandle();
        net.minecraft.server.v1_8_R3.Entity craftPassenger = ((CraftEntity)passenger).getHandle();
        craftVehicle.passenger = craftPassenger;
    }
}
