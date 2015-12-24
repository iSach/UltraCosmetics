package be.isach.ultracosmetics.util;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Sacha on 16/12/15.
 */
public class PacketSender {

    /**
     * Sends easily a packet to a player.
     *
     * @param player Packet destinator.
     * @param packet The packet to send.
     */
    public static void send(Player player, Packet packet) {
        if (player == null || packet == null)
            return;
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

}
