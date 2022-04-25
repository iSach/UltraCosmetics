package be.isach.ultracosmetics.permissions;

import be.isach.ultracosmetics.config.SettingsManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PermissionCommand implements PermissionProvider {
    private final String commandTemplate;
    public PermissionCommand() {
        commandTemplate = SettingsManager.getConfig().getString("TreasureChests.Permission-Add-Command");
    }

    @Override
    public void setPermission(Player player, String permission) {
        String command = commandTemplate.replace("%name%", player.getName()).replace("%permission%", permission);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

}
