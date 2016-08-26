package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.menu.ClickRunnable;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

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
        putToggleGadgetsItems(inventory, player);
    }

    private void putToggleGadgetsItems(Inventory inventory, UltraPlayer player) {
        int slot = inventory.getSize() - (getCategory().hasGoBackArrow() ? 5 : 6);
        MaterialData materialData;
        boolean toggle;
        if (player.hasGadgetsEnabled()) {
            materialData = ItemFactory.createFromConfig("Categories.Gadgets-Item.When-Enabled");
            toggle = false;
        } else {
            materialData = ItemFactory.createFromConfig("Categories.Gadgets-Item.When-Disabled");
            toggle = true;
        }
        String msg = MessageManager.getMessage((toggle ? "Enable" : "Disable") + "-Gadgets");
        ClickRunnable run = data -> {
            player.setGadgetsEnabled(!player.hasGadgetsEnabled());
            putToggleGadgetsItems(inventory, player);
        };
        putItem(inventory, slot, ItemFactory.create(materialData.getItemType(), materialData.getData(), msg), run);
    }

    @Override
    protected ItemStack filterItem(ItemStack itemStack, GadgetType gadgetType, UltraPlayer player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (UltraCosmeticsData.get().isAmmoEnabled() && gadgetType.requiresAmmo()) {
            List<String> loreList = new ArrayList<>();
            if (itemMeta.hasLore()) {
                loreList = itemMeta.getLore();
            }

            loreList.add("");
            int ammo = player.getAmmo(gadgetType.toString().toLowerCase());
            loreList.add(MessageManager.getMessage("Ammo").replace("%ammo%", "" + ammo));
            loreList.add(MessageManager.getMessage("Right-Click-Buy-Ammo"));

            if (SettingsManager.getConfig().getBoolean("Ammo-System-For-Gadgets.Show-Ammo-In-Menu-As-Item-Amount")
                    && !(player.getCurrentGadget() != null
                    && player.getCurrentGadget().getType() == gadgetType)) {
                itemStack.setAmount(Math.max(0, Math.min(64, ammo)));
            }
            itemMeta.setLore(loreList);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public List<GadgetType> enabled() {
        return GadgetType.enabled();
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, String name, UltraCosmetics ultraCosmetics) {
        GadgetType.getByName(name).equip(ultraPlayer, ultraCosmetics);
    }

    @Override
    protected void toggleOff(UltraPlayer ultraPlayer) {
        ultraPlayer.removeGadget();
    }

    @Override
    protected Cosmetic getCosmetic(UltraPlayer ultraPlayer) {
        return ultraPlayer.getCurrentGadget();
    }
}
