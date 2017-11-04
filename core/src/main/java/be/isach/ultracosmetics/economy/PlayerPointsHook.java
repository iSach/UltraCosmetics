package be.isach.ultracosmetics.economy;

import be.isach.ultracosmetics.UltraCosmetics;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * PlayerPoints economy hook.
 *
 * @author RadBuilder
 * @since 2.5
 */
public class PlayerPointsHook implements EconomyHook {
	private UltraCosmetics ultraCosmetics;
	private PlayerPoints playerPoints;
	private boolean economyEnabled;

	public PlayerPointsHook(UltraCosmetics ultraCosmetics) {
		this.ultraCosmetics = ultraCosmetics;
		if (hookPlayerPoints()) {
			ultraCosmetics.getSmartLogger().write("");
			ultraCosmetics.getSmartLogger().write("Hooked into PlayerPoints for economy.");
			ultraCosmetics.getSmartLogger().write("");
			economyEnabled = true;
		} else {
			ultraCosmetics.getSmartLogger().write("");
			ultraCosmetics.getSmartLogger().write("Something happened while hooking into PlayerPoints for economy.");
			ultraCosmetics.getSmartLogger().write("");
			economyEnabled = false;
		}
	}

	/**
	 * Validate that there's access to PlayerPoints.
	 *
	 * @return True if there is access to PlayerPoints, otherwise false.
	 */
	private boolean hookPlayerPoints() {
		final Plugin plugin = ultraCosmetics.getServer().getPluginManager().getPlugin("PlayerPoints");
		playerPoints = PlayerPoints.class.cast(plugin);
		return playerPoints != null;
	}

	@Override
	public void withdraw(Player player, int amount) {
		playerPoints.getAPI().take(player.getUniqueId(), amount);
	}

	@Override
	public void deposit(Player player, int amount) {
		playerPoints.getAPI().give(player.getUniqueId(), amount);
	}

	@Override
	public double balance(Player player) {
		return playerPoints.getAPI().look(player.getUniqueId());
	}

	@Override
	public String getName() {
		return "PlayerPoints";
	}

	@Override
	public boolean economyEnabled() {
		return economyEnabled;
	}
}
