package be.isach.ultracosmetics.permissions;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;

public class LuckPermsHook implements PermissionProvider {
    private final UltraCosmetics ultraCosmetics;
    private final LuckPerms api;
    private final ImmutableContextSet context;
    private boolean log = true;
    public LuckPermsHook(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        api = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        String[] contexts = SettingsManager.getConfig().getString("TreasureChests.Permission-Add-Command").split(" ");
        ImmutableContextSet.Builder contextBuilder = ImmutableContextSet.builder();
        for (int i = 1; i < contexts.length; i++) {
            if (contexts[i].equals("nolog")) {
                log = false;
                continue;
            }
            String[] kv = contexts[i].split("=");
            if (kv.length != 2) {
                ultraCosmetics.getSmartLogger().write(LogLevel.WARNING, "Invalid LuckPerms context: " + contexts[i]);
                continue;
            }
            contextBuilder.add(kv[0], kv[1]);
        }
        context = contextBuilder.build();
    }

    @Override
    public void setPermission(Player player, String permission) {
        if (log) {
            ultraCosmetics.getSmartLogger().write("Setting permission '" + permission + "' for user " + player.getName());
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                User user = api.getPlayerAdapter(Player.class).getUser(player);
                Node node = Node.builder(permission).value(true).context(context).build();
                user.data().add(node);
                api.getUserManager().saveUser(user);
            }
        }.runTaskAsynchronously(ultraCosmetics);
    }

}
