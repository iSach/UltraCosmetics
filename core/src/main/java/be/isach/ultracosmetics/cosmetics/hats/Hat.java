package be.isach.ultracosmetics.cosmetics.hats;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
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
        getOwner().removeHat();
        getOwner().removeEmote();
        getOwner().removeSuit(ArmorSlot.HELMET);

        if (getPlayer().getInventory().getHelmet() != null) {
            getPlayer().sendMessage(MessageManager.getMessage("Hats.Must-Remove-Hat"));
            return;
        }

        getPlayer().getInventory().setHelmet(getType().getItemStack());

        getOwner().setCurrentHat(this);
    }

    @Override
    protected void onClear() {
        getPlayer().getInventory().setHelmet(null);
        getOwner().setCurrentHat(null);
    }

    public ItemStack getItemStack() {
        return getType().getItemStack();
    }
}
