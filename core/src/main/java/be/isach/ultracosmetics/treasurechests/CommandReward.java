package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.util.CustomConfiguration;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * A command reward.
 *
 * @author RadBuilder
 * @since 10-21-2017
 */
public class CommandReward {
	private String name;
	private Material material;
	private int chance;
	private boolean messageEnabled;
	private String message;
	private List<String> commands;

	public CommandReward(String path) {
		CustomConfiguration config = UltraCosmeticsData.get().getPlugin().getConfig();
		chance = config.getInt(path + ".Chance");
		messageEnabled = config.getBoolean(path + ".Message.enabled");
		message = config.getString(path + ".Message.message");
		commands = config.getStringList(path + ".Commands");
		material = Material.valueOf(config.getString(path + ".Material"));
		name = ChatColor.translateAlternateColorCodes('&', config.getString(path + ".Name"));
	}

	public int getChance() {
		return chance;
	}

	public boolean getMessageEnabled() {
		return messageEnabled;
	}

	public String getMessage() {
		return message;
	}

	public List<String> getCommands() {
		return commands;
	}

	public String getName() {
		return name;
	}

	public Material getMaterial() {
		return material;
	}
}
