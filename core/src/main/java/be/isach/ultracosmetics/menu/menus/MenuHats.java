package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.HatType;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Package: be.isach.ultracosmetics.menu.menus
 * Created by: sachalewin
 * Date: 23/08/16
 * Project: UltraCosmetics
 */
public class MenuHats extends CosmeticMenu<HatType> {

    public MenuHats(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.HATS);
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer ultraPlayer, int page) {

    }

    @Override
    public List<HatType> enabled() {
        return HatType.enabled();
    }

    @Override
    protected ItemStack filterItem(ItemStack itemStack, HatType cosmeticType, UltraPlayer player) {
        ItemMeta itemMeta = itemStack.getItemMeta().clone();
        itemStack = cosmeticType.getItemStack();
        ItemMeta other = itemStack.getItemMeta().clone();
        other.setDisplayName(itemMeta.getDisplayName());
        other.setLore(itemMeta.getLore());
        itemStack.setItemMeta(other);
        return itemStack;
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, String name, UltraCosmetics ultraCosmetics) {

    }

    @Override
    protected void toggleOff(UltraPlayer ultraPlayer) {

    }

    @Override
    protected Cosmetic getCosmetic(UltraPlayer ultraPlayer) {
        return ultraPlayer.getCurrentHat();
    }
}
