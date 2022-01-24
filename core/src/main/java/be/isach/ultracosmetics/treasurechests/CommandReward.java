package be.isach.ultracosmetics.treasurechests;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.CustomConfiguration;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * A command reward.
 *
 * @author RadBuilder
 * @since 10-21-2017
 */
public class CommandReward {
    private String name;
    private ItemStack stack;
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
        stack = ItemFactory.getItemStackFromConfig(path + ".Material");
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

    public ItemStack getItemStack() {
        return stack.clone();
    }
}
