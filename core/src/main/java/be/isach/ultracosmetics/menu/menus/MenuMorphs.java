package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.morphs.Morph;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.menu.ClickRunnable;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Morph {@link be.isach.ultracosmetics.menu.Menu Menu}.
 *
 * @author iSach
 * @since 08-23-2016
 */
public class MenuMorphs extends CosmeticMenu<MorphType> {

    public MenuMorphs(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.MORPHS);
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer player, int page) {
        putSelfViewItem(inventory, player);
    }

    private void putSelfViewItem(Inventory inventory, UltraPlayer player) {
        int slot = inventory.getSize() - (getCategory().hasGoBackArrow() ? 5 : 6);
        ItemStack selfViewStack;
        boolean toggle;
        if (player.canSeeSelfMorph()) {
            selfViewStack = ItemFactory.getItemStackFromConfig("Categories.Self-View-Item.When-Enabled");
            toggle = false;
        } else {
            selfViewStack = ItemFactory.getItemStackFromConfig("Categories.Self-View-Item.When-Disabled");
            toggle = true;
        }
        String msg = MessageManager.getMessage((toggle ? "Enable" : "Disable") + "-Third-Person-View");
        ClickRunnable run = data -> {
            player.setSeeSelfMorph(!player.canSeeSelfMorph());
            putSelfViewItem(inventory, player);
        };
        putItem(inventory, slot, ItemFactory.rename(selfViewStack, msg), run);
    }

    @Override
    protected void filterItem(ItemStack itemStack, MorphType cosmeticType, UltraPlayer player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        lore.add("");
        lore.add(cosmeticType.getSkill());
        lore.add("");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    @Override
    public List<MorphType> enabled() {
        return MorphType.enabled();
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, MorphType morphType, UltraCosmetics ultraCosmetics) {
        morphType.equip(ultraPlayer, ultraCosmetics);
    }

    @Override
    protected void toggleOff(UltraPlayer ultraPlayer) {
        ultraPlayer.removeMorph();
    }

    @Override
    protected Morph getCosmetic(UltraPlayer ultraPlayer) {
        return ultraPlayer.getCurrentMorph();
    }
}
