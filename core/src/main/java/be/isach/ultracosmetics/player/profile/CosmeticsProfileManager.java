package be.isach.ultracosmetics.player.profile;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

public class CosmeticsProfileManager {

    private Map<UUID, CosmeticsProfile> cosmeticsProfiles;

    private UltraCosmetics ultraCosmetics;

    public CosmeticsProfileManager(UltraCosmetics ultraCosmetics) {
        this.cosmeticsProfiles = new HashMap<>();
        this.ultraCosmetics = ultraCosmetics;
    }

    public CosmeticsProfile getProfile(UUID uuid) {
        return cosmeticsProfiles.get(uuid);
    }

    /**
     * Initialize players.
     */
    public void initPlayers() {
        for (UltraPlayer up : ultraCosmetics.getPlayerManager().getUltraPlayers()) {
            initForPlayer(up);
        }
    }

    /**
     * Initialize a player who doesn't have a cosmetic profile on the current UC Instance.
     *
     * @param up
     */
    public void initForPlayer(UltraPlayer up) {
        UUID uuid = up.getUUID();

        // First, create the cosmetic profile.
        CosmeticsProfile cosmeticsProfile = new CosmeticsProfile(uuid);
        cosmeticsProfiles.put(uuid, cosmeticsProfile);
        cosmeticsProfile.setUltraPlayer(up);

        // Try to load
        cosmeticsProfile.loadFromData();

        up.setCosmeticsProfile(cosmeticsProfile);

        // ultraCosmetics.getSmartLogger().write("Successfully created a cosmetics profile for " + up.getUsername());

        // run sync because cosmetics have to run sync
        Bukkit.getScheduler().runTask(ultraCosmetics, () -> cosmeticsProfile.loadToPlayer());
    }

    public CosmeticsProfile getProfile(UltraPlayer ultraPlayer) {
        return cosmeticsProfiles.values().stream().filter(profile -> profile.getUltraPlayer() == ultraPlayer).findFirst().get();
    }

    public Map<UUID, CosmeticsProfile> getCosmeticsProfiles() {
        return cosmeticsProfiles;
    }

    public void clearPlayerFromProfile(UltraPlayer up) {
        cosmeticsProfiles.get(up.getUUID()).setUltraPlayer(null);
    }
}
