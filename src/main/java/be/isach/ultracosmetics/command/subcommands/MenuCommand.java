package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.manager.*;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Sacha on 21/12/15.
 */
public class MenuCommand extends SubCommand {


    public MenuCommand() {
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
            GadgetManager.openMenu(sender, page);
        else if (s.startsWith("pa"))
            ParticleEffectManager.openMenu(sender, page);
        else if (s.startsWith("pe"))
            PetManager.openMenu(sender, page);
        else if (s.startsWith("h"))
            HatManager.openMenu(sender, page);
        else if (s.startsWith("s"))
            SuitManager.openMenu(sender, page);
        else if (s.startsWith("mor"))
            MorphManager.openMenu(sender, page);
        else if (s.startsWith("mou"))
            MountManager.openMenu(sender, page);
        else if (s.startsWith("ma"))
            MainMenuManager.openMenu(sender);
        else if (s.startsWith("b")) {
            sender.closeInventory();
            Core.getCustomPlayer(sender).openKeyPurchaseMenu();
        } else
            sender.sendMessage("§c§l/uc menu <menu>\n§c§lInvalid Menu\n§c§lAvailable Menus: main,"
                    + (Core.treasureChestsEnabled() ? " buykey," : "")
                    + (SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled") ? " renamepet," : "") +
                    " gadgets, particleeffects, pets, mounts, suits, hats, morphs");
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        notAllowed(sender);
    }
}

