package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SubCommandClear extends SubCommand {

	public SubCommandClear(UltraCosmetics ultraCosmetics) {
		super("Reload the plugin and config.", "ultracosmetics.command.reload", "/uc reload", ultraCosmetics, "reload");
	}

	@Override
	protected void onExePlayer(Player sender, String... args) {
		common(sender, args);
	}

	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		common(sender, args);
	}

	private void common(CommandSender sender, String... args) {
		getUltraCosmetics().getConfig().loadConfiguration(getUltraCosmetics().getFile());
		sender.sendMessage(ChatColor.GREEN + "Plugin has been loaded successfully.");
	}
}
