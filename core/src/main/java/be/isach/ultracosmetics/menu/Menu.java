package be.isach.ultracosmetics.menu;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Package: be.isach.ultracosmetics.menu
 * Created by: iSach
 * Date: 5/07/16
 * Project: UltraCosmetics
 * <p>
 * Description : Represents a Menu. A menu can have multiple pages in case of cosmetics.
 * Each item in the menu will - when clicked by a player - executes a ClickRunnable.
 */
public abstract class Menu implements Listener {

    /**
     * UltraCosmetcs Instance.
     */
    private UltraCosmetics ultraCosmetics;

    /**
     * Click Runnables maps.
     * <p>
     * Key: Item
     * Value: ClickRunnable to call when item is clicked.
     */
    private Map<ItemStack, ClickRunnable> clickRunnableMap = new HashMap<>();

    public Menu(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;

        ultraCosmetics.getServer().getPluginManager().registerEvents(this, ultraCosmetics);
    }

    public void open(UltraPlayer player) {
        Inventory inventory = Bukkit.createInventory(null, getSize(), getName());

        putItems(inventory, player);

        player.getPlayer().openInventory(inventory);
    }

    protected void putItem(Inventory inventory, int slot, ItemStack itemStack, ClickRunnable clickRunnable) {
        Validate.notNull(itemStack);
        Validate.notNull(clickRunnable);

        inventory.setItem(slot, itemStack);
        clickRunnableMap.put(itemStack, clickRunnable);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // Check Inventory isn't null
        if (event.getInventory() == null) {
            return;
        }

        // Check Item clicked isn't null
        if (event.getCurrentItem() == null) {
            return;
        }

        // Check clicker is player
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        // Check Inventory is the good one
        if (!event.getInventory().getName().contains(getName())) {
            return;
        }

        // Check that Item is meant to do an action.
        if (!clickRunnableMap.keySet().contains(event.getCurrentItem())) {
            return;
        }

        event.setCancelled(true);
        ClickRunnable clickRunnable = clickRunnableMap.get(event.getCurrentItem());

        // Check clickrunnable isn't null.
        if (clickRunnable == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        UltraPlayer ultraPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(player);
        clickRunnable.run(new ClickData(event.getInventory(), ultraPlayer, event.getAction(), event.getCurrentItem(), event.getSlot()));
    }

    public UltraCosmetics getUltraCosmetics() {
        return ultraCosmetics;
    }

    protected abstract void putItems(Inventory inventory, UltraPlayer player);

    protected abstract int getSize();

    protected abstract String getName();
}
