package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.menu.Menu;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.inventory.Inventory;

/**
 * Package: be.isach.ultracosmetics.menu.menus
 * Created by: sachalewin
 * Date: 23/08/16
 * Project: UltraCosmetics
 */
public class MenuMain extends Menu {

    private int[] layout;

    public MenuMain(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics);

        switch (Category.enabledSize()) {
            case 8:
                layout = new int[]{10, 12, 14, 16, 28, 30, 32, 34};
                break;
            case 7:
                layout = new int[]{10, 13, 16, 28, 30, 32, 34};
                break;
            case 6:
                layout = new int[]{10, 13, 16, 28, 31, 34};
                break;
            case 5:
                layout = new int[]{10, 16, 22, 29, 33};
                break;
            case 4:
                layout = new int[]{19, 21, 23, 25};
                break;
            case 3:
                layout = new int[]{20, 22, 24};
                break;
            case 2:
                layout = new int[]{21, 23};
                break;
            case 1:
                layout = new int[]{22};
                break;
        }

        if (UltraCosmeticsData.get().areTreasureChestsEnabled()) {
            for (int i = 0; i < layout.length; i++) {
                layout[i] += 9;
            }
        }
    }

    @Override
    public void open(UltraPlayer player) {
        if (!UltraCosmeticsData.get().areTreasureChestsEnabled()
                && Category.enabledSize() == 1) {
            Category.enabled().get(0).getMenu(getUltraCosmetics()).open(player);
            return;
        }
        super.open(player);
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer player) {
        for (int i = 0; i < Category.enabledSize(); i++) {
            int slot = layout[i];
            Category category = Category.enabled().get(i);
            putItem(inventory, slot, category.getItemStack(), data -> category.getMenu(getUltraCosmetics()).open(player));
        }
    }

    @Override
    protected String getName() {
        return MessageManager.getMessage("Menus.Main-Menu");
    }

    @Override
    protected int getSize() {
        return UltraCosmeticsData.get().areTreasureChestsEnabled() ? 54 : 45;
    }
}
