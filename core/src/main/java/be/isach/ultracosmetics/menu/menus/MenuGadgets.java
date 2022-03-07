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

import java.util.ArrayList;
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
        int slot = inventory.getSize() - (getCategory().hasGoBackArrow() ? 5 : 6);
        String configPath;
        boolean toggle;
        if (player.hasGadgetsEnabled()) {
            configPath = "Categories.Gadgets-Item.When-Enabled";
            toggle = false;
        } else {
            configPath = "Categories.Gadgets-Item.When-Disabled";
            toggle = true;
        }
        String msg = MessageManager.getMessage((toggle ? "Enable" : "Disable") + "-Gadgets");
        ClickRunnable run = data -> {
            player.setGadgetsEnabled(!player.hasGadgetsEnabled());
            putToggleGadgetsItems(inventory, player);
        };
        putItem(inventory, slot, ItemFactory.rename(ItemFactory.getItemStackFromConfig(configPath), msg), run);
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
            int ammo = player.getAmmo(gadgetType);
            loreList.add(MessageManager.getMessage("Ammo").replace("%ammo%", "" + ammo));
            loreList.add(MessageManager.getMessage("Right-Click-Buy-Ammo"));

            if (SettingsManager.getConfig().getBoolean("Ammo-System-For-Gadgets.Show-Ammo-In-Menu-As-Item-Amount")
                    && !(player.getCurrentGadget() != null
                    && player.getCurrentGadget().getType() == gadgetType)) {
                itemStack.setAmount(Math.max(1, Math.min(64, ammo)));
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
    protected void toggleOn(UltraPlayer ultraPlayer, GadgetType gadgetType, UltraCosmetics ultraCosmetics) {
        gadgetType.equip(ultraPlayer, ultraCosmetics);
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
