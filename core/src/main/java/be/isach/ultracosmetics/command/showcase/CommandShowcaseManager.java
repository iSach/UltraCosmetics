package be.isach.ultracosmetics.command.showcase;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.command.showcase.subcommands.SubCommandShowcaseClear;
import be.isach.ultracosmetics.command.showcase.subcommands.SubCommandShowcaseToggle;
import be.isach.ultracosmetics.command.showcase.subcommands.SubCommandShowcaseRenamePet;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Command manager for NPC Showcases.
 *
 * @author SinfulMentality
 * @since 04-24-2020
 */
public class CommandShowcaseManager implements CommandExecutor {
	/**
	 * List of the registered commands.
	 */
	private List<SubCommand> commands = new ArrayList<>();

	private UltraCosmetics ultraCosmetics;

	public CommandShowcaseManager(UltraCosmetics ultraCosmetics) {
		this.ultraCosmetics = ultraCosmetics;
		this.ultraCosmetics.getServer().getPluginCommand("ultracosmeticsshowcase").setExecutor(this);
		this.ultraCosmetics.getServer().getPluginCommand("ultracosmeticsshowcase").setTabCompleter(new UCShowcaseTabCompleter(ultraCosmetics));
		String[] aliasessc = { "ucs", "cosmeticsshowcase" };
		this.ultraCosmetics.getServer().getPluginCommand("ultracosmeticsshowcase").setAliases(Arrays.asList(aliasessc));
	}
	
	/**
	 * Registers a command.
	 *
	 * @param meCommand The command to register.
	 */
	public void registerCommand(SubCommand meCommand) {
		commands.add(meCommand);
	}
	
	public void showHelp(CommandSender commandSender, int page) {
		commandSender.sendMessage("");
		commandSender.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "EnocraftCosmetics Showcase Help (/ucs <page>) " + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "(" + page + "/" + getMaxPages() + ")");
		int from = 1;
		if (page > 1)
			from = 8 * (page - 1) + 1;
		int to = 8 * page;
		for (int h = from; h <= to; h++) {
			if (h > commands.size())
				break;
			SubCommand sub = commands.get(h - 1);
			commandSender.sendMessage(ChatColor.DARK_GRAY + "|  " + ChatColor.GRAY + sub.getUsage() + ChatColor.WHITE + " " + ChatColor.ITALIC + sub.getDescription());
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
		
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
			return false;
		}

		// Parse arguments that have spaces if surrounded by quotes
		arguments = quotedSpaces(arguments);

		if (arguments == null
		    || arguments.length == 0) {
			showHelp(sender, 1);
			return true;
		}
		
		if (arguments.length == 1 && MathUtils.isInteger(arguments[0])) {
			showHelp(sender, Math.max(1, Math.min(Integer.parseInt(arguments[0]), getMaxPages())));
			return true;
		}
		
		for (SubCommand meCommand : commands) {
			if (meCommand.is(arguments[0])) {
				
				if (!sender.hasPermission(meCommand.getPermission())) {
					sender.sendMessage(MessageManager.getMessage("No-Permission"));
					return true;
				}
				
				if (sender instanceof Player) {
					meCommand.onExePlayer((Player) sender, arguments);
				} else {
					meCommand.onExeConsole((ConsoleCommandSender) sender, arguments);
				}
				return true;
			}
		}
		showHelp(sender, 1);
		return true;
	}
	
	public List<SubCommand> getCommands() {
		return commands;
	}

	public SubCommand getCommand(String alias) throws ClassNotFoundException {
		for (SubCommand meCommand : commands) {
			if (meCommand.is(alias)) {
				return meCommand;
			}
		}
		throw new ClassNotFoundException();
	}
	
	public void registerCommands(UltraCosmetics ultraCosmetics) {
		registerCommand(new SubCommandShowcaseClear(ultraCosmetics));
		registerCommand(new SubCommandShowcaseToggle(ultraCosmetics));
		registerCommand(new SubCommandShowcaseRenamePet(ultraCosmetics));
	}

	// TODO: Allow single quotes as well '
	// Regex function to allow arguments to have spaces if in quotes, helpful for parsing input strings
	// i.e uc "arg with spaces" will be returned as {"uc","arg with spaces"}
	public static String[] quotedSpaces(String[] arguments) {
		final Pattern PATTERN = Pattern.compile("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
		return PATTERN.split(String.join(" ", arguments).replaceAll("^\"", ""));
	}
}
