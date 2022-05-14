package be.isach.ultracosmetics.cosmetics.hats;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.ArmorCosmetic;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.HatType;
import be.isach.ultracosmetics.player.UltraPlayer;

/**
 * Represents an instance of a hat summoned by a player.
 *
 * @author iSach
 * @since 08-23-2016
 */
public class Hat extends ArmorCosmetic<HatType> {

    public Hat(UltraCosmetics ultraCosmetics, UltraPlayer owner, HatType type) {
        super(ultraCosmetics, Category.HATS, owner, type);
        itemStack = type.getItemStack();
    }

    @Override
    protected void onEquip() {
        // Setting the item is done in ArmorCosmetic#tryEquip
    }

    @Override
    protected ArmorSlot getArmorSlot() {
        return ArmorSlot.HELMET;
    }

    @Override
    protected String getOccupiedSlotKey() {
        return "Hats.Must-Remove-Hat";
    }
}
