package be.isach.ultracosmetics.run;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.SettingsManager;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Sacha on 15/12/15.
 */
public class InvalidWorldManager implements Runnable {

    @Override
    public void run() {
        Iterator<CustomPlayer> playerIterator = Core.getCustomPlayers().iterator();
        while (playerIterator.hasNext()) {
            CustomPlayer customPlayer = playerIterator.next();
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
