package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraPlayer;
import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Created by Sacha on 20/12/15.
 */
public abstract class Suit extends Cosmetic {

    /**
     * Armor Slot of the Suit.
     */
    private ArmorSlot armorSlot;

    /**
     * Type of the Suit.
     */
    private SuitType suitType;

    /**
     * ItemStack of the Suit.
     */
    protected ItemStack itemStack;

    public Suit(final UltraPlayer ultraPlayer, ArmorSlot armorSlot, SuitType suitType, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.SUITS, ultraPlayer);

        this.armorSlot = armorSlot;
        this.suitType = suitType;

        if (getCustomPlayer().currentHat != null
                && armorSlot == ArmorSlot.HELMET)
            getCustomPlayer().removeHat();

        getCustomPlayer().removeSuit(getArmorSlot());

        switch (getArmorSlot()) {
            case HELMET:
                if (getCustomPlayer().currentHat != null)
                    getCustomPlayer().removeHat();
                if(getCustomPlayer().currentEmote != null)
                    getCustomPlayer().removeEmote();
                if (getPlayer().getInventory().getHelmet() != null) {
                    ItemStack itemStack = getPlayer().getInventory().getHelmet();
                    drop(itemStack);
                    getPlayer().getInventory().setHelmet(null);
                }
                getPlayer().getInventory().setHelmet(ItemFactory.create(getType().getHelmet(), (byte) 0, getType().getName(getArmorSlot()), "", MessageManager.getMessage("Suits.Suit-Part-Lore")));
                itemStack = getPlayer().getInventory().getHelmet();
                getCustomPlayer().currentHelmet = this;
                break;
            case CHESTPLATE:
                if (getPlayer().getInventory().getChestplate() != null) {
                    ItemStack itemStack = getPlayer().getInventory().getChestplate();
                    drop(itemStack);
                    getPlayer().getInventory().setChestplate(null);
                }
                getPlayer().getInventory().setChestplate(ItemFactory.create(getType().getChestplate(), (byte) 0, getType().getName(getArmorSlot()), "", MessageManager.getMessage("Suits.Suit-Part-Lore")));
                itemStack = getPlayer().getInventory().getChestplate();
                getCustomPlayer().currentChestplate = this;
                break;
            case LEGGINGS:
                if (getPlayer().getInventory().getLeggings() != null) {
                    ItemStack itemStack = getPlayer().getInventory().getLeggings();
                    drop(itemStack);
                    getPlayer().getInventory().setLeggings(null);
                }
                getPlayer().getInventory().setLeggings(ItemFactory.create(getType().getLeggings(), (byte) 0, getType().getName(getArmorSlot()), "", MessageManager.getMessage("Suits.Suit-Part-Lore")));
                itemStack = getPlayer().getInventory().getLeggings();
                getCustomPlayer().currentLeggings = this;
                break;
            case BOOTS:
                if (getPlayer().getInventory().getBoots() != null) {
                    ItemStack itemStack = getPlayer().getInventory().getBoots();
                    drop(itemStack);
                    getPlayer().getInventory().setBoots(null);
                }
                getPlayer().getInventory().setBoots(ItemFactory.create(getType().getBoots(), (byte) 0, getType().getName(getArmorSlot()), "", MessageManager.getMessage("Suits.Suit-Part-Lore")));
                itemStack = getPlayer().getInventory().getBoots();
                getCustomPlayer().currentBoots = this;
                break;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getOwner() == null
                        || getPlayer() == null) {
                    cancel();
                    return;
                }
                onUpdate();
            }
        }.runTaskTimerAsynchronously(UltraCosmetics.getInstance(), 0, 1);

        getPlayer().sendMessage(MessageManager.getMessage("Suits.Equip").replace("%suitname%", (UltraCosmetics.getInstance().placeholdersHaveColor())
                ? getType().getName(getArmorSlot()) : UltraCosmetics.filterColor(getType().getName(getArmorSlot()))));
    }

    /**
     * Gets the UltraPlayer of the Owner.
     *
     * @return The UltraPlayer of the Owner.
     */
    public UltraPlayer getCustomPlayer() {
        return UltraCosmetics.getPlayerManager().getCustomPlayer(getPlayer());
    }

    /**
     * Clears the Suit.
     */
    public void clear() {
        switch (getArmorSlot()) {
            case HELMET:
                if (getCustomPlayer().currentHat != null)
                    getCustomPlayer().removeHat();
                getPlayer().getInventory().setHelmet(null);
                getCustomPlayer().currentHelmet = null;
                break;
            case CHESTPLATE:
                getPlayer().getInventory().setChestplate(null);
                getCustomPlayer().currentChestplate = null;
                break;
            case LEGGINGS:
                getPlayer().getInventory().setLeggings(null);
                getCustomPlayer().currentLeggings = null;
                break;
            case BOOTS:
                getPlayer().getInventory().setBoots(null);
                getCustomPlayer().currentBoots = null;
                break;
        }
        if (getPlayer() != null)
            getPlayer().sendMessage(MessageManager.getMessage("Suits.Unequip").replace("%suitname%", (UltraCosmetics.getInstance().placeholdersHaveColor())
                    ? getType().getName(getArmorSlot()) : UltraCosmetics.filterColor(getType().getName(getArmorSlot()))));
        armorSlot = null;
        suitType = null;
        itemStack = null;
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
     * Called each tick while suit active, async.
     */
    protected void onUpdate() {
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
     * Get Suit Type.
     *
     * @return Suit Type.
     */
    public SuitType getType() {
        return suitType;
    }

    /**
     * Drops an Item.
     *
     * @param itemStack The item to drop.
     */
    private void drop(ItemStack itemStack) {
        getPlayer().getWorld().dropItem(getPlayer().getLocation(), itemStack);
    }
}
