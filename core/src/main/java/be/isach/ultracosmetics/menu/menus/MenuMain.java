package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.menu.Menu;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;
import be.isach.ultracosmetics.util.XMaterial;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static be.isach.ultracosmetics.manager.TreasureChestManager.tryOpenChest;

/**
 * Main {@link be.isach.ultracosmetics.menu.Menu Menu}.
 *
 * @author iSach
 * @since 08-23-2016
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

        if (UltraCosmeticsData.get().areTreasureChestsEnabled() && layout != null) {
            for (int i = 0; i < layout.length; i++) {
                layout[i] += 9;
            }
        }
    }

    @Override
    public void open(UltraPlayer player) {
        if (!UltraCosmeticsData.get().areTreasureChestsEnabled()
                && Category.enabledSize() == 1) {
            Category.enabled().get(0).getMenu(getUltraCosmetics().getMenus()).open(player);
            return;
        }
        super.open(player);
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer player) {
        if (Category.enabledSize() > 0) {
            for (int i = 0; i < Category.enabledSize(); i++) {
                int slot = layout[i];
                Category category = Category.enabled().get(i);
                putItem(inventory, slot, category.getItemStack(), data -> {
                    category.getMenu(getUltraCosmetics().getMenus()).open(player);
                });
            }
        }

        // Clear cosmetics item.
        String message = MessageManager.getMessage("Clear-Cosmetics");
        ItemStack itemStack = ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Clear-Cosmetic-Item"), message);
        putItem(inventory, inventory.getSize() - 5, itemStack, data -> {
            player.clear();
            open(player);
        });

        if (UltraCosmeticsData.get().areTreasureChestsEnabled()) {
            ItemStack chest;

            String msgChests = MessageManager.getMessage("Treasure-Chests");
            if (player.getKeys() == 0) {
                chest = ItemFactory.create(XMaterial.CHEST, msgChests, "", MessageManager.getMessage("Dont-Have-Key"), getUltraCosmetics().getEconomyHandler().isUsingEconomy() ?
                        "" : null, getUltraCosmetics().getEconomyHandler().isUsingEconomy() ? MessageManager.getMessage("Click-Buy-Key") : null, getUltraCosmetics().getEconomyHandler().isUsingEconomy() ? "" : null);
            } else {
                chest = ItemFactory.create(XMaterial.CHEST, msgChests, "", MessageManager.getMessage("Click-Open-Chest"), "");
            }
            ItemStack keys = ItemFactory.create(XMaterial.TRIPWIRE_HOOK, MessageManager.getMessage("Treasure-Keys"), "",
                    MessageManager.getMessage("Your-Keys").replace("%keys%", player.getKeys() + ""), getUltraCosmetics().getEconomyHandler().isUsingEconomy() ?
                            "" : null, getUltraCosmetics().getEconomyHandler().isUsingEconomy() ? MessageManager.getMessage("Click-Buy-Key") : null, getUltraCosmetics().getEconomyHandler().isUsingEconomy() ? "" : null);

            putItem(inventory, 5, keys, (data) -> {
                if (!getUltraCosmetics().getEconomyHandler().isUsingEconomy() && player.getKeys() == 0) {
                    SoundUtil.playSound(player.getBukkitPlayer().getLocation(), Sounds.ANVIL_LAND, 0.2f, 1.2f);
                    return;
                }
                player.getBukkitPlayer().closeInventory();
                player.openKeyPurchaseMenu();
            });

            putItem(inventory, 3, chest, (data) -> {
                if (!getUltraCosmetics().getEconomyHandler().isUsingEconomy() && player.getKeys() == 0) {
                    SoundUtil.playSound(player.getBukkitPlayer().getLocation(), Sounds.ANVIL_LAND, 0.2f, 1.2f);
                    return;
                }
                tryOpenChest(player.getBukkitPlayer());
            });

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
