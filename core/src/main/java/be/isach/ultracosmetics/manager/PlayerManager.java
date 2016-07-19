package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraPlayer;
import be.isach.ultracosmetics.config.SettingsManager;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Sacha on 16/12/15.
 */
public class PlayerManager {

    private Map<UUID, UltraPlayer> playerCache;

    public PlayerManager() {
        this.playerCache = new ConcurrentHashMap<>();
    }

    public UltraPlayer getCustomPlayer(Player player) {
        UltraPlayer p = playerCache.get(player.getUniqueId());
        if (p == null)
            return create(player);
        return p;
    }

    public UltraPlayer create(Player player) {
        UltraPlayer customPlayer = new UltraPlayer(player.getUniqueId());
        playerCache.put(player.getUniqueId(), customPlayer);
        return customPlayer;
    }

    public boolean remove(Player player) {
        return playerCache.remove(player.getUniqueId()) != null;
    }

    public Collection<UltraPlayer> getPlayers() {
        return playerCache.values();
    }

    public void dispose() {
        Collection<UltraPlayer> set = playerCache.values();
        for (UltraPlayer cp : set) {
            if (cp.currentTreasureChest != null)
                cp.currentTreasureChest.forceOpen(0);
            cp.clear();
            cp.removeMenuItem();
        }

        playerCache.clear();
        playerCache = null;
    }

}
