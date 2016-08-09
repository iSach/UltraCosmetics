package be.isach.ultracosmetics.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.isach.ultracosmetics.UltraCosmetics;

public class SqlLoader {

    List<String> loadList = Collections.synchronizedList(new ArrayList<String>());

    private UltraCosmetics ultraCosmetics;

    public SqlLoader(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;

        // Single "thread pool"
        new BukkitRunnable() {
            @Override
            public void run() {
                if (loadList.size() <= 0) {
                    return;
                }
                Iterator<String> iter = loadList.iterator();
                while (iter.hasNext()) {
                    UltraPlayer current = null;
                    try {
                        Player p = Bukkit.getPlayer(UUID.fromString(iter.next()));
                        if (p == null || !p.isOnline()) {
                            iter.remove();
                            continue;
                        }
                        current = ultraCosmetics.getPlayerManager().getUltraPlayer(p);
                        //pre load two value then cache into server's
                        current.hasGadgetsEnabled();
                        current.canSeeSelfMorph();
                        current.isLoaded = true;
                        iter.remove();
                    } catch (Exception e) {
                        iter.remove();
                        // exception or not, just remove it.
                        continue;
                    }
                }
            }
        }.runTaskTimerAsynchronously(ultraCosmetics, 0, 10);
    }


    public void addPreloadPlayer(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline()) {
            loadList.add(uuid.toString());
        }
    }
}
