package be.isach.ultracosmetics.util;

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
    public static void send(Player player, Object packet) {
        if (player == null || packet == null)
            return;
        try {
            Object craftPlayer = ReflectionUtils.PackageType.CRAFTBUKKIT_ENTITY.getClass("CraftPlayer").cast(player);
            Object handle = player.getClass().getMethod("getHandle").invoke(craftPlayer);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", ReflectionUtils.PackageType.MINECRAFT_SERVER
                    .getClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
