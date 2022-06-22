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

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Gadget {@link be.isach.ultracosmetics.menu.Menu Menu}.
 *
 * @author iSach
 * @since 07-23-2016
 */
public class MenuGadgets extends CosmeticMenu<GadgetType> {

    public MenuGadgets(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.GADGETS);
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer player, int page) {
        putToggleGadgetsItems(inventory, player);
    }

    private void putToggleGadgetsItems(Inventory inventory, UltraPlayer player) {
        int slot = inventory.getSize() - (getCategory().hasGoBackArrow() ? 4 : 6);
        String configPath = "Categories.Gadgets-Item.When-" + (player.hasGadgetsEnabled() ? "En" : "Dis") + "abled";
        String key = (player.hasGadgetsEnabled() ? "Dis" : "En") + "able-Gadgets";
        String msg = MessageManager.getMessage(key);
        String[] lore = MessageManager.getMessage(key + "-Lore").split("\n");
        if (lore[0].isEmpty()) {
            lore = new String[] {};
        }
        ClickRunnable run = data -> {
            player.setGadgetsEnabled(!player.hasGadgetsEnabled());
            putToggleGadgetsItems(inventory, player);
        };
        putItem(inventory, slot, ItemFactory.rename(ItemFactory.getItemStackFromConfig(configPath), msg, lore), run);
    }

    @Override
    protected void filterItem(ItemStack itemStack, GadgetType gadgetType, UltraPlayer player) {
        if (!UltraCosmeticsData.get().isAmmoEnabled() || !gadgetType.requiresAmmo() || !player.hasPermission(gadgetType.getPermission())) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> loreList = itemMeta.getLore();

        loreList.add("");
        int ammo = player.getAmmo(gadgetType);
        loreList.add(MessageManager.getMessage("Ammo").replace("%ammo%", "" + ammo));
        if (ultraCosmetics.getEconomyHandler().isUsingEconomy()) {
            loreList.add(MessageManager.getMessage("Right-Click-Buy-Ammo"));
        }

        if (SettingsManager.getConfig().getBoolean("Ammo-System-For-Gadgets.Show-Ammo-In-Menu-As-Item-Amount")
                && !(player.getCurrentGadget() != null
                        && player.getCurrentGadget().getType() == gadgetType)) {
            itemStack.setAmount(Math.max(1, Math.min(64, ammo)));
        }
        itemMeta.setLore(loreList);
        itemStack.setItemMeta(itemMeta);
    }

    @Override
    public List<GadgetType> enabled() {
        return GadgetType.enabled();
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, GadgetType gadgetType, UltraCosmetics ultraCosmetics) {
        gadgetType.equip(ultraPlayer, ultraCosmetics);
    }

    @Override
    protected void handleRightClick(UltraPlayer ultraPlayer, GadgetType type) {
        if (ultraCosmetics.getEconomyHandler().isUsingEconomy() && UltraCosmeticsData.get().isAmmoEnabled() && type.requiresAmmo()) {
            toggleOn(ultraPlayer, type, getUltraCosmetics());
            ultraPlayer.setGadgetsPage(getCurrentPage(ultraPlayer));
            ultraCosmetics.getMenus().openAmmoPurchaseMenu(type, ultraPlayer);
        }
    }

    @Override
    protected boolean handleActivate(UltraPlayer ultraPlayer) {
        if (ultraCosmetics.getEconomyHandler().isUsingEconomy() && UltraCosmeticsData.get().isAmmoEnabled() && ultraPlayer.getCurrentGadget().getType().requiresAmmo() && ultraPlayer.getAmmo(ultraPlayer.getCurrentGadget().getType()) < 1) {
            ultraPlayer.setGadgetsPage(getCurrentPage(ultraPlayer));
            ultraCosmetics.getMenus().openAmmoPurchaseMenu(ultraPlayer.getCurrentGadget().getType(), ultraPlayer);
            return false;
        }
        return super.handleActivate(ultraPlayer);
    }
}
