<<<<<<< HEAD
package be.isach.ultracosmetics.menu;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.CosmeticMatType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

/**
 * Package: be.isach.ultracosmetics.menu
 * Created by: sachalewin
 * Date: 9/08/16
 * Project: UltraCosmetics
 */
public abstract class CosmeticMenu<T extends CosmeticMatType> extends Menu {

    public final static int[] COSMETICS_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };


    private Category category;

    public CosmeticMenu(UltraCosmetics ultraCosmetics, Category category) {
        super(ultraCosmetics);
        this.category = category;
    }

    @Override
    public void open(UltraPlayer player) {
        open(player, 1);
    }

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
        int from = 21 * (page - 1) + 1;
        int to = 21 * page;
        for (int h = from; h <= to; h++) {
            if (h > enabled().size()) {
                break;
            }

            T cosmeticMatType = enabled().get(h - 1);

            if (!cosmeticMatType.isEnabled()) {
                continue;
            }

            if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item")
                    && !player.hasPermission(cosmeticMatType.getPermission())) {
                continue;
            }

            if (SettingsManager.getConfig().getBoolean("No-Permission.Custom-Item.enabled")
                    && !player.hasPermission(cosmeticMatType.getPermission())) {
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("No-Permission.Custom-Item.Type"));
                Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Data")));
                String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replace("{cosmetic-name}", cosmeticMatType.getName()).replace("&", "§");
                List<String> npLore = SettingsManager.getConfig().getStringList("No-Permission.Custom-Item.Lore");
                String[] array = new String[npLore.size()];
                npLore.toArray(array);
                putItem(inventory, COSMETICS_SLOTS[i], ItemFactory.create(material, data, name, array), null);
                i++;
                continue;
            }

            String toggle = category.getActivateMenu();

            if (getCosmetic(player) != null && getCosmetic(player).getType() == cosmeticMatType) {
                toggle = category.getDeactivateMenu();
            }

            String typeName = getTypeName(cosmeticMatType, player);

            ItemStack is = ItemFactory.create(cosmeticMatType.getMaterial(), cosmeticMatType.getData(), toggle + " " + typeName);
            if (getCosmetic(player) != null && getCosmetic(player).getType() == cosmeticMatType) {
                is = ItemFactory.addGlow(is);
            }

            ItemMeta itemMeta = is.getItemMeta();
            List<String> loreList = new ArrayList<>();

            if (cosmeticMatType.showsDescription()) {
                loreList.add("");
                loreList.addAll(cosmeticMatType.getDescription());
                loreList.add("");
            }

            if (SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore")) {
                String yesOrNo = player.hasPermission(cosmeticMatType.getPermission()) ? "Yes" : "No";
                String s = SettingsManager.getConfig().getString("No-Permission.Lore-Message-" + yesOrNo);
                loreList.add(ChatColor.translateAlternateColorCodes('&', s));
            }

            itemMeta.setLore(loreList);
            is.setItemMeta(itemMeta);
            is = filterItem(is, cosmeticMatType, player);
            putItem(inventory, COSMETICS_SLOTS[i], is, (data) -> {
                UltraPlayer ultraPlayer = data.getClicker();
                ItemStack clicked = data.getClicked();
                int currentPage = getCurrentPage(ultraPlayer);
                if (UltraCosmeticsData.get().shouldCloseAfterSelect()) {
                    ultraPlayer.getBukkitPlayer().closeInventory();
                }
                if (UltraCosmeticsData.get().isAmmoEnabled() && data.getAction() == InventoryAction.PICKUP_HALF) {
                    StringBuilder sb = new StringBuilder();
                    for (int k = 1; k < clicked.getItemMeta().getDisplayName().split(" ").length; k++) {
                        sb.append(clicked.getItemMeta().getDisplayName().split(" ")[k]);
                        try {
                            if (clicked.getItemMeta().getDisplayName().split(" ")[k + 1] != null)
                                sb.append(" ");
                        } catch (Exception ignored) {
                        }
                    }
                    if (getCosmetic(ultraPlayer) == null) {
                        toggleOff(ultraPlayer);
                    }
                    toggleOn(ultraPlayer, sb.toString(), getUltraCosmetics());

                    if (getCategory() == Category.GADGETS) {
                        if (ultraPlayer.getCurrentGadget().getType().requiresAmmo()) {
                            ultraPlayer.getCurrentGadget().lastPage = currentPage;
                            ultraPlayer.getCurrentGadget().openAmmoPurchaseMenu();
                            ultraPlayer.getCurrentGadget().openGadgetsInvAfterAmmo = true;
                        }
                    }
                    return;
                }

                if (clicked.getItemMeta().getDisplayName().startsWith(category.getDeactivateMenu())) {
                    toggleOff(ultraPlayer);
                    if (!UltraCosmeticsData.get().shouldCloseAfterSelect()) {
                        open(ultraPlayer, currentPage);
                    }
                } else if (clicked.getItemMeta().getDisplayName().startsWith(category.getActivateMenu())) {
                    toggleOff(ultraPlayer);
                    StringBuilder sb = new StringBuilder();
                    String name = clicked.getItemMeta().getDisplayName().replaceFirst(category.getActivateMenu(), "");
                    int j = name.split(" ").length;
                    if (name.contains("(")) {
                        j--;
                    }
                    for (int k = 1; k < j; k++) {
                        sb.append(name.split(" ")[k]);
                        try {
                            if (clicked.getItemMeta().getDisplayName().split(" ")[k + 1] != null)
                                sb.append(" ");
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
            i++;
        }

        // Previous page item.
        if (page > 1) {
            MaterialData materialData = ItemFactory.createFromConfig("Categories.Previous-Page-Item");
            int finalPage = page;
            putItem(inventory, getSize() - 18, ItemFactory.create(materialData.getItemType(), materialData.getData(),
                    MessageManager.getMessage("Menu.Previous-Page")), (data) -> open(player, finalPage - 1));
        }

        // Next page item.
        if (page < getMaxPages()) {
            MaterialData materialData = ItemFactory.createFromConfig("Categories.Next-Page-Item");
            int finalPage = page;
            putItem(inventory, getSize() - 10, ItemFactory.create(materialData.getItemType(), materialData.getData(),
                    MessageManager.getMessage("Menu.Next-Page")), (data) -> open(player, finalPage + 1));
        }

        // Clear cosmetic item.
        MaterialData materialData = ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item");
        String message = MessageManager.getMessage(category.getClearConfigPath());
        ItemStack itemStack = ItemFactory.create(materialData.getItemType(), materialData.getData(), message);
        int finalPage1 = page;
        putItem(inventory, inventory.getSize() - 4, itemStack, data -> {
            toggleOff(player);
            open(player, finalPage1);
        });

        // Go Back to Main Menu Arrow.
        if (getCategory().hasGoBackArrow()) {
            MaterialData backData = ItemFactory.createFromConfig("Categories.Back-Main-Menu-Item");
            ItemStack item = ItemFactory.create(backData.getItemType(), backData.getData(),
                    MessageManager.getMessage("Menu.Main-Menu"));
            putItem(inventory, inventory.getSize() - 6, item, (data) -> {
                getUltraCosmetics().getMenus().getMainMenu().open(player);
            });
        }

        putItems(inventory, player, page);

        player.getBukkitPlayer().openInventory(inventory);
    }

    public T getCosmeticType(String name) {
        for (T effectType : enabled()) {
            if (effectType.getConfigName().replace(" ", "").equals(name.replace(" ", ""))) {
                return effectType;
            }
        }
        return null;
    }

    /**
     * @param ultraPlayer The menu owner.
     * @return The current page of the menu opened by ultraPlayer.
     */
    protected int getCurrentPage(UltraPlayer ultraPlayer) {
        Player player = ultraPlayer.getBukkitPlayer();
        String title = player.getOpenInventory().getTopInventory().getTitle();
        if (player.getOpenInventory() != null
                && title.startsWith(getName())
                && !title.equals(getName())) {
            String s = player.getOpenInventory().getTopInventory().getTitle()
                    .replace(getName() + " §7§o(", "")
                    .replace("/" + getMaxPages() + ")", "");
            return Integer.parseInt(s);
        }
        return 0;
    }

    /**
     * Gets the max amount of pages.
     *
     * @return the maximum amount of pages.
     */
    protected int getMaxPages() {
        int max = getItemsPerPage();
        int i = enabled().size();
        if (i % max == 0) return i / max;
        double j = i / getItemsPerPage();
        int h = (int) Math.floor(j * 100) / 100;
        return h + 1;
    }

    /**
     * This method can be overridden
     * to modify an itemstack of a
     * category being placed in the
     * inventory.
     *
     * @param itemStack    Item Stack being placed.
     * @param cosmeticType The Cosmetic Type.
     * @param player       The Inventory Opener.
     * @return The new item stack filtered.
     */
    protected ItemStack filterItem(ItemStack itemStack, T cosmeticType, UltraPlayer player) {
        return itemStack;
    }

    protected String getTypeName(T cosmeticType, UltraPlayer ultraPlayer) {
        return cosmeticType.getName();
    }

    /**
     * @param page The page to open.
     * @return The name of the menu with page detailed.
     */
    protected String getName(int page) {
        return MessageManager.getMessage("Menus." + category.getConfigPath()) + " §7§o(" + page + "/" + getMaxPages() + ")";
    }

    @Override
    protected int getSize() {
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

    @Override
    protected void putItems(Inventory inventory, UltraPlayer ultraPlayer) {
        //--
    }

    /**
     * @return The name of the menu.
     */
    @Override
    protected String getName() {
        return MessageManager.getMessage("Menus." + category.getConfigPath());
    }

    public Category getCategory() {
        return category;
    }

    protected int getItemsPerPage() {
        return 12;
    }

    /**
     * Puts items in the inventory.
     *
     * @param inventory   Inventory.
     * @param ultraPlayer Inventory Owner.
     * @param page        Page to open.
     */
    abstract protected void putItems(Inventory inventory, UltraPlayer ultraPlayer, int page);

    abstract public List<T> enabled();

    abstract protected void toggleOn(UltraPlayer ultraPlayer, String name, UltraCosmetics ultraCosmetics);

    abstract protected void toggleOff(UltraPlayer ultraPlayer);

    abstract protected Cosmetic getCosmetic(UltraPlayer ultraPlayer);
}
=======
package be.isach.ultracosmetics.menu;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.CosmeticMatType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

/**
 * Package: be.isach.ultracosmetics.menu
 * Created by: sachalewin
 * Date: 9/08/16
 * Project: UltraCosmetics
 */
public abstract class CosmeticMenu<T extends CosmeticMatType> extends Menu {

    public final static int[] COSMETICS_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };


    private Category category;

    public CosmeticMenu(UltraCosmetics ultraCosmetics, Category category) {
        super(ultraCosmetics);
        this.category = category;
    }

    @Override
    public void open(UltraPlayer player) {
        open(player, 1);
    }

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
        int from = 21 * (page - 1) + 1;
        int to = 21 * page;
        for (int h = from; h <= to; h++) {
            if (h > enabled().size()) {
                break;
            }

            T cosmeticMatType = enabled().get(h - 1);

            if (!cosmeticMatType.isEnabled()) {
                continue;
            }

            if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item")
                    && !player.hasPermission(cosmeticMatType.getPermission())) {
                continue;
            }

            if (SettingsManager.getConfig().getBoolean("No-Permission.Custom-Item.enabled")
                    && !player.hasPermission(cosmeticMatType.getPermission())) {
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("No-Permission.Custom-Item.Type"));
                Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Data")));
                String name = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replace("{cosmetic-name}", cosmeticMatType.getName()));
                List<String> npLore = SettingsManager.getConfig().getStringList("No-Permission.Custom-Item.Lore");
                String[] array = new String[npLore.size()];
                npLore.toArray(array);
                putItem(inventory, COSMETICS_SLOTS[i], ItemFactory.create(material, data, name, array), null);
                i++;
                continue;
            }

            String toggle = category.getActivateMenu();

            if (getCosmetic(player) != null && getCosmetic(player).getType() == cosmeticMatType) {
                toggle = category.getDeactivateMenu();
            }

            String typeName = getTypeName(cosmeticMatType, player);

            ItemStack is = ItemFactory.create(cosmeticMatType.getMaterial(), cosmeticMatType.getData(), toggle + " " + typeName);
            if (getCosmetic(player) != null && getCosmetic(player).getType() == cosmeticMatType) {
                is = ItemFactory.addGlow(is);
            }

            ItemMeta itemMeta = is.getItemMeta();
            List<String> loreList = new ArrayList<>();

            if (cosmeticMatType.showsDescription()) {
                loreList.add("");
                loreList.addAll(cosmeticMatType.getDescription());
                loreList.add("");
            }

            if (SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore")) {
                String yesOrNo = player.hasPermission(cosmeticMatType.getPermission()) ? "Yes" : "No";
                String s = SettingsManager.getConfig().getString("No-Permission.Lore-Message-" + yesOrNo);
                loreList.add(ChatColor.translateAlternateColorCodes('&', s));
            }

            itemMeta.setLore(loreList);
            is.setItemMeta(itemMeta);
            is = filterItem(is, cosmeticMatType, player);
            putItem(inventory, COSMETICS_SLOTS[i], is, (data) -> {
                UltraPlayer ultraPlayer = data.getClicker();
                ItemStack clicked = data.getClicked();
                int currentPage = getCurrentPage(ultraPlayer);
                if (UltraCosmeticsData.get().shouldCloseAfterSelect()) {
                    ultraPlayer.getBukkitPlayer().closeInventory();
                }
                if (UltraCosmeticsData.get().isAmmoEnabled() && data.getAction() == InventoryAction.PICKUP_HALF) {
                    StringBuilder sb = new StringBuilder();
                    for (int k = 1; k < clicked.getItemMeta().getDisplayName().split(" ").length; k++) {
                        sb.append(clicked.getItemMeta().getDisplayName().split(" ")[k]);
                        try {
                            if (clicked.getItemMeta().getDisplayName().split(" ")[k + 1] != null)
                                sb.append(" ");
                        } catch (Exception ignored) {
                        }
                    }
                    if (getCosmetic(ultraPlayer) == null) {
                        toggleOff(ultraPlayer);
                    }
                    toggleOn(ultraPlayer, sb.toString(), getUltraCosmetics());

                    if (getCategory() == Category.GADGETS) {
                        if (ultraPlayer.getCurrentGadget().getType().requiresAmmo()) {
                            ultraPlayer.getCurrentGadget().lastPage = currentPage;
                            ultraPlayer.getCurrentGadget().openAmmoPurchaseMenu();
                            ultraPlayer.getCurrentGadget().openGadgetsInvAfterAmmo = true;
                        }
                    }
                    return;
                }

                if (clicked.getItemMeta().getDisplayName().startsWith(category.getDeactivateMenu())) {
                    toggleOff(ultraPlayer);
                    if (!UltraCosmeticsData.get().shouldCloseAfterSelect()) {
                        open(ultraPlayer, currentPage);
                    }
                } else if (clicked.getItemMeta().getDisplayName().startsWith(category.getActivateMenu())) {
                    toggleOff(ultraPlayer);
                    StringBuilder sb = new StringBuilder();
                    String name = clicked.getItemMeta().getDisplayName().replaceFirst(category.getActivateMenu(), "");
                    int j = name.split(" ").length;
                    if (name.contains("(")) {
                        j--;
                    }
                    for (int k = 1; k < j; k++) {
                        sb.append(name.split(" ")[k]);
                        try {
                            if (clicked.getItemMeta().getDisplayName().split(" ")[k + 1] != null)
                                sb.append(" ");
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
            i++;
        }

        // Previous page item.
        if (page > 1) {
            MaterialData materialData = ItemFactory.createFromConfig("Categories.Previous-Page-Item");
            int finalPage = page;
            putItem(inventory, getSize() - 18, ItemFactory.create(materialData.getItemType(), materialData.getData(),
                    MessageManager.getMessage("Menu.Previous-Page")), (data) -> open(player, finalPage - 1));
        }

        // Next page item.
        if (page < getMaxPages()) {
            MaterialData materialData = ItemFactory.createFromConfig("Categories.Next-Page-Item");
            int finalPage = page;
            putItem(inventory, getSize() - 10, ItemFactory.create(materialData.getItemType(), materialData.getData(),
                    MessageManager.getMessage("Menu.Next-Page")), (data) -> open(player, finalPage + 1));
        }

        // Clear cosmetic item.
        MaterialData materialData = ItemFactory.createFromConfig("Categories.Clear-Cosmetic-Item");
        String message = MessageManager.getMessage(category.getClearConfigPath());
        ItemStack itemStack = ItemFactory.create(materialData.getItemType(), materialData.getData(), message);
        putItem(inventory, inventory.getSize() - 4, itemStack, data -> {
            toggleOff(player);
        });

        // Go Back to Main Menu Arrow.
        if (getCategory().hasGoBackArrow()) {
            MaterialData backData = ItemFactory.createFromConfig("Categories.Back-Main-Menu-Item");
            ItemStack item = ItemFactory.create(backData.getItemType(), backData.getData(),
                    MessageManager.getMessage("Menu.Main-Menu"));
            putItem(inventory, inventory.getSize() - 6, item, (data) -> {
                getUltraCosmetics().getMenus().getMainMenu().open(player);
            });
        }

        putItems(inventory, player, page);

        player.getBukkitPlayer().openInventory(inventory);
    }

    public T getCosmeticType(String name) {
        for (T effectType : enabled()) {
            if (effectType.getConfigName().replace(" ", "").equals(name.replace(" ", ""))) {
                return effectType;
            }
        }
        return null;
    }

    /**
     * @param ultraPlayer The menu owner.
     * @return The current page of the menu opened by ultraPlayer.
     */
    protected int getCurrentPage(UltraPlayer ultraPlayer) {
        Player player = ultraPlayer.getBukkitPlayer();
        String title = player.getOpenInventory().getTopInventory().getTitle();
        if (player.getOpenInventory() != null
                && title.startsWith(getName())
                && !title.equals(getName())) {
            String s = player.getOpenInventory().getTopInventory().getTitle()
                    .replace(getName() + " " + ChatColor.GRAY + "" + ChatColor.ITALIC + "(", "")
                    .replace("/" + getMaxPages() + ")", "");
            return Integer.parseInt(s);
        }
        return 0;
    }

    /**
     * Gets the max amount of pages.
     *
     * @return the maximum amount of pages.
     */
    protected int getMaxPages() {
        int max = 21;
        int i = enabled().size();
        if (i % max == 0) return i / max;
        double j = i / 21;
        int h = (int) Math.floor(j * 100) / 100;
        return h + 1;
    }
    
    protected int getItemsPerPage() {
    	return 12;
    }

    /**
     * This method can be overridden
     * to modify an itemstack of a
     * category being placed in the
     * inventory.
     *
     * @param itemStack    Item Stack being placed.
     * @param cosmeticType The Cosmetic Type.
     * @param player       The Inventory Opener.
     * @return The new item stack filtered.
     */
    protected ItemStack filterItem(ItemStack itemStack, T cosmeticType, UltraPlayer player) {
        return itemStack;
    }

    protected String getTypeName(T cosmeticType, UltraPlayer ultraPlayer) {
        return cosmeticType.getName();
    }

    /**
     * @param page The page to open.
     * @return The name of the menu with page detailed.
     */
    protected String getName(int page) {
        return MessageManager.getMessage("Menus." + category.getConfigPath()) + " " + ChatColor.GRAY + "" + ChatColor.ITALIC + "(" + page + "/" + getMaxPages() + ")";
    }

    @Override
    protected int getSize() {
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

    @Override
    protected void putItems(Inventory inventory, UltraPlayer ultraPlayer) {
        //--
    }

    /**
     * @return The name of the menu.
     */
    @Override
    protected String getName() {
        return MessageManager.getMessage("Menus." + category.getConfigPath());
    }

    public Category getCategory() {
        return category;
    }

    /**
     * Puts items in the inventory.
     *
     * @param inventory   Inventory.
     * @param ultraPlayer Inventory Owner.
     * @param page        Page to open.
     */
    abstract protected void putItems(Inventory inventory, UltraPlayer ultraPlayer, int page);

    abstract public List<T> enabled();

    abstract protected void toggleOn(UltraPlayer ultraPlayer, String name, UltraCosmetics ultraCosmetics);

    abstract protected void toggleOff(UltraPlayer ultraPlayer);

    abstract protected Cosmetic getCosmetic(UltraPlayer ultraPlayer);
}
>>>>>>> refs/remotes/origin/master
