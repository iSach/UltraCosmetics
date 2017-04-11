package be.isach.ultracosmetics.run;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Project: UltraCosmetics
 * Package: be.isach.ultracosmetics.tick
 * Created by: Sacha
 * Created on: 21th June, 2016
 * at 14:03
 */
public class InvalidWorldChecker extends BukkitRunnable {

    private UltraCosmetics ultraCosmetics;

    public InvalidWorldChecker(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
    }

    @Override
    public void run() {
        for (UltraPlayer ultraPlayer : ultraCosmetics.getPlayerManager().getUltraPlayers()) {
            Player p = ultraPlayer.getBukkitPlayer();
            try {
                if (!((List) SettingsManager.getConfig().get("Enabled-Worlds")).contains(p.getWorld().getName())) {
                    ultraPlayer.removeMenuItem();
                    if (ultraPlayer.clear())
                        ultraPlayer.getBukkitPlayer().sendMessage(MessageManager.getMessage("World-Disabled"));
                }
            } catch (Exception exc) {
            }
        }
    }
}
