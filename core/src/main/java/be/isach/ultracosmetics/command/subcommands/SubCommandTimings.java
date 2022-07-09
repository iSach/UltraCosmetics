package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.cosmetics.gadgets.Gadget;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubCommandTimings extends SubCommand {

    public SubCommandTimings(UltraCosmetics ultraCosmetics) {
        super("timings", "Shows timings", "[reset]", ultraCosmetics);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        if (args.length > 1 && args[1].equalsIgnoreCase("reset")) {
            Gadget.TIMINGS.clear();
            sender.sendMessage("timings reset");
            return;
        }
        List<List<Long>> byStep = new ArrayList<>();
        for (int i = 0; i < Gadget.TIMINGS.get(0).size() - 1; i++) {
            byStep.add(new ArrayList<>());
        }
        for (List<Long> time : Gadget.TIMINGS) {
            long last = time.get(0);
            for (int i = 1; i < time.size(); i++) {
                long ms = time.get(i) - last;
                byStep.get(i - 1).add(ms);
                last = time.get(i);
            }
        }
        for (int i = 0; i < byStep.size(); i++) {
            long min = Collections.min(byStep.get(i));
            long max = Collections.max(byStep.get(i));
            double avg = byStep.get(i).stream().mapToLong(a -> a).average().getAsDouble();
            sender.sendMessage(ChatColor.YELLOW + "Step " + (i + 1) + ": best " + min + "ms, worst: " + max + "ms, avg: " + avg + "ms");
        }
    }

}
