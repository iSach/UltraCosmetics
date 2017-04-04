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
 * Hat {@link be.isach.ultracosmetics.menu.Menu Menu}.
 * 
 * @author 	iSach
 * @since 	08-23-2016
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
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemStack = cosmeticType.getItemStack().clone();
        ItemMeta other = itemStack.getItemMeta().clone();
        other.setDisplayName(itemMeta.getDisplayName());
        other.setLore(itemMeta.getLore());
        itemStack.setItemMeta(other);
        return itemStack;
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, HatType hatType, UltraCosmetics ultraCosmetics) {
        hatType.equip(ultraPlayer, ultraCosmetics);
    }

    @Override
    protected void toggleOff(UltraPlayer ultraPlayer) {
        ultraPlayer.removeHat();
    }

    @Override
    protected Cosmetic getCosmetic(UltraPlayer ultraPlayer) {
        return ultraPlayer.getCurrentHat();
    }
}
