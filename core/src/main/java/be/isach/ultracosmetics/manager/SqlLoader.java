package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class SqlLoader {
    private final UltraCosmetics ultraCosmetics;
    public SqlLoader(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
    }

    public void addPreloadPlayer(UUID uuid) {
        new BukkitRunnable() {
            @Override
            public void run() {
                UltraPlayer player = ultraCosmetics.getPlayerManager().getUltraPlayer(Bukkit.getPlayer(uuid));
                if (player == null) return;
                player.loadSQLValues();
            }
        }.runTaskAsynchronously(ultraCosmetics);
    }
}
