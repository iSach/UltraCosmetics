package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraPlayer;

/**
 * Package: be.isach.ultracosmetics.cosmetics
 * Created by: sachalewin
 * Date: 21/07/16
 * Project: UltraCosmetics
 *
 * WIP.
 *
 * TODO
 *
 */
public abstract class Cosmetic {

    private UltraPlayer owner;
    private CosmeticType cosmeticType;
    private UltraCosmetics ultraCosmetics;

    public Cosmetic(UltraCosmetics ultraCosmetics, CosmeticType cosmeticType, UltraPlayer owner) {
        this.owner = owner;
        this.cosmeticType = cosmeticType;
        this.ultraCosmetics = ultraCosmetics;
    }

    public final UltraPlayer getOwner() {
        return owner;
    }

    public final UltraCosmetics getUltraCosmetics() {
        return ultraCosmetics;
    }

    public final CosmeticType getCosmeticType() {
        return cosmeticType;
    }
}
