package be.isach.ultracosmetics.v1_17_R1;

import be.isach.ultracosmetics.version.IActionBar;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author RadBuilder
 */
public class ActionBar implements IActionBar {
    @Override
    public void sendActionMessage(Player player, String message) {
        CraftPlayer p = (CraftPlayer) player;
        Component cbc = Component.Serializer.fromJson("{\"text\": \"" + message + "\"}");
        ClientboundChatPacket ppoc = new ClientboundChatPacket(cbc, ChatType.GAME_INFO, UUID.randomUUID());
        p.getHandle().connection.send(ppoc);
    }
}
