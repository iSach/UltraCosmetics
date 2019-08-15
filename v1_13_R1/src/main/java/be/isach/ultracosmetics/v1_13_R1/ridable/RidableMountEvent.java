package be.isach.ultracosmetics.v1_13_R1.ridable;

import org.bukkit.entity.Player;

/**
 * @author BillyGalbreath
 * <p>
 * Author of plugin: "Ridables"
 * Thanks for authorizing using Ridables code to make UC work!
 * <p>
 * Called when a Player mounts onto a RidableEntity
 * <p>
 * This event is called <b>before</b> {@link org.bukkit.event.vehicle.VehicleEnterEvent} and {@link org.spigotmc.event.entity.EntityMountEvent}
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