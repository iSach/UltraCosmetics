package be.isach.ultracosmetics.run;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
            // not sure what causes p to be null, but it happens in some circumstances apparently
            // https://mcpaste.io/1bbcbf856c5e503b
            if (p != null && !SettingsManager.isAllowedWorld(p.getWorld())) {
                ultraPlayer.removeMenuItem();
                ultraPlayer.setQuitting(true);
                if (ultraPlayer.clear()) {
                    ultraPlayer.getBukkitPlayer().sendMessage(MessageManager.getMessage("World-Disabled"));
                }
                ultraPlayer.setQuitting(false);
            }
        }
    }
}
