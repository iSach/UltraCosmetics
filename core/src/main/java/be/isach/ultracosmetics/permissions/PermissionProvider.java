package be.isach.ultracosmetics.permissions;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public interface PermissionProvider {
    public default void setPermission(Player player, Permission permission) {
        setPermission(player, permission.getName());
    }
    public void setPermission(Player player, String permission);
}
