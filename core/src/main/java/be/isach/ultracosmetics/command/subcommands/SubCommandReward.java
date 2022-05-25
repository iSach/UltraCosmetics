package be.isach.ultracosmetics.command.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.treasurechests.TreasureRandomizer;

public class SubCommandReward extends SubCommand {

    public SubCommandReward(UltraCosmetics ultraCosmetics) {
        super("reward", "Gives reward(s) as if a treasure chest was used", "[amount] [player]", ultraCosmetics);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        if (args.length < 3 && !(sender instanceof Player)) {
            badUsage(sender, "You must specify a player when used in console!");
            return;
        }
        Player target;
        int n = 1;
        if (args.length > 1) {
            try {
                n = Integer.parseInt(args[1]);
                if (n < 1) n = 1;
            } catch (NumberFormatException e) {
                error(sender, "Invalid number!");
                return;
            }
        }
        if (args.length > 2) {
            target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                error(sender, "Invalid player!");
                return;
            }
        } else {
            target = (Player) sender;
        }
        TreasureRandomizer tr = new TreasureRandomizer(target, target.getLocation());
        for (int i = 0; i < n; i++) {
            tr.giveRandomThing();
        }
    }

}
