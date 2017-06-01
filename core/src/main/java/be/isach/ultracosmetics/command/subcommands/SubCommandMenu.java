package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.menu.Menus;
import be.isach.ultracosmetics.menu.menus.MenuGadgets;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Menu {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 *
 * @author iSach
 * @since 12-21-2015
 */
public class SubCommandMenu extends SubCommand {
	
	public SubCommandMenu(UltraCosmetics ultraCosmetics) {
		super("Opens Specified Menu", "ultracosmetics.command.menu", "/uc menu <menu> [page]", ultraCosmetics, "menu");
		this.menuGadgets = new MenuGadgets(getUltraCosmetics());
	}
	
	private MenuGadgets menuGadgets;
	
	@Override
	protected void onExePlayer(Player sender, String... args) {
		if (args.length < 2) {
			sender.sendMessage("§c§l/uc menu <menu>\n§c§lAvailable Menus: main, gadgets, particleeffects, pets, mounts, suits, hats, morphs");
			return;
		}
		
		int page = 1;
		
		if (args.length > 2 && MathUtils.isInteger(args[2])) {
			page = Integer.parseInt(args[2]);
		}
		
		String s = args[1].toLowerCase();
		
		Menus menus = getUltraCosmetics().getMenus();
		
		UltraPlayer ultraPlayer = getUltraCosmetics().getPlayerManager().getUltraPlayer(sender);
		
		if (s.startsWith("g")) {
			menus.getGadgetsMenu().open(ultraPlayer, page);
		} else if (s.startsWith("pa")) {
			menus.getEffectsMenu().open(ultraPlayer, page);
		} else if (s.startsWith("pe")) {
			menus.getPetsMenu().open(ultraPlayer, page);
		} else if (s.startsWith("h")) {
			menus.getHatsMenu().open(ultraPlayer, page);
		} else if (s.startsWith("s")) {
			menus.getSuitsMenu().open(ultraPlayer, page);
		} else if (s.startsWith("mor")) {
			menus.getMorphsMenu().open(ultraPlayer, page);
		} else if (s.startsWith("mou")) {
			menus.getMountsMenu().open(ultraPlayer, page);
		} else if (s.startsWith("ma")) {
			menus.getMainMenu().open(ultraPlayer);
		} else if (s.startsWith("e")) {
			menus.getEmotesMenu().open(ultraPlayer, page);
		} else if (s.startsWith("b")) {
			sender.closeInventory();
			getUltraCosmetics().getPlayerManager().getUltraPlayer(sender).openKeyPurchaseMenu();
		} else {
			sender.sendMessage("§c§l/uc menu <menu>\n§c§lInvalid Menu\n§c§lAvailable Menus: main,"
			                   + (UltraCosmeticsData.get().areTreasureChestsEnabled() ? " buykey," : "")
			                   + (SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled") ? " renamepet," : "") +
			                   " gadgets, particleeffects, pets, mounts, suits, hats, morphs, emotes");
		}
	}
	
	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		notAllowed(sender);
	}
}

