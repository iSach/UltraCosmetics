package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.EmoteType;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Package: be.isach.ultracosmetics.menu.menus
 * Created by: sachalewin
 * Date: 23/08/16
 * Project: UltraCosmetics
 */
public class MenuSuits extends CosmeticMenu<SuitType> {

    public MenuSuits(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.SUITS);
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer ultraPlayer, int page) {

    }

    @Override
    public List<SuitType> enabled() {
        return SuitType.enabled();
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, String name, UltraCosmetics ultraCosmetics) {

    }

    @Override
    protected void toggleOff(UltraPlayer ultraPlayer) {

    }

    @Override
    protected Cosmetic getCosmetic(UltraPlayer ultraPlayer) {
        return ultraPlayer.getCurrentHelmet();
    }
}
