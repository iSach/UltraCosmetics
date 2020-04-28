package be.isach.ultracosmetics.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface ITabCompletable {

    public List<String> getTabCompleteSuggestion(CommandSender sender, String... args);

}
