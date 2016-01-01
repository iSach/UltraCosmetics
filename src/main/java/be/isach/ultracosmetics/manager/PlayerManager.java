package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.CustomPlayer;

import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Sacha on 16/12/15.
 */
public class PlayerManager {

    private Map<UUID, CustomPlayer> playerCache;

    public PlayerManager() {
        this.playerCache = new ConcurrentHashMap<>();
    }

    public CustomPlayer getCustomPlayer(Player player) {
        CustomPlayer p = playerCache.get(player.getUniqueId());
        if (p == null)
            return create(player);
        return p;
    }

    public CustomPlayer create(Player player) {
        CustomPlayer customPlayer = new CustomPlayer(player.getUniqueId());
        if(!customPlayer.isLoaded){
        	return null;
        }
        playerCache.put(player.getUniqueId(), customPlayer);
        return customPlayer;
    }

    public boolean remove(Player player) {
        return playerCache.remove(player.getUniqueId()) != null;
    }

    public void clearPlayers() {
        Iterator<CustomPlayer> i = playerCache.values().iterator();
        while (i.hasNext())
            i.next().clear();
    }

    public Collection<CustomPlayer> getPlayers() {
        return playerCache.values();
    }

    public void dispose() {
        Collection<CustomPlayer> set = playerCache.values();
        for (CustomPlayer cp : set) {
            if (cp.currentTreasureChest != null)
                cp.currentTreasureChest.forceOpen(0);
            cp.clear();
            cp.removeMenuItem();
        }

        playerCache.clear();
        playerCache = null;
    }

}
