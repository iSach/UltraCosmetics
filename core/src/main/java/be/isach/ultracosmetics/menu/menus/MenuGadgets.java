package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.menu.ClickRunnable;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Package: be.isach.ultracosmetics.menu.menus
 * Created by: sachalewin
 * Date: 23/07/16
 * Project: UltraCosmetics
 */
public class MenuGadgets extends CosmeticMenu<GadgetType> {

    public MenuGadgets(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.GADGETS);
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer player, int page) {
        int i = 0;
        int from = 1;
        if (page > 1)
            from = 21 * (page - 1) + 1;
        int to = 21 * page;
        for (int h = from; h <= to; h++) {
            if (h > enabled().size())
                break;
            GadgetType g = GadgetType.enabled().get(h - 1);
            if (!g.isEnabled()) continue;
            if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item"))
                if (!player.hasPermission(g.getPermission()))
                    continue;
            if (SettingsManager.getConfig().getBoolean("No-Permission.Custom-Item.enabled") && !player.hasPermission(g.getPermission())) {
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("No-Permission.Custom-Item.Type"));
                Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Data")));
                String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replace("{cosmetic-name}", g.getName()).replace("&", "§");
                List<String> npLore = SettingsManager.getConfig().getStringList("No-Permission.Custom-Item.Lore");
                String[] array = new String[npLore.size()];
                npLore.toArray(array);
                putItem(inventory, COSMETICS_SLOTS[i], ItemFactory.create(material, data, name, array), ((clickData) -> {}));
                i++;
                continue;
            }

            String toggle = MessageManager.getMessage("Menu.Activate");
            if (player.getCurrentGadget() != null && player.getCurrentGadget().getCosmeticType() == g)
                toggle = MessageManager.getMessage("Menu.Deactivate");
            ItemStack is = ItemFactory.create(g.getMaterial(), g.getData(), toggle + " " + g.getName());
            if (player.getCurrentGadget() != null && player.getCurrentGadget().getCosmeticType() == g)
                is = ItemFactory.addGlow(is);
            ItemMeta itemMeta = is.getItemMeta();
            List<String> loreList = new ArrayList<>();
            if (UltraCosmeticsData.get().isAmmoEnabled() && g.requiresAmmo()) {
                if (itemMeta.hasLore())
                    loreList = itemMeta.getLore();
                loreList.add("");
                int ammo = player.getAmmo(g.toString().toLowerCase());
                loreList.add(MessageManager.getMessage("Ammo").replace("%ammo%", "" + ammo));
                loreList.add(MessageManager.getMessage("Right-Click-Buy-Ammo"));

                if (SettingsManager.getConfig().getBoolean("Ammo-System-For-Gadgets.Show-Ammo-In-Menu-As-Item-Amount")
                        && !(player.getCurrentGadget() != null && player.getCurrentGadget().getCosmeticType() == g && ammo == 0))
                    is.setAmount(Math.max(0, Math.min(64, ammo)));
            }
            if (g.showsDescription()) {
                loreList.add("");
                for (String s : g.getDescription())
                    loreList.add(s);
                loreList.add("");
            }
            if (SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore"))
                loreList.add(ChatColor.translateAlternateColorCodes('&',
                        String.valueOf(SettingsManager.getConfig().get("No-Permission.Lore-Message-" +
                                ((player.hasPermission(g.getPermission()) ? "Yes" : "No"))))));
            itemMeta.setLore(loreList);
            is.setItemMeta(itemMeta);
            putItem(inventory, COSMETICS_SLOTS[i], is, (data) -> {
                UltraPlayer ultraPlayer = data.getClicker();
                ItemStack clicked = data.getClicked();
                int slot = data.getSlot();
                if (clicked.getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu"))) {
                    //UltraCosmetics.openMainMenuFromOther(ultraPlayer.getPlayer());
                    return;
                } else if (clicked.getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Gadget"))) {
                    if (ultraPlayer.getCurrentGadget() != null) {
                        int currentPage = getCurrentPage(ultraPlayer);
                        ultraPlayer.getPlayer().closeInventory();
                        ultraPlayer.removeGadget();
                        open(ultraPlayer, currentPage);
                    } else return;
                    return;
                } else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Menu.Next-Page"))) {
                    open(ultraPlayer, getCurrentPage(ultraPlayer) + 1);
                    return;
                } else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Menu.Previous-Page"))) {
                    open(ultraPlayer, getCurrentPage(ultraPlayer) - 1);
                    return;
                } else if (clicked.getItemMeta().getDisplayName().equals(MessageManager.getMessage("Enable-Gadgets"))) {
                    ultraPlayer.setGadgetsEnabled(true);
                    inventory.setItem(slot, ItemFactory.create(Material.INK_SACK, (byte) 0xa, MessageManager.getMessage("Disable-Gadgets")));
                    return;
                } else if (clicked.getItemMeta().getDisplayName().equals(MessageManager.getMessage("Disable-Gadgets"))) {
                    ultraPlayer.setGadgetsEnabled(false);
                    inventory.setItem(slot, ItemFactory.create(Material.INK_SACK, (byte) 0x8, MessageManager.getMessage("Enable-Gadgets")));
                    return;
                }
                int currentPage = getCurrentPage(ultraPlayer);
                if (UltraCosmeticsData.get().shouldCloseAfterSelect())
                    ultraPlayer.getPlayer().closeInventory();
                if (UltraCosmeticsData.get().isAmmoEnabled() && data.getAction() == InventoryAction.PICKUP_HALF) {
                    StringBuilder sb = new StringBuilder();
                    for (int k = 1; k < clicked.getItemMeta().getDisplayName().split(" ").length; k++) {
                        sb.append(clicked.getItemMeta().getDisplayName().split(" ")[k]);
                        try {
                            if (clicked.getItemMeta().getDisplayName().split(" ")[k + 1] != null)
                                sb.append(" ");
                        } catch (Exception exc) {

                        }

                    }
                    if (ultraPlayer.getCurrentGadget() == null)
                        ultraPlayer.removeGadget();
//                    equipGadget(getGadgetByName(sb.toString()), ultraPlayer.getPlayer(), ultraCosmetics);
                    ultraPlayer.sendMessage(data.getClicked().getType());
                    if (ultraPlayer.getCurrentGadget().getCosmeticType().requiresAmmo()) {
                        ultraPlayer.getCurrentGadget().lastPage = currentPage;
                        ultraPlayer.getCurrentGadget().openAmmoPurchaseMenu();
                        ultraPlayer.getCurrentGadget().openGadgetsInvAfterAmmo = true;
                    }
                    return;
                }

                if (clicked.getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Deactivate"))) {
                    ultraPlayer.removeGadget();
                    if (!UltraCosmeticsData.get().shouldCloseAfterSelect()) {
                        open(ultraPlayer, currentPage);
                    }
                } else if (clicked.getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Activate"))) {
                    ultraPlayer.removeGadget();
                    StringBuilder sb = new StringBuilder();
                    String name = clicked.getItemMeta().getDisplayName().replaceFirst(MessageManager.getMessage("Menu.Activate"), "");
                    int j = name.split(" ").length;
                    if (name.contains("("))
                        j--;
                    for (int k = 1; k < j; k++) {
                        sb.append(name.split(" ")[k]);
                        try {
                            if (clicked.getItemMeta().getDisplayName().split(" ")[k + 1] != null)
                                sb.append(" ");
                        } catch (Exception exc) {

                        }
                    }
//                    equipGadget(getGadgetByName(sb.toString()), ultraPlayer.getPlayer(), ultraCosmetics);
                    if (ultraPlayer.getCurrentGadget() != null && UltraCosmeticsData.get().isAmmoEnabled() && ultraPlayer.getAmmo(ultraPlayer.getCurrentGadget().getCosmeticType().toString().toLowerCase()) < 1 && ultraPlayer.getCurrentGadget().getCosmeticType().requiresAmmo()) {
                        ultraPlayer.getCurrentGadget().lastPage = currentPage;
                        ultraPlayer.getCurrentGadget().openAmmoPurchaseMenu();
                    } else {
                        if (!UltraCosmeticsData.get().shouldCloseAfterSelect())
                            open(ultraPlayer, currentPage);
                    }
                }
            });
            i++;
        }
    }

    @Override
    public List<GadgetType> enabled() {
        return GadgetType.enabled();
    }
}
