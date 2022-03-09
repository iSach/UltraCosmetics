package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.emotes.Emote;
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
    protected void filterItem(ItemStack itemStack, EmoteType cosmeticType, UltraPlayer player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        ItemMeta emoteMeta = cosmeticType.getFrames().get(cosmeticType.getMaxFrames() - 1).getItemMeta();
        emoteMeta.setDisplayName(itemMeta.getDisplayName());
        emoteMeta.setLore(itemMeta.getLore());
        itemStack.setItemMeta(emoteMeta);
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
    protected void toggleOff(UltraPlayer ultraPlayer, EmoteType type) {
        ultraPlayer.removeEmote();
    }

    @Override
    protected Emote getCosmetic(UltraPlayer ultraPlayer) {
        return ultraPlayer.getCurrentEmote();
    }
}
