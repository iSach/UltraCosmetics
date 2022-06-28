package be.isach.ultracosmetics.menu;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.menu.menus.MenuEmotes;
import be.isach.ultracosmetics.menu.menus.MenuGadgets;
import be.isach.ultracosmetics.menu.menus.MenuHats;
import be.isach.ultracosmetics.menu.menus.MenuMain;
import be.isach.ultracosmetics.menu.menus.MenuMorphs;
import be.isach.ultracosmetics.menu.menus.MenuMounts;
import be.isach.ultracosmetics.menu.menus.MenuParticleEffects;
import be.isach.ultracosmetics.menu.menus.MenuPets;
import be.isach.ultracosmetics.menu.menus.MenuPurchase;
import be.isach.ultracosmetics.menu.menus.MenuSuits;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;

import org.bukkit.inventory.ItemStack;

/**
 * Stores menus.
 *
 * @author iSach
 * @since 08-23-2016
 */
public class Menus {

    private final UltraCosmetics ultraCosmetics;
    private final MenuEmotes emotesMenu;
    private final MenuGadgets gadgetsMenu;
    private final MenuHats hatsMenu;
    private final MenuMorphs morphsMenu;
    private final MenuMounts mountsMenu;
    private final MenuParticleEffects effectsMenu;
    private final MenuPets petsMenu;
    private final MenuSuits suitsMenu;
    private final MenuMain mainMenu;

    public Menus(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        this.emotesMenu = new MenuEmotes(ultraCosmetics);
        this.gadgetsMenu = new MenuGadgets(ultraCosmetics);
        this.effectsMenu = new MenuParticleEffects(ultraCosmetics);
        this.hatsMenu = new MenuHats(ultraCosmetics);
        this.morphsMenu = new MenuMorphs(ultraCosmetics);
        this.mountsMenu = new MenuMounts(ultraCosmetics);
        this.petsMenu = new MenuPets(ultraCosmetics);
        this.suitsMenu = new MenuSuits(ultraCosmetics);
        this.mainMenu = new MenuMain(ultraCosmetics);
    }

    public MenuEmotes getEmotesMenu() {
        return emotesMenu;
    }

    public MenuMain getMainMenu() {
        return mainMenu;
    }

    public MenuGadgets getGadgetsMenu() {
        return gadgetsMenu;
    }

    public MenuHats getHatsMenu() {
        return hatsMenu;
    }

    public MenuMorphs getMorphsMenu() {
        return morphsMenu;
    }

    public MenuMounts getMountsMenu() {
        return mountsMenu;
    }

    public MenuParticleEffects getEffectsMenu() {
        return effectsMenu;
    }

    public MenuPets getPetsMenu() {
        return petsMenu;
    }

    public MenuSuits getSuitsMenu() {
        return suitsMenu;
    }

    /**
     * Opens Ammo Purchase Menu.
     */
    public void openAmmoPurchaseMenu(GadgetType type, UltraPlayer player) {
        String itemName = MessageManager.getMessage("Buy-Ammo-Description");
        itemName = itemName.replace("%amount%", String.valueOf(type.getResultAmmoAmount()));
        itemName = itemName.replace("%price%", String.valueOf(type.getAmmoPrice()));
        itemName = itemName.replace("%gadgetname%", type.getName());
        ItemStack display = ItemFactory.create(type.getMaterial(), itemName);
        PurchaseData pd = new PurchaseData();
        pd.setPrice(type.getAmmoPrice());
        pd.setShowcaseItem(display);
        pd.setOnPurchase(() -> {
            player.addAmmo(type, type.getResultAmmoAmount());
            ultraCosmetics.getMenus().getGadgetsMenu().open(player, player.getGadgetsPage());
            player.setGadgetsPage(1);
        });
        pd.setOnCancel(() -> {
            ultraCosmetics.getMenus().getGadgetsMenu().open(player, player.getGadgetsPage());
            player.setGadgetsPage(1);
        });
        MenuPurchase mp = new MenuPurchase(ultraCosmetics, MessageManager.getMessage("Menu.Buy-Ammo.Title"), pd);
        player.getBukkitPlayer().openInventory(mp.getInventory(player));
    }
}
