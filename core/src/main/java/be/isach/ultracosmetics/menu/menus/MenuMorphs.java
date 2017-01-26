package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.player.UltraPlayer;

import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Morph {@link be.isach.ultracosmetics.menu.Menu Menu}.
 * 
 * @author 	iSach
 * @since 	08-23-2016
 */
public class MenuMorphs extends CosmeticMenu<MorphType> {

    public MenuMorphs(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.MORPHS);
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer ultraPlayer, int page) {
    }

    @Override
    public List<MorphType> enabled() {
        return MorphType.enabled();
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, String name, UltraCosmetics ultraCosmetics) {
    	MorphType.getByName(name.trim()).equip(ultraPlayer, ultraCosmetics);
    }

    @Override
    protected void toggleOff(UltraPlayer ultraPlayer) {
    	ultraPlayer.removeMorph();
    }

    @Override
    protected Cosmetic getCosmetic(UltraPlayer ultraPlayer) {
        return ultraPlayer.getCurrentMorph();
    }
}
