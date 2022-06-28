package be.isach.ultracosmetics.menu;

import static be.isach.ultracosmetics.util.ItemFactory.fillerItem;

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
import java.util.Map.Entry;
import java.util.Set;

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
    protected UltraCosmetics ultraCosmetics;

    /**
     * Click Runnables maps.
     * <p>
     * Key: Item
     * Value: ClickRunnable to call when item is clicked.
     */
    private Map<Inventory,Map<ItemStack,ClickRunnable>> clickRunnableMap = new HashMap<>();

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
        Map<ItemStack,ClickRunnable> map = clickRunnableMap.computeIfAbsent(inventory, f -> new HashMap<>());
        map.put(itemStack, clickRunnable);
    }

    protected void putItem(Inventory inventory, int slot, ItemStack itemStack) {
        putItem(inventory, slot, itemStack, data -> {
        });
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() == null) {
            return;
        }

        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        // Check Inventory is the good one.
        // Using contains because inventory titles get
        // page numbers when multiple pages exist.
        if (!event.getView().getTitle().startsWith(getName())) {
            return;
        }
        // Check that Inventory is valid.
        if (!clickRunnableMap.containsKey(event.getInventory())) {
            return;
        }

        // Check that the filler item isn't being clicked
        if (event.getCurrentItem().equals(fillerItem)) {
            event.setCancelled(true);
            return;
        }

        ClickRunnable clickRunnable = null;
        String clickItemName = event.getCurrentItem().getItemMeta().getDisplayName();
        Set<Entry<ItemStack,ClickRunnable>> entries = clickRunnableMap.get(event.getInventory()).entrySet();
        for (Entry<ItemStack,ClickRunnable> entry : entries) {
            if (entry.getKey().getItemMeta().getDisplayName().equals(clickItemName)) {
                clickRunnable = entry.getValue();
                break;
            }
        }
        if (clickRunnable == null) {
            return;
        }

        event.setCancelled(true);

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
