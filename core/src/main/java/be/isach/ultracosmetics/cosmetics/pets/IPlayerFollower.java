package be.isach.ultracosmetics.cosmetics.pets;

import org.bukkit.entity.Player;

/**
 * Created by Sacha on 7/03/16.
 */
public interface IPlayerFollower {

    void follow(Player player);

    Runnable getTask();

}
