package be.isach.ultracosmetics.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import be.isach.ultracosmetics.version.IAncientUtil;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class AncientUtil implements IAncientUtil {
    @Override
    public void setSpeed(LivingEntity entity, double speed) {
        ((CraftLivingEntity) entity).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed);
    }

    @Override
    public void setSilent(Entity entity, boolean silent) {
        net.minecraft.server.v1_8_R3.Entity nms = ((CraftEntity)entity).getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        // Copy entity's tags into nbt
        nms.c(nbt);
        nbt.setBoolean("Silent", true);
        // Copy nbt over entity's tags
        nms.f(nbt);
    }
}
