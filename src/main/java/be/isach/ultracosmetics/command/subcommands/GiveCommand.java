package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetType;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Sacha on 21/12/15.
 */
public class GiveCommand extends SubCommand {

    public GiveCommand() {
        super("Gives Ammo/Key.", "ultracosmetics.command.give", "/uc give <key|ammo> <amount> [player]", "give");
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        if (args.length < 3) {
            if (args.length == 2) {
                if (args[1].startsWith("k"))
                    sender.sendMessage("  §c§lIncorrect Usage. /uc give key <amount> [player]");
                else if (args[1].startsWith("a"))
                    sender.sendMessage("  §c§lIncorrect Usage. /uc give ammo <gadget> <amount> [player]");
            } else
                sender.sendMessage("  §c§lIncorrect Usage. " + getUsage());
            return;
        }

        Player receiver = sender;

        String arg1 = args[1].toLowerCase();
        if (arg1.startsWith("k")) { // Giving key.
            if (args.length > 3) {
                receiver = Bukkit.getPlayer(args[3]);
                if (receiver == null) {
                    sender.sendMessage("  §c§lPlayer " + args[3] + " not found!");
                    return;
                }
            }

            if (!MathUtils.isInteger(args[2])) {
                sender.sendMessage("  §c§l" + args[2] + " isn't a number!");
                return;
            }

            int keys = Math.max(0, Math.min(Integer.MAX_VALUE, Integer.parseInt(args[2])));

            for (int i = 0; i < keys; i++)
                Core.getPlayerManager().getCustomPlayer(receiver).addKey();
            sender.sendMessage("  §c§l" + keys + " treasure keys given to " + receiver.getName());
            return;

        } else if (arg1.startsWith("a")) { // Giving ammo. /uc give ammo <type> <amount> [player]
            if (args.length < 4) {
                sender.sendMessage("  §c§lIncorrect Usage. /uc give ammo <gadget> <amount> [player]");
                return;
            }
            if (args.length > 4) {
                receiver = Bukkit.getPlayer(args[4]);
                if (receiver == null) {
                    sender.sendMessage("  §c§lPlayer " + args[4] + " not found!");
                    return;
                }
            }
            GadgetType gadgetType;
            try {
                gadgetType = GadgetType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Gadget"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < GadgetType.enabled().size(); i++)
                    sb.append(GadgetType.enabled().get(i).toString().toLowerCase() + ((i != GadgetType.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lGadget Types: §c" + sb.toString());
                return;
            }

            if (!gadgetType.isEnabled()) {
                sender.sendMessage("  §c§lThis gadget isn't enabled!");
                return;
            }
            if (!MathUtils.isInteger(args[3])) {
                sender.sendMessage("  §c§l" + args[3] + " isn't a number!");
                return;
            }
            int ammo = Math.max(0, Math.min(Integer.MAX_VALUE, Integer.parseInt(args[3])));
            Core.getPlayerManager().getCustomPlayer(receiver).addAmmo(gadgetType.toString().toLowerCase(), ammo);
            sender.sendMessage("  §c§l" + ammo + " " + gadgetType.toString().toLowerCase() + " ammo given to " + receiver.getName());
            return;
        } else {
            sender.sendMessage("  §c§lIncorrect Usage. " + getUsage());
            return;
        }
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        if (args.length < 4) {
            if (args.length == 2) {
                if (args[1].startsWith("k"))
                    sender.sendMessage("  §c§lIncorrect Usage. /uc give key <amount> <player>");
                else if (args[1].startsWith("a"))
                    sender.sendMessage("  §c§lIncorrect Usage. /uc give ammo <gadget> <amount> <player>");
            } else
                sender.sendMessage("  §c§lIncorrect Usage. " + getUsage());
            return;
        }

        Player receiver;

        String arg1 = args[1].toLowerCase();
        if (arg1.startsWith("k")) {
            receiver = Bukkit.getPlayer(args[3]);
            if (receiver == null) {
                sender.sendMessage("  §c§lPlayer " + args[3] + " not found!");
                return;
            }

            if (!MathUtils.isInteger(args[2])) {
                sender.sendMessage("  §c§l" + args[2] + " isn't a number!");
                return;
            }

            int keys = Math.max(0, Math.min(Integer.MAX_VALUE, Integer.parseInt(args[2])));

            for (int i = 0; i < keys; i++)
                Core.getPlayerManager().getCustomPlayer(receiver).addKey();
            sender.sendMessage("  §c§l" + keys + " treasure keys given to " + receiver.getName());
            return;

        } else if (arg1.startsWith("a")) {
            if (args.length < 5) {
                sender.sendMessage("  §c§lIncorrect Usage. /uc give ammo <gadget> <amount> <player>");
                return;
            }
            receiver = Bukkit.getPlayer(args[4]);
            if (receiver == null) {
                sender.sendMessage("  §c§lPlayer " + args[4] + " not found!");
                return;
            }
            GadgetType gadgetType;
            try {
                gadgetType = GadgetType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Gadget"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < GadgetType.enabled().size(); i++)
                    sb.append(GadgetType.enabled().get(i).toString().toLowerCase() + ((i != GadgetType.enabled().size() - 1) ? "§f§l, §c" : ""));
                sender.sendMessage("§c§lGadget Types: §c" + sb.toString());
                return;
            }

            if (!gadgetType.isEnabled()) {
                sender.sendMessage("  §c§lThis gadget isn't enabled!");
                return;
            }
            if (!MathUtils.isInteger(args[3])) {
                sender.sendMessage("  §c§l" + args[3] + " isn't a number!");
                return;
            }
            int ammo = Math.max(0, Math.min(Integer.MAX_VALUE, Integer.parseInt(args[3])));
            Core.getPlayerManager().getCustomPlayer(receiver).addAmmo(gadgetType.toString().toLowerCase(), ammo);
            sender.sendMessage("  §c§l" + ammo + " " + gadgetType.toString().toLowerCase() + " ammo given to " + receiver.getName());
            return;
        } else {
            sender.sendMessage("  §c§lIncorrect Usage. /uc give <key|ammo> <amount> <player>");
            return;
        }
    }
}
