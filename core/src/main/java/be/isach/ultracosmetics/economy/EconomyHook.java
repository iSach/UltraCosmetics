package be.isach.ultracosmetics.economy;

import org.bukkit.entity.Player;

/**
 * Economy hook interface.
 *
 * @author RadBuilder
 * @since 2.5
 */
public interface EconomyHook {
	/**
	 * Withdraws the specified amount of money from the specified player.
	 *
	 * @param player The player to withdraw money from.
	 * @param amount The amount to withdraw from the player.
	 */
	void withdraw(Player player, int amount);

	/**
	 * Gives the specified amount of money to the specified player.
	 *
	 * @param player The player to give money to.
	 * @param amount The amount to give to the player.
	 */
	void deposit(Player player, int amount);

	/**
	 * Gets the balance of the specified player.
	 *
	 * @param player The player to get the balance of.
	 * @return The player's balance.
	 */
	double balance(Player player);

	/**
	 * Gets the name of the economy being used.
	 *
	 * @return The name of the economy being used.
	 */
	String getName();

	/**
	 * Gets if the economy is enabled and functioning.
	 *
	 * @return True if the economy is enabled and functioning, false otherwise.
	 */
	boolean economyEnabled();
}
