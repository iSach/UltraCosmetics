package be.isach.ultracosmetics.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Project: UltraCosmetics
 * Package: be.isach.ultracosmetics.version
 * Created by: Sacha
 * Created on: 18th June, 2016
 * at 19:11
 */
public class SoundUtil {

    private static ServerVersion serverVersion;

    public static void setServerVersion(ServerVersion serverVersion) {
        SoundUtil.serverVersion = serverVersion;
    }

    public static void playSound(Location location, Sound sound, float volume, float speed) {
        if (serverVersion.compareTo(ServerVersion.v1_9_R1) < 0)
            location.getWorld().playSound(location, Sounds.valueOf(sound.toString()).bukkitSound(), volume, speed);
        else
            location.getWorld().playSound(location, sound, volume, speed);
    }

    public static void playSound(Location location, Sound sound, float volume) {
        playSound(location, sound, volume, 1f);
    }

    public static void playSound(Location location, Sound sound) {
        playSound(location, sound, 1f, 1f);
    }

    public static void playSound(Player player, Sound sound, float volume, float speed) {
        if (serverVersion.compareTo(ServerVersion.v1_9_R1) < 0)
            player.playSound(player.getLocation(), Sounds.valueOf(sound.toString()).bukkitSound(), volume, speed);
        else
            player.playSound(player.getLocation(), sound, volume, speed);
    }

    public static void playSound(Player player, Sound sound, float volume) {
        playSound(player, sound, volume, 1f);
    }

    public static void playSound(Player player, Sound sound) {
        playSound(player, sound, 1f, 1f);
    }

}
