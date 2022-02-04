package be.isach.ultracosmetics.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import be.isach.ultracosmetics.version.IAncientUtil;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class AncientUtil implements IAncientUtil {
    @Override
    public void setSpeed(LivingEntity entity, double speed) {
        ((CraftLivingEntity) entity).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed);
    }

    @Override
    public void sendActionBarMessage(Player player, String message) {
        CraftPlayer p = (CraftPlayer) player;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        p.getHandle().playerConnection.sendPacket(ppoc);
    }
}