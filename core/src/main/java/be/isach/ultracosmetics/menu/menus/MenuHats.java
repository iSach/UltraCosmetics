package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
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
 * @author iSach
 * @since 08-23-2016
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
    protected void filterItem(ItemStack itemStack, HatType cosmeticType, UltraPlayer player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        ItemMeta hatMeta = cosmeticType.getItemStack().getItemMeta();
        hatMeta.setDisplayName(itemMeta.getDisplayName());
        hatMeta.setLore(itemMeta.getLore());
        itemStack.setItemMeta(hatMeta);
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, HatType hatType, UltraCosmetics ultraCosmetics) {
        hatType.equip(ultraPlayer, ultraCosmetics);
    }
}
