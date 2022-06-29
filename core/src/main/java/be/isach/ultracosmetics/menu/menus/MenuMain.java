package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.menu.Menu;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.treasurechests.TreasureChestManager;
import be.isach.ultracosmetics.treasurechests.TreasureRandomizer;
import be.isach.ultracosmetics.util.ItemFactory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

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
            layout = new int[] { 10, 12, 14, 16, 28, 30, 32, 34 };
            break;
        case 7:
            layout = new int[] { 10, 13, 16, 28, 30, 32, 34 };
            break;
        case 6:
            layout = new int[] { 10, 13, 16, 28, 31, 34 };
            break;
        case 5:
            layout = new int[] { 10, 16, 22, 29, 33 };
            break;
        case 4:
            layout = new int[] { 19, 21, 23, 25 };
            break;
        case 3:
            layout = new int[] { 20, 22, 24 };
            break;
        case 2:
            layout = new int[] { 21, 23 };
            break;
        case 1:
            layout = new int[] { 22 };
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
        String message = MessageManager.getMessage("Clear.Cosmetics");
        ItemStack itemStack = ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Clear-Cosmetic-Item"), message);
        putItem(inventory, inventory.getSize() - 5, itemStack, data -> {
            player.clear();
            open(player);
        });

        if (UltraCosmeticsData.get().areTreasureChestsEnabled()) {
            String msgChests = MessageManager.getMessage("Treasure-Chests");
            final boolean usingEconomy = getUltraCosmetics().getEconomyHandler().isUsingEconomy();
            boolean canBuyKeys = usingEconomy && SettingsManager.getConfig().getInt("TreasureChests.Key-Price") > 0;
            String buyKeyMessage = "";
            if (canBuyKeys) {
                buyKeyMessage = "\n" + MessageManager.getMessage("Click-Buy-Key") + "\n";
            }
            String[] chestLore;
            if (player.getKeys() == 0) {
                chestLore = new String[] { "", MessageManager.getMessage("Dont-Have-Key"), buyKeyMessage };
            } else {
                if (SettingsManager.getConfig().getString("TreasureChests.Mode", "").equalsIgnoreCase("both")) {
                    chestLore = new String[] { "", MessageManager.getMessage("Left-Click-Open-Chest"), MessageManager.getMessage("Right-Click-Simple"), "" };
                } else {
                    chestLore = new String[] { "", MessageManager.getMessage("Click-Open-Chest"), "" };
                }
            }
            ItemStack keys = ItemFactory.create(XMaterial.TRIPWIRE_HOOK, MessageManager.getMessage("Treasure-Keys"), "",
                    MessageManager.getMessage("Your-Keys").replace("%keys%", String.valueOf(player.getKeys())), buyKeyMessage);

            putItem(inventory, 5, keys, (data) -> {
                if (!canBuyKeys) {
                    XSound.BLOCK_ANVIL_LAND.play(player.getBukkitPlayer().getLocation(), 0.2f, 1.2f);
                    return;
                }
                player.getBukkitPlayer().closeInventory();
                player.openKeyPurchaseMenu();
            });

            ItemStack chest = ItemFactory.create(XMaterial.CHEST, msgChests, chestLore);
            putItem(inventory, 3, chest, (data) -> {
                if (!canBuyKeys && player.getKeys() == 0) {
                    XSound.BLOCK_ANVIL_LAND.play(player.getBukkitPlayer().getLocation(), 0.2f, 1.2f);
                    return;
                }
                String mode = SettingsManager.getConfig().getString("TreasureChests.Mode", "structure");
                if (mode.equalsIgnoreCase("both")) {
                    if (data.getClick().isRightClick()) {
                        mode = "simple";
                    } else {
                        mode = "structure";
                    }
                }
                if (mode.equalsIgnoreCase("simple")) {
                    player.removeKey();
                    int count = SettingsManager.getConfig().getInt("TreasureChests.Count", 4);
                    TreasureRandomizer tr = new TreasureRandomizer(player.getBukkitPlayer(), player.getBukkitPlayer().getLocation(), true);
                    for (int i = 0; i < count; i++) {
                        tr.giveRandomThing();
                    }
                    // Refresh with new key count
                    open(player);
                } else {
                    TreasureChestManager.tryOpenChest(player.getBukkitPlayer());
                }
            });

        }
    }

    @Override
    protected String getName() {
        return MessageManager.getMessage("Menu.Main.Title");
    }

    @Override
    protected int getSize() {
        return UltraCosmeticsData.get().areTreasureChestsEnabled() ? 54 : 45;
    }

    /**
     * Opens UC's main menu OR runs the custom main menu command specified in config.yml
     *
     * @param ultraPlayer The player to show the menu to
     */
    public static void openMainMenu(UltraPlayer ultraPlayer) {
        UltraCosmetics ultraCosmetics = UltraCosmeticsData.get().getPlugin();
        if (ultraCosmetics.getConfig().getBoolean("Categories.Back-To-Main-Menu-Custom-Command.Enabled")) {
            String command = ultraCosmetics.getConfig().getString("Categories.Back-To-Main-Menu-Custom-Command.Command").replace("/", "").replace("{player}", ultraPlayer.getBukkitPlayer().getName()).replace("{playeruuid}", ultraPlayer.getUUID().toString());
            Bukkit.dispatchCommand(ultraCosmetics.getServer().getConsoleSender(), command);
        } else {
            ultraCosmetics.getMenus().getMainMenu().open(ultraPlayer);
        }
    }
}
