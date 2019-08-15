package be.isach.ultracosmetics.v1_13_R2.ridable;

import org.bukkit.entity.Player;

/**
 * @author BillyGalbreath
 *
 * Author of plugin: "Ridables"
 * Thanks for authorizing using Ridables code to make UC work!
 */
public class RidableMountEvent extends RidableEvent {
    private final Player player;

    public RidableMountEvent(RidableEntity entity, Player player) {
        super(entity);
        this.player = player;
    }

    /**
     * Gets the Player mounting the RidableEntity
     *
     * @return Player mounting the RidableEntity
     */
    public Player getPlayer() {
        return player;
    }
}