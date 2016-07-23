package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.menu.menus.*;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Sacha on 21/12/15.
 */
public class    SubCommandMenu extends SubCommand {

    public SubCommandMenu() {
        super("Opens Specified Menu", "ultracosmetics.command.menu", "/uc menu <menu> [page]", "menu");
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        if (args.length < 2) {
            sender.sendMessage("§c§l/uc menu <menu>\n§c§lAvailable Menus: main, gadgets, particleeffects, pets, mounts, suits, hats, morphs");
            return;
        }

        int page = 1;

        if (args.length > 2 && MathUtils.isInteger(args[2]))
            page = Integer.parseInt(args[2]);

        String s = args[1].toLowerCase();
        if (s.startsWith("g"))
            MenuGadgets_old.openMenu(sender, page);
        else if (s.startsWith("pa"))
            MenuParticleEffects.openMenu(sender, page);
        else if (s.startsWith("pe"))
            MenuPets.openMenu(sender, page);
        else if (s.startsWith("h"))
            MenuHats.openMenu(sender, page);
        else if (s.startsWith("s"))
            MenuSuits.openMenu(sender, page);
        else if (s.startsWith("mor"))
            MenuMorphs.openMenu(sender, page);
        else if (s.startsWith("mou"))
            MenuMounts.openMenu(sender, page);
        else if (s.startsWith("ma"))
            MenuMain.openMenu(sender);
        else if (s.startsWith("e"))
            MenuEmotes.openMenu(sender, page);
        else if (s.startsWith("b")) {
            sender.closeInventory();
            UltraCosmetics.getCustomPlayer(sender).openKeyPurchaseMenu();
        } else
            sender.sendMessage("§c§l/uc menu <menu>\n§c§lInvalid Menu\n§c§lAvailable Menus: main,"
                    + (UltraCosmetics.getInstance().areTreasureChestsEnabled() ? " buykey," : "")
                    + (SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled") ? " renamepet," : "") +
                    " gadgets, particleeffects, pets, mounts, suits, hats, morphs, emotes");
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        notAllowed(sender);
    }
}

