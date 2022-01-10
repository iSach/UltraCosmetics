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
import be.isach.ultracosmetics.menu.CosmeticsInventoryHolder;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.UCMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Suit {@link be.isach.ultracosmetics.menu.Menu Menu}.
 *
 * @author iSach
 * @since 08-23-2016
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

        Inventory inventory = Bukkit.createInventory(new CosmeticsInventoryHolder(), getSize(), getMaxPages() == 1 ? getName() : getName(page));

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

            // Whole equip button

            ItemStack wholeEquipStack = UCMaterial.HOPPER.parseItem();
            ItemMeta wholeEquipMeta = wholeEquipStack.getItemMeta();
            wholeEquipMeta.setDisplayName(CATEGORY.getActivateMenu() + " " + MessageManager.getMessage("Suits." + suitType.getConfigName() + ".whole-equip"));
            wholeEquipMeta.setLore(Arrays.asList("", MessageManager.getMessage("Suits.Whole-Equip-Lore"), ""));
            wholeEquipStack.setItemMeta(wholeEquipMeta);
            putItem(inventory, SLOTS[i] - 9, wholeEquipStack, clickData -> {
                for (ArmorSlot armorSlot : ArmorSlot.values()) {
                    if (player.hasPermission(suitType.getPermission(armorSlot))) {
                        if (player.getSuit(armorSlot) != null
                                && player.getSuit(armorSlot).getType() == suitType) {
                            continue;
                        }
                        toggleOn(clickData.getClicker(), suitType, getUltraCosmetics(), armorSlot);
                    }
                }
                if (UltraCosmeticsData.get().shouldCloseAfterSelect()) {
                    player.getBukkitPlayer().closeInventory();
                } else {
                    open(player, getCurrentPage(player));
                }
            });

            //slotLoop:
            for (int l = 0; l < 4; l++) {
                ArmorSlot armorSlot = ArmorSlot.values()[l];
                Suit suit = player.getSuit(armorSlot);
                int slot = SLOTS[i] + l * 9;

                if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item")) {
                    if (!player.hasPermission(suitType.getPermission(armorSlot))) {
                        continue;
                    }
                }

                if (SettingsManager.getConfig().getBoolean("No-Permission.Custom-Item.enabled")
                        && !player.hasPermission(suitType.getPermission(armorSlot))) {
                    UCMaterial material = UCMaterial.matchUCMaterial(SettingsManager.getConfig().getString("No-Permission.Custom-Item.Type"));
                    // Byte data = Byte.valueOf(SettingsManager.getConfig().getString("No-Permission.Custom-Item.Data"));
                    String name = SettingsManager.getConfig().getString("No-Permission.Custom-Item.Name");
                    name = ChatColor.translateAlternateColorCodes('&', name.replace("{cosmetic-name}", suitType.getName()));
                    List<String> npLore = SettingsManager.getConfig().getStringList("No-Permission.Custom-Item.Lore");
                    String[] array = new String[npLore.size()];
                    npLore.toArray(array);
                    putItem(inventory, COSMETICS_SLOTS[i], ItemFactory.create(material, name, array), clickData -> {
                        Player clicker = clickData.getClicker().getBukkitPlayer();
                        clicker.sendMessage(MessageManager.getMessage("No-Permission"));
                        clicker.closeInventory();
                    });
                    i++;
                    continue superLoop;
                }

                String toggle = (suit != null && suit.getType() == suitType) ? CATEGORY.getDeactivateMenu() : CATEGORY.getActivateMenu();
                ItemStack is = ItemFactory.create(suitType.getMaterial(armorSlot), toggle + " " + suitType.getName(armorSlot));

                if (suit != null && suit.getType() == suitType) {
                    is = ItemFactory.addGlow(is);
                }

                ItemMeta itemMeta = is.getItemMeta();

                if (suitType == SuitType.valueOf("santa")
                        || suitType == SuitType.valueOf("rave")
                        || (suitType == SuitType.valueOf("frozen") && l != 0)) {
                    LeatherArmorMeta laMeta = (LeatherArmorMeta) itemMeta;

                    Color color = Color.RED;

                    if (suitType == SuitType.valueOf("rave")) {
                        int r = MathUtils.random(255);
                        int g = MathUtils.random(255);
                        int b = MathUtils.random(255);

                        color = Color.fromRGB(r, g, b);
                    }

                    if (suitType == SuitType.valueOf("frozen")) {
                        color = Color.AQUA;
                    }

                    laMeta.setColor(color);
                }

                List<String> loreList = new ArrayList<>();

                if (suitType.showsDescription()) {
                    loreList.add("");
                    loreList.addAll(suitType.getDescription());
                    loreList.add("");
                }

                if (SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore")) {
                    String yesOrNo = player.hasPermission(suitType.getPermission(armorSlot)) ? "Yes" : "No";
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
                        toggleOff(ultraPlayer, armorSlot);
                    } else if (clicked.getItemMeta().getDisplayName().startsWith(CATEGORY.getActivateMenu())) {
                        toggleOn(ultraPlayer, suitType, getUltraCosmetics(), armorSlot);
                    }
                    if (!UltraCosmeticsData.get().shouldCloseAfterSelect()) {
                        open(ultraPlayer, currentPage);
                    }
                });
            }
            i++;
        }

        // Previous page item.
        if (page > 1) {
            int finalPage = page;
            putItem(inventory, getSize() - 9, ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Previous-Page-Item"),
                    MessageManager.getMessage("Menu.Previous-Page")), (data) -> open(player, finalPage - 1));
        }

        // Next page item.
        if (page < getMaxPages()) {
            int finalPage = page;
            putItem(inventory, getSize() - 1, ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Next-Page-Item"),
                    MessageManager.getMessage("Menu.Next-Page")), (data) -> open(player, finalPage + 1));
        }

        // Clear cosmetic item.
        String message = MessageManager.getMessage(CATEGORY.getClearConfigPath());
        ItemStack itemStack = ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Clear-Cosmetic-Item"), message);
        int finalPage1 = page;
        putItem(inventory, inventory.getSize() - 4, itemStack, data -> {
            toggleOff(player);
            open(player, finalPage1);
        });

        putItems(inventory, player, page);
        ItemFactory.fillInventory(inventory);
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
            String message = MessageManager.getMessage("Menu.Main-Menu");
            ItemStack item = ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Back-Main-Menu-Item"), message);
            putItem(inventory, inventory.getSize() - 6, item, (data) -> getUltraCosmetics().openMainMenu(ultraPlayer));
        }
    }

    @Override
    public List<SuitType> enabled() {
        return SuitType.enabled();
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, SuitType suitType, UltraCosmetics ultraCosmetics) {
        suitType.equip(ultraPlayer, ultraCosmetics, ArmorSlot.CHESTPLATE);
    }

    protected void toggleOn(UltraPlayer ultraPlayer, SuitType suitType, UltraCosmetics ultraCosmetics, ArmorSlot armorSlot) {
        suitType.equip(ultraPlayer, ultraCosmetics, armorSlot);
    }

    protected void toggleOff(UltraPlayer ultraPlayer, ArmorSlot armorSlot) {
        ultraPlayer.removeSuit(armorSlot);
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