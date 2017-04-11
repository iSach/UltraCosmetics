package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.EmoteType;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Emote {@link be.isach.ultracosmetics.menu.Menu Menu}.
 *
 * @author iSach
 * @since 08-23-2016
 */
public class MenuEmotes extends CosmeticMenu<EmoteType> {

    public MenuEmotes(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.EMOTES);
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer ultraPlayer, int page) {
    }

    @Override
    protected ItemStack filterItem(ItemStack itemStack, EmoteType cosmeticType, UltraPlayer player) {
        ItemMeta itemMeta = itemStack.getItemMeta().clone();
        itemStack = cosmeticType.getFrames().get(cosmeticType.getMaxFrames() - 1).clone();
        ItemMeta other = itemStack.getItemMeta().clone();
        other.setDisplayName(itemMeta.getDisplayName());
        other.setLore(itemMeta.getLore());
        itemStack.setItemMeta(other);
        return itemStack;
    }

    @Override
    public List<EmoteType> enabled() {
        return EmoteType.enabled();
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, EmoteType emoteType, UltraCosmetics ultraCosmetics) {
        emoteType.equip(ultraPlayer, ultraCosmetics);
    }

    @Override
    protected void toggleOff(UltraPlayer ultraPlayer) {
        ultraPlayer.removeEmote();
    }

    @Override
    protected Cosmetic getCosmetic(UltraPlayer ultraPlayer) {
        return ultraPlayer.getCurrentEmote();
    }
}
