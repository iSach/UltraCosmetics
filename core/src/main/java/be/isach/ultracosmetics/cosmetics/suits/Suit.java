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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
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
     * Armor Slot of the Suit.
     */
    private ArmorSlot armorSlot;

    /**
     * ItemStack of the Suit.
     */
    protected ItemStack itemStack;

    public Suit(UltraPlayer ultraPlayer, ArmorSlot armorSlot, SuitType suitType, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.SUITS, ultraPlayer, suitType);
        this.armorSlot = armorSlot;
        Bukkit.getPluginManager().registerEvents(this, ultraCosmetics);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack drop = event.getItemDrop().getItemStack();
        if (event.getPlayer().equals(getPlayer()) && drop.hasItemMeta() && drop.getItemMeta().hasDisplayName() && drop.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName())) {
            event.getItemDrop().remove();
            if (SettingsManager.getConfig().getBoolean("Remove-Gadget-With-Drop")) {
                clear();
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        if (getPlayer() != null && player.equals(getPlayer()) && current != null && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && current.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName())) {
            event.setCancelled(true);
            if (event.getAction().name().contains("DROP") && SettingsManager.getConfig().getBoolean("Remove-Gadget-With-Drop")) {
                clear();
            }
            player.updateInventory();
        }
    }

    @Override
    protected void onEquip() {
        if (getOwner().getCurrentHat() != null
                && armorSlot == ArmorSlot.HELMET) {
            getOwner().removeHat();
        }

        getOwner().removeSuit(getArmorSlot());

        switch (getArmorSlot()) {
            case HELMET:
                if (getOwner().getCurrentHat() != null) {
                    getOwner().removeHat();
                }
                if (getOwner().getCurrentEmote() != null) {
                    getOwner().removeEmote();
                }
                if (getPlayer().getInventory().getHelmet() != null) {
                    ItemStack itemStack = getPlayer().getInventory().getHelmet();
                    drop(itemStack);
                    getPlayer().getInventory().setHelmet(null);
                }
                getPlayer().getInventory().setHelmet(ItemFactory.create(getType().getHelmet(), (byte) 0, getType().getName(getArmorSlot()), "", MessageManager.getMessage("Suits.Suit-Part-Lore")));
                itemStack = getPlayer().getInventory().getHelmet();
                break;
            case CHESTPLATE:
                if (getPlayer().getInventory().getChestplate() != null) {
                    ItemStack itemStack = getPlayer().getInventory().getChestplate();
                    drop(itemStack);
                    getPlayer().getInventory().setChestplate(null);
                }
                getPlayer().getInventory().setChestplate(ItemFactory.create(getType().getChestplate(), (byte) 0, getType().getName(getArmorSlot()), "", MessageManager.getMessage("Suits.Suit-Part-Lore")));
                itemStack = getPlayer().getInventory().getChestplate();
                break;
            case LEGGINGS:
                if (getPlayer().getInventory().getLeggings() != null) {
                    ItemStack itemStack = getPlayer().getInventory().getLeggings();
                    drop(itemStack);
                    getPlayer().getInventory().setLeggings(null);
                }
                getPlayer().getInventory().setLeggings(ItemFactory.create(getType().getLeggings(), (byte) 0, getType().getName(getArmorSlot()), "", MessageManager.getMessage("Suits.Suit-Part-Lore")));
                itemStack = getPlayer().getInventory().getLeggings();
                break;
            case BOOTS:
                if (getPlayer().getInventory().getBoots() != null) {
                    ItemStack itemStack = getPlayer().getInventory().getBoots();
                    drop(itemStack);
                    getPlayer().getInventory().setBoots(null);
                }
                getPlayer().getInventory().setBoots(ItemFactory.create(getType().getBoots(), (byte) 0, getType().getName(getArmorSlot()), "", MessageManager.getMessage("Suits.Suit-Part-Lore")));
                itemStack = getPlayer().getInventory().getBoots();
                break;
        }

        getOwner().setSuit(armorSlot, this);
        runTaskTimerAsynchronously(getUltraCosmetics(), 0, 1);
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
    public void onClear() {
        switch (getArmorSlot()) {
            case HELMET:
                if (getOwner().getCurrentHat() != null) {
                    getOwner().removeHat();
                }

                if (getOwner().getCurrentEmote() != null) {
                    getOwner().removeEmote();
                }

                getPlayer().getInventory().setHelmet(null);
                break;
            case CHESTPLATE:
                getPlayer().getInventory().setChestplate(null);
                break;
            case LEGGINGS:
                getPlayer().getInventory().setLeggings(null);
                break;
            case BOOTS:
                getPlayer().getInventory().setBoots(null);
                break;
        }
        getOwner().setSuit(getArmorSlot(), null);
        HandlerList.unregisterAll(this);
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
        return armorSlot;
    }

    /**
     * Drops an Item.
     *
     * @param itemStack The item to drop.
     */
    private void drop(ItemStack itemStack) {
        getPlayer().getWorld().dropItem(getPlayer().getLocation(), itemStack);
    }

    @Override
    protected String getTypeName() {
        return getType().getName(getArmorSlot());
    }
}