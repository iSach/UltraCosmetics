package be.isach.ultracosmetics.command.subcommands;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;
import be.isach.ultracosmetics.mysql.Table;
import be.isach.ultracosmetics.player.profile.PlayerData;

public class SubCommandMigrate extends SubCommand {

    public SubCommandMigrate(UltraCosmetics ultraCosmetics) {
        super("migrate", "Moves player data from flatfile to MySQL and vice versa", "<flatfile|sql>", ultraCosmetics);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            error(sender, "This command can only be used from the console.");
            return;
        }
        if (UltraCosmeticsData.get().usingFileStorage()) {
            error(sender, "SQL must be enabled and connected to migrate either direction.");
            return;
        }
        if (args.length < 2 || (!args[1].equalsIgnoreCase("flatfile") && !args[1].equalsIgnoreCase("sql"))) {
            badUsage(sender);
            return;
        }
        boolean flatfile = args[1].equalsIgnoreCase("flatfile");
        if (args.length < 3 || !args[2].equalsIgnoreCase("confirm")) {
            if (Bukkit.getOnlinePlayers().size() > 0) {
                error(sender, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                error(sender, "  It is HIGHLY recommended migration is only  ");
                error(sender, "performed when no players are online, but you ");
                error(sender, "           can decide for yourself!           ");
                error(sender, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            String from = flatfile ? "SQL database" : "flatfile";
            String to = flatfile ? "flatfile" : "SQL database";
            error(sender, "You are migrating from " + from + " to " + to);
            error(sender, "Warning: any conflicting data in " + to + " will be overwritten.");
            error(sender, "If you want to proceed, use /uc migrate " + args[1] + " confirm");
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(ultraCosmetics, () -> migrate(flatfile));
    }

    private void migrate(boolean flatfile) {
        ultraCosmetics.getSmartLogger().write("Loading UUIDs to migrate...");
        List<UUID> uuids;
        if (flatfile) {
            uuids = getFlatfileUUIDs();
        } else {
            uuids = getSqlUUIDs();
        }
        if (uuids == null) return; // error already printed
        ultraCosmetics.getSmartLogger().write("Found " + uuids.size() + " UUIDs, starting migration");
        PlayerData data;
        for (UUID uuid : uuids) {
            data = new PlayerData(uuid);
            if (flatfile) {
                data.loadFromSQL();
                data.saveToFile();
            } else {
                data.loadFromFile();
                data.saveToSQL();
            }
            ultraCosmetics.getSmartLogger().write("Successfully migrated " + uuid.toString());
        }
        ultraCosmetics.getSmartLogger().write(uuids.size() + " UUIDs successfully migrated!");
    }

    private List<UUID> getSqlUUIDs() {
        List<UUID> uuids = new ArrayList<>();
        File dataFolder = new File(ultraCosmetics.getDataFolder(), "data");
        for (File file : dataFolder.listFiles()) {
            if (!file.getName().endsWith(".yml")) continue;
            String baseName = file.getName().substring(0, file.getName().length() - 4);
            try {
                uuids.add(UUID.fromString(baseName));
            } catch (IllegalArgumentException e) {
                ultraCosmetics.getSmartLogger().write(LogLevel.WARNING, "Failed to parse UUID of flatfile '" + file.getName() + "', ignoring");
            }
        }
        return uuids;
    }

    private List<UUID> getFlatfileUUIDs() {
        Table table = ultraCosmetics.getMySqlConnectionManager().getTable();
        List<UUID> uuids;
        try (Connection co = ultraCosmetics.getMySqlConnectionManager().getDataSource().getConnection()) {
            PreparedStatement statement = co.prepareStatement("SELECT uuid FROM " + table.getWrappedName());
            ResultSet set = statement.executeQuery();
            uuids = new ArrayList<>();
            while (set.next()) {
                uuids.add(UUID.fromString(set.getString(1)));
            }
            statement.close(); // ResultSet auto closed
        } catch (SQLException e) {
            ultraCosmetics.getSmartLogger().write(LogLevel.ERROR, "Failed to fetch UUIDs from database, aborting migration");
            e.printStackTrace();
            return null;
        }
        return uuids;
    }
}
