 package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.command.ITabCompletable;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.command.UCTabCompleter;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.mysql.MySqlConnectionManager;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Give {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 *
 * @author iSach
 * @since 12-21-2015
 */
@SuppressWarnings("deprecation")
public class SubCommandGive extends SubCommand {

    public SubCommandGive(UltraCosmetics ultraCosmetics) {
        super("Gives Ammo/Key.", "ultracosmetics.command.give", "/uc give <player> <key|ammo>", ultraCosmetics, "give");
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        common(sender, args);
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        common(sender, args);
    }

    protected void common(CommandSender sender, String... args) {
        if (args.length < 4) {
            if (args.length == 3) {
                if (args[2].startsWith("k"))
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Usage. /uc give <player> key <amount>");
                else if (args[2].startsWith("a"))
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Usage. /uc give <player> ammo <gadget> <amount>");
            } else
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Usage. " + getUsage());
            return;
        }

        OfflinePlayer receiver;

        String keyorammo = args[2].toLowerCase();
        if (keyorammo.startsWith("k")) {
            receiver = Bukkit.getPlayer(args[1]);
            if (receiver == null
                    && Bukkit.getOfflinePlayer(args[1]) == null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Player " + args[1] + " not found and has never come!");
                return;
            }
            if (Bukkit.getPlayer(args[1]) == null || (!Bukkit.getPlayer(args[1]).isOnline() && Bukkit.getOfflinePlayer(args[1]) != null)) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Player " + args[1] + " is offline.");

                receiver = Bukkit.getOfflinePlayer(args[1]);
            }

            if (receiver == null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Player " + args[1] + " not found and has never come!");
                return;
            }

            if (!MathUtils.isInteger(args[3])) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + args[3] + " isn't a number!");
                return;
            }

            int keys = Math.max(0, Math.min(Integer.MAX_VALUE, Integer.parseInt(args[3])));

            for (int i = 0; i < keys; i++)
                addKey(receiver);
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + keys + " treasure keys given to " + receiver.getName());

        } else if (keyorammo.startsWith("a")) {
            if (args.length < 5) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Usage. /uc give <player> ammo <gadget> <amount>");
                return;
            }
            receiver = Bukkit.getPlayer(args[1]);
            if (receiver == null
                    && Bukkit.getOfflinePlayer(args[1]) == null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Player " + args[1] + " not found!");
                return;
            }
            if (Bukkit.getPlayer(args[1]) == null || (!Bukkit.getPlayer(args[1]).isOnline() && Bukkit.getOfflinePlayer(args[1]) != null)) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Player " + args[1] + " is offline.");

                receiver = Bukkit.getOfflinePlayer(args[1]);
            }

            if (receiver == null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Player " + args[1] + " not found!");
                return;
            }
            GadgetType gadgetType;
            try {
                gadgetType = GadgetType.valueOf(args[3].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage(MessageManager.getMessage("Invalid-Gadget"));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < GadgetType.enabled().size(); i++)
                    sb.append(GadgetType.enabled().get(i).toString().toLowerCase() + ((i != GadgetType.enabled().size() - 1) ? ChatColor.WHITE + "" + ChatColor.BOLD + ", " + ChatColor.RED : ""));
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Gadget Types: " + ChatColor.RED + sb.toString());
                return;
            }

            if(gadgetType == null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "No gadget with this name!");
                return;
            }

            if (!gadgetType.isEnabled()) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "This gadget isn't enabled!");
                return;
            }
            if (!MathUtils.isInteger(args[4])) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + args[4] + " isn't a number!");
                return;
            }
            int ammo = Math.max(0, Math.min(Integer.MAX_VALUE, Integer.parseInt(args[4])));
            addAmmo(gadgetType, receiver, ammo);
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + ammo + " " + gadgetType.toString().toLowerCase() + " ammo given to " + receiver.getName());
        } else {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Usage. /uc give <key|ammo> <amount> <player>");
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

    @Override
    public List<String> getTabCompleteSuggestion(CommandSender sender, String... args) {
        //uc give <player> <key> <amount>
        //uc give <player> <ammo> <gadget> <amount>
        List<String> tabSuggestion = new ArrayList<>();

        // Check if the root argument doesn't match our command's alias, or if no additional arguments are given (shouldn't happen)
        if(!Arrays.stream(getAliases()).anyMatch(args[0]::equals) || args.length < 2)
            return tabSuggestion;

        else if(args.length == 2) { // Tab-completing first argument: <player>
            return UCTabCompleter.GetOnlinePlayers(sender);
        }

        else if(args.length == 3) { // Tab-completing second argument: <key|ammo>
            tabSuggestion.add("ammo");
            tabSuggestion.add("key");
            return tabSuggestion;
        }

        else if(args.length == 4 && args[3].equalsIgnoreCase("ammo")) { // Tab-completing third argument <gadget>
            for(GadgetType gadgetType : GadgetType.enabled()) {
                tabSuggestion.add(gadgetType.getConfigName());
            }
            return tabSuggestion;
        }

        else {
            return tabSuggestion;
        }
    }
}
