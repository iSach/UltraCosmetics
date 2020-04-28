package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Gadgets {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 *
 * @author iSach
 * @since 12-20-2015
 */
public class SubCommandGadgets extends SubCommand {

    public SubCommandGadgets(UltraCosmetics ultraCosmetics) {
        super("Toggle Gadgets", "ultracosmetics.command.gadgets", "/uc gadgets", ultraCosmetics, "gadgets");
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        if (!UltraCosmeticsData.get().getEnabledWorlds().contains(sender.getWorld().getName())) {
            sender.sendMessage(MessageManager.getMessage("World-Disabled"));
            return;
        }

        UltraPlayer customPlayer = getUltraCosmetics().getPlayerManager().getUltraPlayer(sender);
        customPlayer.setGadgetsEnabled(!customPlayer.hasGadgetsEnabled());
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        notAllowed(sender);
    }

    @Override
    public List<String> getTabCompleteSuggestion(CommandSender sender, String... args) {
        //uc gadgets
        List<String> tabSuggestion = new ArrayList<>();
        return tabSuggestion; // no need to tab-complete anything in gadgets
    }
}
