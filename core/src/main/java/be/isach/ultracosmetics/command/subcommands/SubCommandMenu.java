package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.menu.Menus;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;

import java.util.StringJoiner;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Menu {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 *
 * @author iSach
 * @since 12-21-2015
 */
public class SubCommandMenu extends SubCommand {

    public SubCommandMenu(UltraCosmetics ultraCosmetics) {
        super("menu", "Opens Specified Menu", "ultracosmetics.command.menu", "/uc menu <menu> [page]", ultraCosmetics);
    }

    @Override
    protected void onExePlayer(Player sender, String[] args) {
        if (!SettingsManager.isAllowedWorld(sender.getWorld())) {
            sender.sendMessage(MessageManager.getMessage("World-Disabled"));
            return;
        }

        if (args.length < 2) {
            sendMenuList(sender);
            return;
        }

        int page = 1;

        if (args.length > 2 && MathUtils.isInteger(args[2])) {
            page = Integer.parseInt(args[2]);
        }

        String s = args[1].toLowerCase();

        Menus menus = ultraCosmetics.getMenus();

        UltraPlayer ultraPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(sender);

        if (s.startsWith("ma")) {
            menus.getMainMenu().open(ultraPlayer);
            return;
        } else if (s.startsWith("r") && SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled")) {
            if (SettingsManager.getConfig().getBoolean("Pets-Rename.Permission-Required") && !sender.hasPermission("ultracosmetics.pets.rename")) {
                error(sender, "You don't have permission.");
                return;
            }
            if (ultraPlayer.getCurrentPet() == null) {
                sender.sendMessage(MessageManager.getMessage("Active-Pet-Needed"));
                return;
            }
            menus.getPetsMenu().renamePet(ultraPlayer);
            return;
        } else if (s.startsWith("b") && UltraCosmeticsData.get().areTreasureChestsEnabled()) {
            sender.closeInventory();
            ultraCosmetics.getPlayerManager().getUltraPlayer(sender).openKeyPurchaseMenu();
            return;
        }
        Category cat = Category.fromString(s);
        if (cat == null) {
            sendMenuList(sender);
            return;
        }
        if (!cat.isEnabled()) {
            error(sender, "That menu is disabled.");
            return;
        }
        switch(cat) {
        case EFFECTS:
            menus.getEffectsMenu().open(ultraPlayer, page);
            break;
        case EMOTES:
            menus.getEmotesMenu().open(ultraPlayer, page);
            break;
        case GADGETS:
            menus.getGadgetsMenu().open(ultraPlayer, page);
            break;
        case HATS:
            menus.getHatsMenu().open(ultraPlayer, page);
            break;
        case MORPHS:
            menus.getMorphsMenu().open(ultraPlayer, page);
            break;
        case MOUNTS:
            menus.getMountsMenu().open(ultraPlayer, page);
            break;
        case PETS:
            menus.getPetsMenu().open(ultraPlayer, page);
            break;
        case SUITS:
            menus.getSuitsMenu().open(ultraPlayer, page);
            break;
        }
    }

    @Override
    protected void onExeNotPlayer(CommandSender sender, String[] args) {
        notAllowed(sender);
    }

    private void sendMenuList(CommandSender sender) {
        error(sender, "Invalid menu, available menus are:");
        StringJoiner menuList = new StringJoiner(", ");
        menuList.add("main");
        if (UltraCosmeticsData.get().areTreasureChestsEnabled()) {
            menuList.add("buykey");
        }
        if (SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled")) {
            menuList.add("renamepet");
        }
        for (Category category : Category.enabled()) {
            menuList.add(category.name().toLowerCase());
        }
        error(sender, menuList.toString());
    }
}

