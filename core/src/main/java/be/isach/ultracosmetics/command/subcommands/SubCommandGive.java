<<<<<<< HEAD
package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.mysql.MySqlConnectionManager;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Sacha on 21/12/15.
 */
public class SubCommandGive extends SubCommand {

    public SubCommandGive(UltraCosmetics ultraCosmetics) {
        super("Gives Ammo/Key.", "ultracosmetics.command.give", "/uc give <key|ammo> <amount> [player]", ultraCosmetics, "give");
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

        OfflinePlayer receiver = sender;

        String arg1 = args[1].toLowerCase();
        if (arg1.startsWith("k")) { // Giving key.
            if (args.length > 3) {
                receiver = Bukkit.getPlayer(args[3]);
                if (receiver == null
                        && Bukkit.getOfflinePlayer(args[3]) == null) {
                    sender.sendMessage("  §c§lPlayer " + args[3] + " not found!");
                    return;
                }
                if (Bukkit.getOfflinePlayer(args[3]) != null
                        && Bukkit.getOfflinePlayer(args[3]).hasPlayedBefore()
                        && Bukkit.getPlayer(args[3]) == null) {
                    sender.sendMessage("  §c§lPlayer " + args[3] + " is offline.");

                    receiver = Bukkit.getOfflinePlayer(args[3]);
                }

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
                addKey(receiver);

            sender.sendMessage("  §c§l" + keys + " treasure keys given to " + receiver.getName());
            return;

        } else if (arg1.startsWith("a")) { // Giving ammo. /uc give ammo <type> <amount> [player]
            if (args.length < 4) {
                sender.sendMessage("  §c§lIncorrect Usage. /uc give ammo <gadget> <amount> [player]");
                return;
            }
            if (args.length > 4) {
                receiver = Bukkit.getPlayer(args[4]);
                if (receiver == null
                        && Bukkit.getOfflinePlayer(args[4]) == null) {
                    sender.sendMessage("  §c§lPlayer " + args[4] + " not found and has never come!");
                    return;
                }
                if (Bukkit.getOfflinePlayer(args[4]) != null
                        && Bukkit.getOfflinePlayer(args[4]).hasPlayedBefore()
                        && Bukkit.getPlayer(args[4]) == null) {
                    sender.sendMessage("  §c§lPlayer " + args[4] + " is offline.");

                    receiver = Bukkit.getOfflinePlayer(args[4]);
                }

                if (receiver == null) {
                    sender.sendMessage("  §c§lPlayer " + args[4] + " not found and has never come!");
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
            addAmmo(gadgetType, receiver, ammo);
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

        OfflinePlayer receiver;

        String arg1 = args[1].toLowerCase();
        if (arg1.startsWith("k")) {
            receiver = Bukkit.getPlayer(args[3]);
            if (receiver == null
                    && Bukkit.getOfflinePlayer(args[3]) == null) {
                sender.sendMessage("  §c§lPlayer " + args[3] + " not found and has never come!");
                return;
            }
            if (Bukkit.getOfflinePlayer(args[3]) != null) {
                sender.sendMessage("  §c§lPlayer " + args[3] + " is offline.");

                receiver = Bukkit.getOfflinePlayer(args[3]);
            }

            if (receiver == null) {
                sender.sendMessage("  §c§lPlayer " + args[3] + " not found and has never come!");
                return;
            }

            if (!MathUtils.isInteger(args[2])) {
                sender.sendMessage("  §c§l" + args[2] + " isn't a number!");
                return;
            }

            int keys = Math.max(0, Math.min(Integer.MAX_VALUE, Integer.parseInt(args[2])));

            for (int i = 0; i < keys; i++)
                addKey(receiver);
            sender.sendMessage("  §c§l" + keys + " treasure keys given to " + receiver.getName());
            return;

        } else if (arg1.startsWith("a")) {
            if (args.length < 5) {
                sender.sendMessage("  §c§lIncorrect Usage. /uc give ammo <gadget> <amount> <player>");
                return;
            }
            receiver = Bukkit.getPlayer(args[4]);
            if (receiver == null
                    && Bukkit.getOfflinePlayer(args[4]) == null) {
                sender.sendMessage("  §c§lPlayer " + args[4] + " not found!");
                return;
            }
            if (Bukkit.getOfflinePlayer(args[4]) != null) {
                sender.sendMessage("  §c§lPlayer " + args[4] + " is offline.");

                receiver = Bukkit.getOfflinePlayer(args[4]);
            }

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
            addAmmo(gadgetType, receiver, ammo);
            sender.sendMessage("  §c§l" + ammo + " " + gadgetType.toString().toLowerCase() + " ammo given to " + receiver.getName());
            return;
        } else {
            sender.sendMessage("  §c§lIncorrect Usage. /uc give <key|ammo> <amount> <player>");
            return;
        }
    }

    private void addKey(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null || offlinePlayer.getUniqueId() == null)
            return;
        if (offlinePlayer instanceof Player)
            getUltraCosmetics().getPlayerManager().getUltraPlayer((Player) offlinePlayer).addKey();
        else {
            if (UltraCosmeticsData.get().usingFileStorage())
                SettingsManager.getData(offlinePlayer.getUniqueId()).set("Keys", getKeys(offlinePlayer.getUniqueId()) + 1);
            else
                getUltraCosmetics().getMySqlConnectionManager().getSqlUtils().addKey(MySqlConnectionManager.INDEXS.get(offlinePlayer.getUniqueId()));
        }
    }

    private void addAmmo(GadgetType gadgetType, OfflinePlayer receiver, int ammo) {
        if (receiver == null || receiver.getUniqueId() == null)
            return;
        if (receiver instanceof Player)
            getUltraCosmetics().getPlayerManager().getUltraPlayer((Player) receiver).addAmmo(gadgetType.toString().toLowerCase(), ammo);
        else {
            if (UltraCosmeticsData.get().usingFileStorage())
                SettingsManager.getData(receiver.getUniqueId()).set("Ammo." + gadgetType.toString().toLowerCase(),
                        ((int) SettingsManager.getData(receiver.getUniqueId()).get("Ammo." + gadgetType.toString().toLowerCase())) + ammo);
            else
                getUltraCosmetics().getMySqlConnectionManager().getSqlUtils().addAmmo(MySqlConnectionManager.INDEXS.get(receiver.getUniqueId()), gadgetType.toString().toLowerCase(), ammo);
        }
    }

    private int getKeys(UUID uuid) {
        return UltraCosmeticsData.get().usingFileStorage() ? (int) SettingsManager.getData(uuid).get("Keys") : getUltraCosmetics().getMySqlConnectionManager().getSqlUtils().getKeys(MySqlConnectionManager.INDEXS.get(uuid));
    }
}
=======
package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.mysql.MySqlConnectionManager;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Sacha on 21/12/15.
 */
public class SubCommandGive extends SubCommand {

    public SubCommandGive(UltraCosmetics ultraCosmetics) {
        super("Gives Ammo/Key.", "ultracosmetics.command.give", "/uc give <key|ammo> <amount> [player]", ultraCosmetics, "give");
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        if (args.length < 3) {
            if (args.length == 2) {
                if (args[1].startsWith("k"))
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Incorrect Usage. /uc give key <amount> [player]");
                else if (args[1].startsWith("a"))
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Incorrect Usage. /uc give ammo <gadget> <amount> [player]");
            } else
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Incorrect Usage. " + getUsage());
            return;
        }

        OfflinePlayer receiver = sender;

        String arg1 = args[1].toLowerCase();
        if (arg1.startsWith("k")) { // Giving key.
            if (args.length > 3) {
                receiver = Bukkit.getPlayer(args[3]);
                if (receiver == null
                        && Bukkit.getOfflinePlayer(args[3]) == null) {
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Player " + args[3] + " not found!");
                    return;
                }
                if (Bukkit.getOfflinePlayer(args[3]) != null
                        && Bukkit.getOfflinePlayer(args[3]).hasPlayedBefore()
                        && Bukkit.getPlayer(args[3]) == null) {
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Player " + args[3] + " is offline.");

                    receiver = Bukkit.getOfflinePlayer(args[3]);
                }

                if (receiver == null) {
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Player " + args[3] + " not found!");
                    return;
                }
            }
            if (!MathUtils.isInteger(args[2])) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + args[2] + " isn't a number!");
                return;
            }

            int keys = Math.max(0, Math.min(Integer.MAX_VALUE, Integer.parseInt(args[2])));

            for (int i = 0; i < keys; i++)
                addKey(receiver);

            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + keys + " treasure keys given to " + receiver.getName());
            return;

        } else if (arg1.startsWith("a")) { // Giving ammo. /uc give ammo <type> <amount> [player]
            if (args.length < 4) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Incorrect Usage. /uc give ammo <gadget> <amount> [player]");
                return;
            }
            if (args.length > 4) {
                receiver = Bukkit.getPlayer(args[4]);
                if (receiver == null
                        && Bukkit.getOfflinePlayer(args[4]) == null) {
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Player " + args[4] + " not found and has never come!");
                    return;
                }
                if (Bukkit.getOfflinePlayer(args[4]) != null
                        && Bukkit.getOfflinePlayer(args[4]).hasPlayedBefore()
                        && Bukkit.getPlayer(args[4]) == null) {
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Player " + args[4] + " is offline.");

                    receiver = Bukkit.getOfflinePlayer(args[4]);
                }

                if (receiver == null) {
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Player " + args[4] + " not found and has never come!");
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
                    sb.append(GadgetType.enabled().get(i).toString().toLowerCase() + ((i != GadgetType.enabled().size() - 1) ? ChatColor.WHITE + "" + ChatColor.BOLD + ", " + ChatColor.RED : ""));
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Gadget Types: " + ChatColor.RED + sb.toString());
                return;
            }

            if (!gadgetType.isEnabled()) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  This gadget isn't enabled!");
                return;
            }
            if (!MathUtils.isInteger(args[3])) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  " + args[3] + " isn't a number!");
                return;
            }
            int ammo = Math.max(0, Math.min(Integer.MAX_VALUE, Integer.parseInt(args[3])));
            addAmmo(gadgetType, receiver, ammo);
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  " + ammo + " " + gadgetType.toString().toLowerCase() + " ammo given to " + receiver.getName());
            return;
        } else {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Incorrect Usage. " + getUsage());
            return;
        }
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        if (args.length < 4) {
            if (args.length == 2) {
                if (args[1].startsWith("k"))
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Incorrect Usage. /uc give key <amount> <player>");
                else if (args[1].startsWith("a"))
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Incorrect Usage. /uc give ammo <gadget> <amount> <player>");
            } else
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Incorrect Usage. " + getUsage());
            return;
        }

        OfflinePlayer receiver;

        String arg1 = args[1].toLowerCase();
        if (arg1.startsWith("k")) {
            receiver = Bukkit.getPlayer(args[3]);
            if (receiver == null
                    && Bukkit.getOfflinePlayer(args[3]) == null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Player " + args[3] + " not found and has never come!");
                return;
            }
            if (Bukkit.getOfflinePlayer(args[3]) != null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Player " + args[3] + " is offline.");

                receiver = Bukkit.getOfflinePlayer(args[3]);
            }

            if (receiver == null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Player " + args[3] + " not found and has never come!");
                return;
            }

            if (!MathUtils.isInteger(args[2])) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  " + args[2] + " isn't a number!");
                return;
            }

            int keys = Math.max(0, Math.min(Integer.MAX_VALUE, Integer.parseInt(args[2])));

            for (int i = 0; i < keys; i++)
                addKey(receiver);
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  " + keys + " treasure keys given to " + receiver.getName());
            return;

        } else if (arg1.startsWith("a")) {
            if (args.length < 5) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Incorrect Usage. /uc give ammo <gadget> <amount> <player>");
                return;
            }
            receiver = Bukkit.getPlayer(args[4]);
            if (receiver == null
                    && Bukkit.getOfflinePlayer(args[4]) == null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Player " + args[4] + " not found!");
                return;
            }
            if (Bukkit.getOfflinePlayer(args[4]) != null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Player " + args[4] + " is offline.");

                receiver = Bukkit.getOfflinePlayer(args[4]);
            }

            if (receiver == null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Player " + args[4] + " not found!");
                return;
            }
            GadgetType gadgetType;
            try {
                gadgetType = GadgetType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Gadget"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < GadgetType.enabled().size(); i++)
                    sb.append(GadgetType.enabled().get(i).toString().toLowerCase() + ((i != GadgetType.enabled().size() - 1) ? ChatColor.WHITE + "" + ChatColor.BOLD +", " + ChatColor.RED : ""));
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Gadget Types: " + ChatColor.RED + "" + sb.toString());
                return;
            }

            if (!gadgetType.isEnabled()) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  This gadget isn't enabled!");
                return;
            }
            if (!MathUtils.isInteger(args[3])) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  " + args[3] + " isn't a number!");
                return;
            }
            int ammo = Math.max(0, Math.min(Integer.MAX_VALUE, Integer.parseInt(args[3])));
            addAmmo(gadgetType, receiver, ammo);
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  " + ammo + " " + gadgetType.toString().toLowerCase() + " ammo given to " + receiver.getName());
            return;
        } else {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "  Incorrect Usage. /uc give <key|ammo> <amount> <player>");
            return;
        }
    }

    private void addKey(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null || offlinePlayer.getUniqueId() == null)
            return;
        if (offlinePlayer instanceof Player)
            getUltraCosmetics().getPlayerManager().getUltraPlayer((Player) offlinePlayer).addKey();
        else {
            if (UltraCosmeticsData.get().usingFileStorage())
                SettingsManager.getData(offlinePlayer.getUniqueId()).set("Keys", getKeys(offlinePlayer.getUniqueId()) + 1);
            else
                getUltraCosmetics().getMySqlConnectionManager().getSqlUtils().addKey(MySqlConnectionManager.INDEXS.get(offlinePlayer.getUniqueId()));
        }
    }

    private void addAmmo(GadgetType gadgetType, OfflinePlayer receiver, int ammo) {
        if (receiver == null || receiver.getUniqueId() == null)
            return;
        if (receiver instanceof Player)
            getUltraCosmetics().getPlayerManager().getUltraPlayer((Player) receiver).addAmmo(gadgetType.toString().toLowerCase(), ammo);
        else {
            if (UltraCosmeticsData.get().usingFileStorage())
                SettingsManager.getData(receiver.getUniqueId()).set("Ammo." + gadgetType.toString().toLowerCase(),
                        ((int) SettingsManager.getData(receiver.getUniqueId()).get("Ammo." + gadgetType.toString().toLowerCase())) + ammo);
            else
                getUltraCosmetics().getMySqlConnectionManager().getSqlUtils().addAmmo(MySqlConnectionManager.INDEXS.get(receiver.getUniqueId()), gadgetType.toString().toLowerCase(), ammo);
        }
    }

    private int getKeys(UUID uuid) {
        return UltraCosmeticsData.get().usingFileStorage() ? (int) SettingsManager.getData(uuid).get("Keys") : getUltraCosmetics().getMySqlConnectionManager().getSqlUtils().getKeys(MySqlConnectionManager.INDEXS.get(uuid));
    }
}
>>>>>>> refs/remotes/origin/master
