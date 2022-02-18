package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SqlLoader {

    private final List<UUID> loadList = Collections.synchronizedList(new ArrayList<UUID>());

    public SqlLoader(UltraCosmetics ultraCosmetics) {
        // Single "thread pool"
        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<UUID> iter = loadList.iterator();
                while (iter.hasNext()) {
                    Player p = Bukkit.getPlayer(iter.next());
                    if (p == null || !p.isOnline()) {
                        iter.remove();
                        continue;
                    }
                    UltraPlayer current = ultraCosmetics.getPlayerManager().getUltraPlayer(p);
                    //pre load two value then cache into server's
                    current.loadSQLValues();
                    iter.remove();
                }
            }
            // TODO: do we need a repeating task?
        }.runTaskTimerAsynchronously(ultraCosmetics, 0, 10);
    }


    public void addPreloadPlayer(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline()) {
            loadList.add(uuid);
        }
    }
}
