package be.isach.ultracosmetics.player.profile;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * Used to save what cosmetics a player toggled.
 */
public class FileCosmeticsProfile extends CosmeticsProfile {

    public FileCosmeticsProfile(UltraPlayer ultraPlayer, UltraCosmetics ultraCosmetics) {
        super(ultraPlayer, ultraCosmetics);
    }

    @Override
    protected void load() {
        data.loadFromFile();
    }

    @Override
    public void save() {
        data.saveToFile();
    }

    
}
