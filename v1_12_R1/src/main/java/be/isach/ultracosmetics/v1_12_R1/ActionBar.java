package be.isach.ultracosmetics.v1_12_R1;

import be.isach.ultracosmetics.version.IActionBar;
import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @author RadBuilder
 */
public class ActionBar implements IActionBar {
	@Override
	public void sendActionMessage(Player player, String message) {
		CraftPlayer p = (CraftPlayer) player;
		IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, ChatMessageType.GAME_INFO);
		p.getHandle().playerConnection.sendPacket(ppoc);
	}
}
