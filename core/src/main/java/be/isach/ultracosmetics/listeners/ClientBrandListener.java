package be.isach.ultracosmetics.listeners;

import be.isach.ultracosmetics.UltraCosmetics;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class ClientBrandListener implements PluginMessageListener {
    private final UltraCosmetics ultraCosmetics;
    public ClientBrandListener(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("minecraft:brand")) {
            return;
        }
        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        // Discard message length
        input.readByte();
        ultraCosmetics.getPlayerManager().getUltraPlayer(player).setClientBrand(input.readLine());
    }

}
