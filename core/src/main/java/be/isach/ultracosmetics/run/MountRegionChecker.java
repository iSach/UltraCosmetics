package be.isach.ultracosmetics.run;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.player.UltraPlayer;

/*
 * Mounts that aren't horses don't trigger PlayerMoveEvent I guess,
 * so we have to check manually for those.
 */

public class MountRegionChecker extends BukkitRunnable {
    private UltraPlayer player;
    private UltraCosmetics uc;
    public MountRegionChecker(UltraPlayer player, UltraCosmetics uc) {
        this.player = player;
        this.uc = uc;
    }

    @Override
    public void run() {
        Player bukkitPlayer = player.getBukkitPlayer();
        // Mount#onClear() will cancel it for us
        if (bukkitPlayer == null) return;
        if (!uc.areCosmeticsAllowedInRegion(bukkitPlayer)) {
            player.clear();
            bukkitPlayer.sendMessage(MessageManager.getMessage("Region-Disabled"));
        }
    }
}
