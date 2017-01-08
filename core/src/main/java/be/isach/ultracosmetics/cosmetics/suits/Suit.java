package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.TextUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Package: be.isach.ultracosmetics.cosmetics.suits
 * Created by: Sacha
 * Date: 20/12/15
 * Project: UltraCosmetics
 */
public abstract class Suit extends Cosmetic<SuitType> {

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

    public Suit(UltraPlayer ultraPlayer, ArmorSlot armorSlot, SuitType suitType, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.SUITS, ultraPlayer, suitType);

        this.armorSlot = armorSlot;
        this.suitType = suitType;

        if (getOwner().getCurrentHat() != null
                && armorSlot == ArmorSlot.HELMET)
            getOwner().removeHat();

        getOwner().removeSuit(getArmorSlot());

        switch (getArmorSlot()) {
            case HELMET:
                if (getOwner().getCurrentHat() != null)
                    getOwner().removeHat();
                if (getOwner().getCurrentEmote() != null)
                    getOwner().removeEmote();
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
    }

    @Override
    protected void onEquip() {
        runTaskTimerAsynchronously(UltraCosmeticsData.get().getPlugin(), 0, 1);
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
     * Drops an Item.
     *
     * @param itemStack The item to drop.
     */
    private void drop(ItemStack itemStack) {
        getPlayer().getWorld().dropItem(getPlayer().getLocation(), itemStack);
    }
}