package be.isach.ultracosmetics.menu;

import be.isach.ultracosmetics.player.UltraPlayer;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Inventory click data.
 *
 * @author iSach
 * @since 08-09-2016
 */
public class ClickData {

    private final Inventory inventory;
    private final UltraPlayer clicker;
    private final ClickType click;
    private final ItemStack clicked;
    private final int slot;

    public ClickData(Inventory inventory, UltraPlayer clicker, ClickType click, ItemStack clicked, int slot) {
        this.inventory = inventory;
        this.clicker = clicker;
        this.click = click;
        this.clicked = clicked;
        this.slot = slot;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack getClicked() {
        return clicked;
    }

    public int getSlot() {
        return slot;
    }

    public ClickType getClick() {
        return click;
    }

    public UltraPlayer getClicker() {
        return clicker;
    }
}
