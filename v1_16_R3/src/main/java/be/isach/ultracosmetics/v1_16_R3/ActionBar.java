package be.isach.ultracosmetics.v1_16_R3;

import be.isach.ultracosmetics.version.IActionBar;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author RadBuilder
 */
public class ActionBar implements IActionBar {
    @Override
    public void sendActionMessage(Player player, String message) {
        CraftPlayer p = (CraftPlayer) player;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, ChatMessageType.GAME_INFO, UUID.randomUUID());
        p.getHandle().playerConnection.sendPacket(ppoc);
    }
}
