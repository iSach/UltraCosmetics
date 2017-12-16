package be.isach.ultracosmetics.economy;

import be.isach.ultracosmetics.UltraCosmetics;
import org.bukkit.entity.Player;

/**
 * Handles the current economy being used.
 *
 * @author RadBuilder
 * @since 2.5
 */
public class EconomyHandler {
	private UltraCosmetics ultraCosmetics;
	private EconomyHook economyHook;
	private boolean usingEconomy;

	public EconomyHandler(UltraCosmetics ultraCosmetics, String economy) {
		this.ultraCosmetics = ultraCosmetics;
		if (economy == null || economy.equalsIgnoreCase("")) {
			ultraCosmetics.getSmartLogger().write("Economy not specified in the config, disabling economy features.");
			usingEconomy = false;
			return;
		}
		if (economy.equalsIgnoreCase("vault")) {
			economyHook = new VaultHook(ultraCosmetics);
			usingEconomy = true;
		} else if (economy.equalsIgnoreCase("playerpoints")) {
			economyHook = new PlayerPointsHook(ultraCosmetics);
			usingEconomy = true;
		} else {
			ultraCosmetics.getSmartLogger().write("Unknown economy: '" + economy + "'. Valid economies: Vault, PlayerPoints.");
			usingEconomy = false;
		}
	}

	public void withdraw(Player player, int amount) {
		economyHook.withdraw(player, amount);
	}

	public void deposit(Player player, int amount) {
		economyHook.deposit(player, amount);
	}

	public double balance(Player player) {
		return economyHook.balance(player);
	}

	public String getName() {
		return economyHook.getName();
	}

	public boolean isUsingEconomy() {
		return usingEconomy && economyHook.economyEnabled();
	}
}
