package be.isach.ultracosmetics.version;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class APIAncientUtil implements IAncientUtil {
    @Override
    public void setSpeed(LivingEntity entity, double speed) {
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
    }

    @Override
    public void sendActionBarMessage(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}
