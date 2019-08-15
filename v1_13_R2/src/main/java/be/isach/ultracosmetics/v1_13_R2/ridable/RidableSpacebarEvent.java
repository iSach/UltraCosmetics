package be.isach.ultracosmetics.v1_13_R2.ridable;

/**
 * @author BillyGalbreath
 *
 * Author of plugin: "Ridables"
 * Thanks for authorizing using Ridables code to make UC work!
 */
public class RidableSpacebarEvent extends RidableEvent {
    private boolean handled;

    public RidableSpacebarEvent(RidableEntity entity) {
        super(entity);
    }

    /**
     * Get if a plugin is handling this event
     *
     * @return True if a plugin is handling this event
     */
    public boolean isHandled() {
        return handled;
    }

    /**
     * Set if this event is handled by a plugin
     *
     * @param handled True to mark the event as handled by a plugin
     */
    public void setHandled(boolean handled) {
        this.handled = handled;
    }
}
