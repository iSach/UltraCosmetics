package be.isach.ultracosmetics.menu;

/**
 * Inventory click runnable.
 *
 * @author iSach
 * @since 08-09-2016
 */
@FunctionalInterface
public interface ClickRunnable {

    void run(ClickData data);
}
