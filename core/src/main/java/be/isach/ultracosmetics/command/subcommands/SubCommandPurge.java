package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * Purge {@link SubCommand SubCommand}.
 *
 * @author RadBuilder
 * @since 11-14-2018
 */
public class SubCommandPurge extends SubCommand {
	
	public SubCommandPurge(UltraCosmetics ultraCosmetics) {
		super("Purges old data files.", "ultracosmetics.command.purge", "/uc purge <confirm>", ultraCosmetics, "purge");
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
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Are you sure you want to purge old player data files? Depending on the amount of data files you have, this may lag your server for a noticable amount of time.");
			sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "To confirm purge of playerdata that doesn't contain treasure keys or pet names, type /uc purge confirm");
			return;
		}
		if (args[1].equalsIgnoreCase("confirm")) {
			sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Starting deletion now, this may take a while. Please wait...");
			Bukkit.getScheduler().runTaskAsynchronously(getUltraCosmetics(), () -> {
				File dataFolder = new File(getUltraCosmetics().getDataFolder(), "data");
				int deletedFiles = 0;
				int savedFiles = 0;
				if (dataFolder.isDirectory()) {
					for (File file : dataFolder.listFiles()) {
						if (file.lastModified() < System.currentTimeMillis() + 86400000) { // File old enough to check for config values set
							YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
							if ((config.contains("Keys") && config.getInt("Keys") > 0) || config.contains("Pet-Names")) {
								savedFiles++;
							} else {
								deletedFiles++;
								file.delete();
							}
						}
					}
					sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Success! " + deletedFiles + " files were deleted, and " + savedFiles + " files were saved because of keys or pet names.");
				} else {
					sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "An error occured: folder not valid. No data was purged.");
				}
			});
		} else {
			sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Are you sure you want to purge old player data files? Depending on the amount of data files you have, this may lag your server for a noticable amount of time.");
			sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "To confirm purge of playerdata that doesn't contain treasure keys or pet names, type /uc purge confirm");
		}
	}
}
