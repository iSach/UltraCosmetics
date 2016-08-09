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
 *
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
     *
     * Key: Item
     * Value: ClickRunnable to call when item is clicked.
     */
    private Map<ItemStack, ClickRunnable> clickRunnableMap = new HashMap<>();

    public Menu(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
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
        if (event.getInventory() == null) return;
        if (event.getCurrentItem() == null) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getInventory().getName().contains(getName())) return;
        if (!clickRunnableMap.keySet().contains(event.getCurrentItem())) return;
        ClickRunnable clickRunnable = clickRunnableMap.get(event.getCurrentItem());
        Player player = (Player) event.getWhoClicked();
        UltraPlayer ultraPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(player);
        clickRunnable.run(new ClickData(event.getInventory(), ultraPlayer, event.getAction(), event.getCurrentItem(), event.getSlot()));
        event.setCancelled(true);
    }

    public UltraCosmetics getUltraCosmetics() {
        return ultraCosmetics;
    }

    protected abstract void putItems(Inventory inventory, UltraPlayer player);

    abstract int getSize();

    abstract String getName();
}
