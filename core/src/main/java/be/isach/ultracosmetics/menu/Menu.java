package be.isach.ultracosmetics.menu;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

import static be.isach.ultracosmetics.util.ItemFactory.fillerItem;

/**
 * Represents a Menu. A menu can have multiple pages in case of cosmetics.
 * Each item in the menu will, when clicked by a player, execute a ClickRunnable.
 *
 * @author iSach
 * @since 07-05-2016
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
    private Map<Inventory, Map<ItemStack, ClickRunnable>> clickRunnableMap = new HashMap<>();

    public Menu(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;

        ultraCosmetics.getServer().getPluginManager().registerEvents(this, ultraCosmetics);
    }

    public void open(UltraPlayer player) {
        player.getBukkitPlayer().openInventory(getInventory(player));
    }

    public Inventory getInventory(UltraPlayer player) {
        Inventory inventory = Bukkit.createInventory(new CosmeticsInventoryHolder(), getSize(), getName());
        putItems(inventory, player);
        ItemFactory.fillInventory(inventory);
        return inventory;
    }

    protected void putItem(Inventory inventory, int slot, ItemStack itemStack, ClickRunnable clickRunnable) {
        Validate.notNull(itemStack);
        Validate.notNull(clickRunnable);

        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.values());
            itemStack.setItemMeta(itemMeta);
        }

        inventory.setItem(slot, itemStack);
        Map<ItemStack, ClickRunnable> map = clickRunnableMap.computeIfAbsent(inventory, f -> new HashMap<>());
        map.put(itemStack, clickRunnable);
    }

    protected void putItem(Inventory inventory, int slot, ItemStack itemStack) {
        putItem(inventory, slot, itemStack, data -> {});
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() == null) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }


        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        // Check Inventory is the good one
        if (!event.getView().getTitle().contains(getName())) {
            return;
        }

        // Check that the filler item isn't being clicked
        if (fillerItem != null && event.getCurrentItem().equals(fillerItem)) {
            event.setCancelled(true);
            return;
        }
        // Check that Inventory is valid.
        if (!clickRunnableMap.containsKey(event.getInventory())) {
            return;
        }

        boolean correctItem = false;

        ClickRunnable clickRunnable = null;
        for (ItemStack itemStack : clickRunnableMap.get(event.getInventory()).keySet()) {
            if (ItemFactory.haveSameName(itemStack, event.getCurrentItem())) {
                correctItem = true;
                clickRunnable = clickRunnableMap.get(event.getInventory()).get(itemStack);
            }
        }
        if (!correctItem) {
            return;
        }

        event.setCancelled(true);

        if (clickRunnable == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        UltraPlayer ultraPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(player);
        clickRunnable.run(new ClickData(event.getInventory(), ultraPlayer, event.getClick(), event.getCurrentItem(), event.getSlot()));
        player.updateInventory();
    }

    public UltraCosmetics getUltraCosmetics() {
        return ultraCosmetics;
    }

    protected abstract void putItems(Inventory inventory, UltraPlayer player);

    protected abstract int getSize();

    protected abstract String getName();
}
