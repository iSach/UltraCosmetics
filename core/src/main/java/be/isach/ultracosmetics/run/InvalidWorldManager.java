package be.isach.ultracosmetics.run;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Project: UltraCosmetics
 * Package: be.isach.ultracosmetics.run
 * Created by: Sacha
 * Created on: 21th June, 2016
 *             at 14:03
 */
public class InvalidWorldManager implements Runnable {

    @Override
    public void run() {
        for(CustomPlayer customPlayer : UltraCosmetics.getCustomPlayers()) {
            Player p = customPlayer.getPlayer();
            try {
                if (!((List) SettingsManager.getConfig().get("Enabled-Worlds")).contains(p.getWorld().getName())) {
                    customPlayer.clear();
                    customPlayer.removeMenuItem();
                    customPlayer.getPlayer().sendMessage(MessageManager.getMessage("World-Disabled"));
                }
            } catch (Exception exc) {
            }
        }
    }
}
