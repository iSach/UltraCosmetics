package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an instance of a suit summoned by a player.
 *
 * @author iSach
 * @since 12-20-2015
 */
public abstract class Suit extends Cosmetic<SuitType> implements Updatable {
    /**
     * ItemStack of the Suit.
     */
    protected ItemStack itemStack;

    public Suit(UltraPlayer ultraPlayer, SuitType suitType, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.SUITS, ultraPlayer, suitType);
        Bukkit.getPluginManager().registerEvents(this, ultraCosmetics);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (getOwner() == null || getPlayer() == null) {
            return;
        } 
        if (event.getPlayer() == getPlayer() && isItemThis(event.getItemDrop().getItemStack())) {
            event.getItemDrop().remove();
            if (SettingsManager.getConfig().getBoolean("Remove-Gadget-With-Drop")) {
                clear();
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        handleClick(event);
    }

    // InventoryCreativeEvent is a subclass of InventoryClickEvent,
    // so do we really need both listeners?
    @EventHandler
    public void onInventoryClick(InventoryCreativeEvent event) {
        handleClick(event);
    }

    private void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        if (event.getSlotType().equals(SlotType.ARMOR) && player == getPlayer() && isItemThis(current)) {
            event.setCancelled(true);
            if (event instanceof InventoryCreativeEvent) {
                // Close the inventory because clicking again results in the event being handled client side
                player.closeInventory();
            } else {
                player.updateInventory();
            }
        }
    }

    private boolean isItemThis(ItemStack is) {
        return is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()
                && is.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName());
    }

    public void equip(ArmorSlot slot) {
        if (!getOwner().getBukkitPlayer().hasPermission(getType().getPermission())) {
            getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
            return;
        }

        // Remove current equipped armor piece
        getOwner().removeSuit(getArmorSlot());

        if (getArmorSlot() == ArmorSlot.HELMET) {
            getOwner().removeHat();
            getOwner().removeEmote();
        }

        // If the user's armor slot is still occupied after we've removed all related cosmetics,
        // give up and ask the user to free up the slot.
        if (getArmorItem() != null) {
            getOwner().sendMessage(MessageManager.getMessage("Suits.Must-Remove." + getArmorSlot().toString()));
            return;
        }

        getUltraCosmetics().getServer().getPluginManager().registerEvents(this, getUltraCosmetics());

        this.equipped = true;

        String mess = MessageManager.getMessage(getCategory().getConfigPath() + "." + getCategory().getActivateConfig());
        mess = mess.replace(getCategory().getChatPlaceholder(), TextUtil.filterPlaceHolder(getTypeName(), getUltraCosmetics()));
        getPlayer().sendMessage(mess);

        onEquip();
    }

    @Override
    protected void onEquip() {
        setupItemStack();
        setArmorItem(itemStack);

        getOwner().setCurrentSuitPart(cosmeticType.getSlot(), this);
        runTaskTimerAsynchronously(getUltraCosmetics(), 0, 1);
    }

    protected void setupItemStack() {
        itemStack = ItemFactory.create(getType().getMaterial(), getType().getName(), "", MessageManager.getMessage("Suits.Suit-Part-Lore"));
    }

    @Override
    public void run() {
        if (getOwner() == null || getPlayer() == null) {
            cancel();
            return;
        }
        onUpdate();
    }

    /**
     * Clears the Suit.
     */
    @Override
    public void onClear() {
        setArmorItem(null);
        getOwner().setCurrentSuitPart(getArmorSlot(), null);
    }

    /**
     * The Suit ItemStack.
     *
     * @return The Suit ItemStack.
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Get Suit Armor Slot.
     *
     * @return Suit Armor Slot.
     */
    public ArmorSlot getArmorSlot() {
        return cosmeticType.getSlot();
    }

    @Override
    public void onUpdate() {
    }

    protected ItemStack getArmorItem() {
        switch (getArmorSlot()) {
        case BOOTS:
            return getPlayer().getInventory().getBoots();
        case LEGGINGS:
            return getPlayer().getInventory().getLeggings();
        case CHESTPLATE:
            return getPlayer().getInventory().getChestplate();
        case HELMET:
            return getPlayer().getInventory().getHelmet();
        default:
            return null;
        }
    }

    protected void setArmorItem(ItemStack item) {
        switch (getArmorSlot()) {
        case BOOTS:
            getPlayer().getInventory().setBoots(item);
            break;
        case LEGGINGS:
            getPlayer().getInventory().setLeggings(item);
            break;
        case CHESTPLATE:
            getPlayer().getInventory().setChestplate(item);
            break;
        case HELMET:
            getPlayer().getInventory().setHelmet(item);
            break;
        }
    }
}