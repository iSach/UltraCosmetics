package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.ArmorCosmetic;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;

/**
 * Represents an instance of a suit summoned by a player.
 *
 * @author iSach
 * @since 12-20-2015
 */
public abstract class Suit extends ArmorCosmetic<SuitType> {

    public Suit(UltraPlayer ultraPlayer, SuitType suitType, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.SUITS, ultraPlayer, suitType);
        setupItemStack();
    }

    @Override
    protected void scheduleTask() {
        runTaskTimerAsynchronously(getUltraCosmetics(), 0, 1);
    }

    @Override
    public void run() {
        if (getPlayer() == null || getOwner().getSuit(getArmorSlot()) != this) {
            return;
        }
        ((Updatable)this).onUpdate();
    }

    protected void setupItemStack() {
        itemStack = ItemFactory.create(getType().getMaterial(), getType().getName(), "", MessageManager.getMessage("Suits.Suit-Part-Lore"));
    }

    @Override
    protected void unsetCosmetic() {
        getOwner().setCurrentSuitPart(getArmorSlot(), null);
    }

    @Override
    protected void unequipLikeCosmetics() {
        getOwner().removeSuit(getArmorSlot());
    }

    @Override
    protected void onEquip() {
    }

    /**
     * Get Suit Armor Slot.
     *
     * @return Suit Armor Slot.
     */
    @Override
    public ArmorSlot getArmorSlot() {
        return cosmeticType.getSlot();
    }

    @Override
    public String getOccupiedSlotKey() {
        return "Suits.Must-Remove." + getArmorSlot().toString();
    }
}
