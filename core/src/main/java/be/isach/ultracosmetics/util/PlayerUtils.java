package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmeticsData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Created by Sacha on 17/10/15.
 */
public class PlayerUtils {

    public static Vector getHorizontalDirection(Player player, double mult) {
        Vector vector = new Vector();
        double rotX = player.getLocation().getYaw();
        double rotY = 0;
        vector.setY(-Math.sin(Math.toRadians(rotY)));
        double xz = Math.cos(Math.toRadians(rotY));
        vector.setX((-xz * Math.sin(Math.toRadians(rotX))) * mult);
        vector.setZ((xz * Math.cos(Math.toRadians(rotX))) * mult);
        return vector;
    }

    public static void sendInActionBar(Player player, String message) {
        UltraCosmeticsData.get().getVersionManager().getActionBarUtil().sendActionMessage(player, message);
    }

}
