package be.isach.ultracosmetics.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import be.isach.ultracosmetics.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.isach.ultracosmetics.UltraCosmetics;

public class SQLLoaderManager {

    List<String> loadList = Collections.synchronizedList(new ArrayList<String>());

    public SQLLoaderManager() {
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
                        current = UltraCosmetics.getPlayerManager().getCustomPlayer(p);
                        //pre load two value then cache into server's
                        current.hasGadgetsEnabled();
                        current.canSeeSelfMorph();
                        current.isLoaded = true;
                    } catch (Exception e) {
                        // exception or not, just remove it.
                        continue;
                    } finally {
                        iter.remove();
                    }
                }
            }
        }.runTaskTimer(UltraCosmetics.getInstance(), 0, 10);
    }


    public void addPreloadPlayer(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline()) {
            loadList.add(uuid.toString());
        }
    }
}
