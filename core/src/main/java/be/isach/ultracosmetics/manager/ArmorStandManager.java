package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.UltraCosmetics;
import org.bukkit.entity.ArmorStand;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by sacha on 11/01/17.
 */
public class ArmorStandManager {

    private static final String META_TEXT = "UCArmorStand";

    private UltraCosmetics ultraCosmetics;

    public ArmorStandManager(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
    }

    public void makeUcStand(ArmorStand armorStand) {
        armorStand.setMetadata(META_TEXT, new FixedMetadataValue(ultraCosmetics, META_TEXT));
    }

    public boolean isUcStand(ArmorStand armorStand) {
        return armorStand.getMetadata(META_TEXT).equals(META_TEXT);
    }

}
