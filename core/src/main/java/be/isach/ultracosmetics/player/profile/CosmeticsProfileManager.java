package be.isach.ultracosmetics.player.profile;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        UUID uuid = up.getUuid();

        // First, create the cosmetic profile.
        CosmeticsProfile cosmeticsProfile = new CosmeticsProfile(uuid);
        cosmeticsProfiles.put(uuid, cosmeticsProfile);
        cosmeticsProfile.setUltraPlayer(up);
        up.setCosmeticsProfile(cosmeticsProfile);

        ultraCosmetics.getSmartLogger().write("Successfully created a cosmetics profile for " + up.getUsername());

        // Load the profile from File/MySQL and enable cosmetics.
        // cosmeticsProfile.loadFromData();
        // cosmeticsProfile.loadToPlayer(); TODO
    }

    public CosmeticsProfile getProfile(UltraPlayer ultraPlayer) {
        return cosmeticsProfiles.values().stream().filter(profile -> profile.getUltraPlayer() == ultraPlayer).findFirst().get();
    }

    public Map<UUID, CosmeticsProfile> getCosmeticsProfiles() {
        return cosmeticsProfiles;
    }

    public void clearPlayerFromProfile(UltraPlayer up) {
        cosmeticsProfiles.get(up.getUuid()).setUltraPlayer(null);
    }
}
