package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class SubCommandReload extends SubCommand {

	private UltraCosmetics uc;

	public SubCommandReload(UltraCosmetics ultraCosmetics) {
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
		SettingsManager.getConfig().loadConfiguration(uc.getFile());

		for (be.isach.ultracosmetics.player.UltraPlayer pl : uc.getPlayerManager().getUltraPlayers()) {
			if (pl.getInventory().getTitle().equalsIgnoreCase(uc.getMenus().getMainMenu().getName())) {
				pl.closeInventory();
				//sender.sendMessage(ChatColor.RED + "The cosmetics menu has been closed, because reloading the plugin.");
			}
			uc.getPlayerManager().getUltraPlayer(pl).removeMenuItem();

			uc.getPlayerManager().getUltraPlayer(pl).giveMenuItem();
			uc.getPlayerManager().getUltraPlayer(pl).updateInventory();
		}
		sender.sendMessage(ChatColor.GREEN + "Plugin has been loaded successfully.");
	}
}
