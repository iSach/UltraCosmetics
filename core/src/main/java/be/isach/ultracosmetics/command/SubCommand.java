package be.isach.ultracosmetics.command;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by Sacha on 20/12/15.
 * <p/>
 */
public abstract class SubCommand {

    String[] aliases;
    String description, permission, usage;
    private UltraCosmetics ultraCosmetics;

    public SubCommand(String description, String permission, String usage, UltraCosmetics ultraCosmetics, String... aliases) {
        this.aliases = aliases;
        this.description = description;
        this.permission = permission;
        this.usage = usage;
        this.ultraCosmetics = ultraCosmetics;
    }

    /**
     * Checks if the given String is an alias of this command.
     *
     * @param arg The String to check.
     * @return {@code true} if the String is an alias.
     */
    public boolean is(String arg) {
        return Arrays.asList(aliases).contains(arg.toLowerCase());
    }

    /**
     * Get the usage message of this command.
     *
     * @return The usage of this command.
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Get the description of this command.
     *
     * @return The description of this command.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the permission of this command.
     *
     * @return The permission of this command.
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Called when the sub command is executed by a player.
     *
     * @param sender The player who executed the command.
     * @param args   The args of the command. (Includes the subcommand alias).
     */
    protected abstract void onExePlayer(Player sender, String... args);

    /**
     * Called when the sub command is executed by console.
     *
     * @param sender The console sender who executed the command.
     * @param args   The args of the command. (Includes the subcommand alias).
     */
    protected abstract void onExeConsole(ConsoleCommandSender sender, String... args);

    /**
     * Sent when player doesn't have permission to the command.
     *
     * @param commandSender The sender who hasn't got the required permission.
     */
    protected void notAllowed(CommandSender commandSender) {
        commandSender.sendMessage(MessageManager.getMessage("Not-Allowed-From-Console"));
    }

    public UltraCosmetics getUltraCosmetics() {
        return ultraCosmetics;
    }
}
