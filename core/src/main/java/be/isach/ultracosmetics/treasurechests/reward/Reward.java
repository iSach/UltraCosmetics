package be.isach.ultracosmetics.treasurechests.reward;

import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * Package: be.isach.ultracosmetics.treasurechests.reward
 * Created by: sachalewin
 * Date: 9/08/16
 * Project: UltraCosmetics
 */
public abstract class Reward {

    public Reward() {

    }

    abstract void giveTo(UltraPlayer ultraPlayer);

}
