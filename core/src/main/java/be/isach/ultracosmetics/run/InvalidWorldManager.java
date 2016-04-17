package be.isach.ultracosmetics.run;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.SettingsManager;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Sacha on 15/12/15.
 */
public class InvalidWorldManager implements Runnable {

    @Override
    public void run() {
        for(CustomPlayer customPlayer : UltraCosmetics.getCustomPlayers()) {
            Player p = customPlayer.getPlayer();
            try {
                if (!((List<String>) SettingsManager.getConfig().get("Enabled-Worlds")).contains(p.getWorld().getName())) {
                    customPlayer.clear();
                    customPlayer.removeMenuItem();
                }
            } catch (Exception exc) {
            }
        }
    }
}
