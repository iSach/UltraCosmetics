package be.isach.ultracosmetics.menu;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Package: be.isach.ultracosmetics.menu
 * Created by: sachalewin
 * Date: 9/08/16
 * Project: UltraCosmetics
 */
public abstract class CosmeticMenu<T extends CosmeticType> extends Menu {

    public final static int[] COSMETICS_SLOTS =
            {
                    10, 11, 12, 13, 14, 15, 16,
                    19, 20, 21, 22, 23, 24, 25,
                    28, 29, 30, 31, 32, 33, 34
            };



    private Category category;

    public CosmeticMenu(UltraCosmetics ultraCosmetics, Category category) {
        super(ultraCosmetics);
        this.category = category;
    }

    public void open(UltraPlayer player, int page) {
        Inventory inventory = Bukkit.createInventory(null, getSize(), getName());

        putItems(inventory, player, page);

        player.getPlayer().openInventory(inventory);
    }

    public T getCosmeticType(String name) {
        for (T effectType : enabled()) {
            if (effectType.getConfigName().replace(" ", "").equals(name.replace(" ", "")))
                return effectType;
        }
        return null;
    }

    @Override
    int getSize() {
        int listSize = enabled().size();
        int slotAmount = 54;
        if (listSize < 22) {
            slotAmount = 54;
        }
        if (listSize < 15) {
            slotAmount = 45;
        }
        if (listSize < 8) {
            slotAmount = 36;
        }
        return slotAmount;
    }

    protected abstract void putItems(Inventory inventory, UltraPlayer ultraPlayer, int page);

    @Override
    protected void putItems(Inventory inventory, UltraPlayer ultraPlayer) {
        //--
    }

    public abstract List<T> enabled();

    protected int getCurrentPage(UltraPlayer ultraPlayer) {
        Player player = ultraPlayer.getPlayer();
        if (player.getOpenInventory() != null
                && player.getOpenInventory().getTopInventory().getTitle()
                .startsWith(MessageManager.getMessage("Menus." + category.getConfigPath()))) {
            String s = player.getOpenInventory().getTopInventory().getTitle()
                    .replace(MessageManager.getMessage("Menus." + category.getConfigPath()) + " §7§o(", "")
                    .replace("/" + getMaxPagesAmount() + ")", "");
            return Integer.parseInt(s);
        }
        return 0;
    }

    /**
     * Gets the max amount of pages.
     *
     * @return the maximum amount of pages.
     */
    protected final int getMaxPagesAmount() {
        int max = 21;
        int i = enabled().size();
        if (i % max == 0) return i / max;
        double j = i / 21;
        int h = (int) Math.floor(j * 100) / 100;
        return h + 1;
    }

    @Override
    String getName() {
        return MessageManager.getMessage("Menus." + category.getConfigPath());
    }

    protected String getName(int page) {
        return MessageManager.getMessage("Menus." + category.getConfigPath()) + " §7§o(" + page + "/" + getMaxPagesAmount() + ")";
    }
}
