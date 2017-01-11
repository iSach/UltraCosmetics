package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.suits.Suit;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

/**
 * Package: be.isach.ultracosmetics.menu.menus
 * Created by: sachalewin
 * Date: 23/08/16
 * Project: UltraCosmetics
 */
public final class MenuSuits extends CosmeticMenu<SuitType> {

    private static final int[] SLOTS = new int[]{10, 11, 12, 13, 14, 15, 16, 17};
    private static final Category CATEGORY = Category.SUITS;

    public MenuSuits(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, CATEGORY);
    }

    @Override
    public void open(UltraPlayer player, int page) {
        if (page > getMaxPages()) {
            page = getMaxPages();
        }

        if (page < 1) {
            page = 1;
        }

        Inventory inventory = Bukkit.createInventory(null, getSize(), getMaxPages() == 1 ? getName() : getName(page));

        // Cosmetic items.
        int i = 0;
        int from = (page - 1) * 7; // 0->6 7->13 14->20
        int to = page * 7 - 1;
        superLoop:
        for (int h = from; h <= to; h++) {
            if (h >= enabled().size()) {
                break;
            }

            SuitType suitType = enabled().get(h);

            if (!suitType.isEnabled()) {
                continue;
            }

            if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item")
                    && !player.hasPermission(suitType.getPermission())) {
                continue;
            }

            //slotLoop:
            for (int l = 0; l < 4; l++) {

                ArmorSlot armorSlot = ArmorSlot.values()[l];
                Suit suit = player.getSuit(armorSlot);
                int slot = SLOTS[i] + l * 9;

                if (SettingsManager.getConfig().getBoolean("No-Permission.Custom-Item.enabled")
                        && !player.hasPermission(suitType.getPermission())) {
                    Material material = Material.valueOf((String) SettingsManager.getConfig().get("No-Permission.Custom-Item.Type"));
                    Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Data")));
                    String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name"));
                    name = ChatColor.translateAlternateColorCodes('&', name.replace("{cosmetic-name}", suitType.getName()));
                    List<String> npLore = SettingsManager.getConfig().getStringList("No-Permission.Custom-Item.Lore");
                    String[] array = new String[npLore.size()];
                    npLore.toArray(array);
                    putItem(inventory, COSMETICS_SLOTS[i], ItemFactory.create(material, data, name, array), null);
                    i++;
                    continue superLoop;
                }

                String toggle = (suit != null && suit.getType() == suitType) ? CATEGORY.getDeactivateMenu() : CATEGORY.getActivateMenu();
                ItemStack is = ItemFactory.create(suitType.getMaterial(armorSlot), suitType.getData(), toggle + " " + suitType.getName(armorSlot));

                if (suit != null && suit.getType() == suitType) {
                    is = ItemFactory.addGlow(is);
                }

                ItemMeta itemMeta = is.getItemMeta();
                List<String> loreList = new ArrayList<>();

                if (suitType.showsDescription()) {
                    loreList.add("");
                    loreList.addAll(suitType.getDescription());
                    loreList.add("");
                }

                if (SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore")) {
                    String yesOrNo = player.hasPermission(suitType.getPermission()) ? "Yes" : "No";
                    String s = SettingsManager.getConfig().getString("No-Permission.Lore-Message-" + yesOrNo);
                    loreList.add(ChatColor.translateAlternateColorCodes('&', s));
                }

                itemMeta.setLore(loreList);
                is.setItemMeta(itemMeta);
                is = filterItem(is, suitType, player);
                putItem(inventory, slot, is, (data) -> {
                    UltraPlayer ultraPlayer = data.getClicker();
                    ItemStack clicked = data.getClicked();
                    int currentPage = getCurrentPage(ultraPlayer);
                    if (UltraCosmeticsData.get().shouldCloseAfterSelect()) {
                        ultraPlayer.getBukkitPlayer().closeInventory();
                    }

                    if (clicked.getItemMeta().getDisplayName().startsWith(CATEGORY.getDeactivateMenu())) {
                        toggleOff(ultraPlayer);
                        if (!UltraCosmeticsData.get().shouldCloseAfterSelect()) {
                            open(ultraPlayer, currentPage);
                        }
                    } else if (clicked.getItemMeta().getDisplayName().startsWith(CATEGORY.getActivateMenu())) {
                        toggleOff(ultraPlayer);
                        StringBuilder sb = new StringBuilder();
                        String name = clicked.getItemMeta().getDisplayName().replaceFirst(CATEGORY.getActivateMenu(), "");
                        int j = name.split(" ").length;
                        if (name.contains("(")) {
                            j--;
                        }
                        for (int k = 1; k < j; k++) {
                            sb.append(name.split(" ")[k]);
                            try {
                                if (clicked.getItemMeta().getDisplayName().split(" ")[k + 1] != null) {
                                    sb.append(" ");
                                }
                            } catch (Exception ignored) {
                            }
                        }
                        toggleOn(ultraPlayer, sb.toString(), getUltraCosmetics());
                        if (ultraPlayer.getCurrentGadget() != null && UltraCosmeticsData.get().isAmmoEnabled() && ultraPlayer.getAmmo(ultraPlayer.getCurrentGadget().getType().toString().toLowerCase()) < 1 && ultraPlayer.getCurrentGadget().getType().requiresAmmo()) {
                            ultraPlayer.getCurrentGadget().lastPage = currentPage;
                            ultraPlayer.getCurrentGadget().openAmmoPurchaseMenu();
                        } else {
                            if (!UltraCosmeticsData.get().shouldCloseAfterSelect()) {
                                open(ultraPlayer, currentPage);
                            }
                        }
                    }
                });
            }
            i++;
        }

        // Previous page item.
        if (page > 1) {
            MaterialData materialData = ItemFactory.createFromConfig("Categories.Previous-Page-Item");
            int finalPage = page;
            putItem(inventory, getSize() - 9, ItemFactory.create(materialData.getItemType(), materialData.getData(),
                    MessageManager.getMessage("Menu.Previous-Page")), (data) -> open(player, finalPage - 1));
        }

        // Next page item.
        if (page < getMaxPages()) {
            MaterialData materialData = ItemFactory.createFromConfig("Categories.Next-Page-Item");
            int finalPage = page;
            putItem(inventory, getSize() - 1, ItemFactory.create(materialData.getItemType(), materialData.getData(),
                    MessageManager.getMessage("Menu.Next-Page")), (data) -> open(player, finalPage + 1));
        }

        // Clear cosmetic item.
        MaterialData materialData = ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item");
        String message = MessageManager.getMessage(CATEGORY.getClearConfigPath());
        ItemStack itemStack = ItemFactory.create(materialData.getItemType(), materialData.getData(), message);
        int finalPage1 = page;
        putItem(inventory, inventory.getSize() - 4, itemStack, data -> {
            toggleOff(player);
            open(player, finalPage1);
        });

        putItems(inventory, player, page);

        player.getBukkitPlayer().openInventory(inventory);
    }

    @Override
    protected int getSize() {
        return 54;
    }

    /**
     * @return The name of the menu.
     */
    @Override
    protected String getName() {
        return MessageManager.getMessage("Menus." + CATEGORY.getConfigPath());
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer ultraPlayer, int page) {
        // Go Back to Main Menu Arrow.
        if (getCategory().hasGoBackArrow()) {
            MaterialData backData = ItemFactory.createFromConfig("Categories.Back-Main-Menu-Item");
            String message = MessageManager.getMessage("Menu.Main-Menu");
            ItemStack item = ItemFactory.create(backData.getItemType(), backData.getData(), message);
            putItem(inventory, inventory.getSize() - 6, item, (data) -> getUltraCosmetics().getMenus().getMainMenu().open(ultraPlayer));
        }
    }

    @Override
    public List<SuitType> enabled() {
        return SuitType.enabled();
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, String name, UltraCosmetics ultraCosmetics) {
        SuitType.getByName(name).equip(ultraPlayer, ultraCosmetics, ArmorSlot.getByName(name.split(" ")[1])).equip();
    }

    @Override
    protected void toggleOff(UltraPlayer ultraPlayer) {
        ultraPlayer.removeSuit();
    }

    @Override
    protected Cosmetic getCosmetic(UltraPlayer ultraPlayer) {
        return ultraPlayer.getSuit(ArmorSlot.CHESTPLATE);
    }

    @Override
    protected int getItemsPerPage() {
        return 7;
    }
}