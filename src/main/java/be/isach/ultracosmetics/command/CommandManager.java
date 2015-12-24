package be.isach.ultracosmetics.command;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sacha on 20/12/15.
 */
public class CommandManager implements CommandExecutor {

    /**
     * List of the registered commands.
     */
    public List<SubCommand> commands = new ArrayList<>();

    public CommandManager(Plugin plugin) {
        plugin.getServer().getPluginCommand("ultracosmetics").setExecutor(this);
        String[] aliases = {"uc", "cosmetics"};
        plugin.getServer().getPluginCommand("ultracosmetics").setAliases(Arrays.asList(aliases));
    }

    /**
     * Registers a command.
     *
     * @param meCommand The command to register.
     */
    public void registerCommand(SubCommand meCommand) {
        commands.add(meCommand);
        Core.log("  Registered subcommand '" + meCommand.aliases[0] + "'");
    }

    public void showHelp(CommandSender commandSender, int page) {
        commandSender.sendMessage("");
        commandSender.sendMessage("§f§l  UltraCosmetics Help (/uc <page>) §8§l(" + page + "/" + getMaxPages() + ")");
        int from = 1;
        if (page > 1)
            from = 8 * (page - 1) + 1;
        int to = 8 * page;
        for (int h = from; h <= to; h++) {
            if (h > commands.size())
                break;
            SubCommand sub = commands.get(h - 1);
            commandSender.sendMessage("    §8|  §7" + sub.getUsage() + "§f§o " + sub.getDescription());
        }
    }

    /**
     * Gets the max amount of pages.
     *
     * @return the maximum amount of pages.
     */
    private int getMaxPages() {
        int max = 8;
        int i = commands.size();
        if (i % max == 0) return i / max;
        double j = i / 8;
        int h = (int) Math.floor(j * 100) / 100;
        return h + 1;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {

        if (!(sender instanceof Player) && !(sender instanceof CommandSender))
            return false;

        if (arguments == null
                || arguments.length == 0) {
            showHelp(sender, 1);
            return true;
        }
        if (arguments.length == 1 && MathUtils.isInteger(arguments[0])) {
            showHelp(sender, Math.max(1, Math.min(Integer.parseInt(arguments[0]), getMaxPages())));
            return true;
        }

        for (SubCommand meCommand : commands)
            if (meCommand.is(arguments[0])) {

                if (!sender.hasPermission(meCommand.getPermission())) {
                    sender.sendMessage(MessageManager.getMessage("No-Permission"));
                    return true;
                }

                if (sender instanceof Player)
                    meCommand.onExePlayer((Player) sender, arguments);
                else
                    meCommand.onExeConsole((ConsoleCommandSender) sender, arguments);
                return true;
            }
        showHelp(sender, 1);

        return true;
    }
}

