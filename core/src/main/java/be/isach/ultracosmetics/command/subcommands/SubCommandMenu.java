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
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
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
            sender.sendMessage(getMenuList());
            return;
        }

        int page = 1;

        if (args.length > 2 && MathUtils.isInteger(args[2])) {
            page = Integer.parseInt(args[2]);
        }

        String s = args[1].toLowerCase();

        Menus menus = ultraCosmetics.getMenus();

        UltraPlayer ultraPlayer = ultraCosmetics.getPlayerManager().getUltraPlayer(sender);

        if (s.startsWith("g") && Category.GADGETS.isEnabled()) {
            menus.getGadgetsMenu().open(ultraPlayer, page);
        } else if ((s.startsWith("pa") || s.startsWith("ef")) && Category.EFFECTS.isEnabled()) {
            menus.getEffectsMenu().open(ultraPlayer, page);
        } else if (s.startsWith("pe") && Category.PETS.isEnabled()) {
            menus.getPetsMenu().open(ultraPlayer, page);
        } else if (s.startsWith("h") && Category.HATS.isEnabled()) {
            menus.getHatsMenu().open(ultraPlayer, page);
        } else if (s.startsWith("s") && Category.SUITS.isEnabled()) {
            menus.getSuitsMenu().open(ultraPlayer, page);
        } else if (s.startsWith("mor") && Category.MORPHS.isEnabled()) {
            menus.getMorphsMenu().open(ultraPlayer, page);
        } else if (s.startsWith("mou") && Category.MOUNTS.isEnabled()) {
            menus.getMountsMenu().open(ultraPlayer, page);
        } else if (s.startsWith("ma")) {
            menus.getMainMenu().open(ultraPlayer);
        } else if (s.startsWith("e") && Category.EMOTES.isEnabled()) {
            menus.getEmotesMenu().open(ultraPlayer, page);
        } else if (s.startsWith("b") && UltraCosmeticsData.get().areTreasureChestsEnabled()) {
            sender.closeInventory();
            ultraCosmetics.getPlayerManager().getUltraPlayer(sender).openKeyPurchaseMenu();
        } else if (s.startsWith("r") && SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled")) {
            if (SettingsManager.getConfig().getBoolean("Pets-Rename.Permission-Required")) {
                if (sender.hasPermission("ultracosmetics.pets.rename")) {
                    if (ultraPlayer.getCurrentPet() != null) {
                        menus.getPetsMenu().renamePet(ultraPlayer);
                    } else {
                        sender.sendMessage(MessageManager.getMessage("Active-Pet-Needed"));
                    }
                }
            } else if (ultraPlayer.getCurrentPet() != null) {
                menus.getPetsMenu().renamePet(ultraPlayer);
            } else {
                sender.sendMessage(MessageManager.getMessage("Active-Pet-Needed"));
            }
        } else {
            sender.sendMessage(getMenuList());
        }
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String[] args) {
        notAllowed(sender);
    }

    private String getMenuList() {
        StringBuilder menuList = new StringBuilder(ChatColor.RED + "" + ChatColor.BOLD + "/uc menu <menu>\n" + ChatColor.RED + "" + ChatColor.BOLD + "Invalid Menu\n"
                + ChatColor.RED + "" + ChatColor.BOLD + "Available Menus: main," + (UltraCosmeticsData.get().areTreasureChestsEnabled() ? " buykey," : "")
                + (SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled") ? " renamepet," : ""));
        for (Category category : Category.enabled()) {
            menuList.append(" ").append(category.name().toLowerCase()).append(",");
        }
        return menuList.substring(0, menuList.length() - 1);
    }
}

