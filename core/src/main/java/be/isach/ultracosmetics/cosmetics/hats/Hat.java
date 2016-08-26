package be.isach.ultracosmetics.cosmetics.hats;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.HatType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.inventory.ItemStack;

/**
 * Package: be.isach.ultracosmetics.cosmetics.hats
 * Created by: sachalewin
 * Date: 23/08/16
 * Project: UltraCosmetics
 */
public class Hat extends Cosmetic<HatType> {

    public Hat(UltraCosmetics ultraCosmetics, UltraPlayer owner, HatType type) {
        super(ultraCosmetics, Category.HATS, owner, type);
    }

    @Override
    protected void onEquip() {

    }

    @Override
    protected void onClear() {

    }

    public ItemStack getItemStack() {
        return getType().getItemStack();
    }
}
