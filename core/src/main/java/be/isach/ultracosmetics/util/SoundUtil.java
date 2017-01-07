package be.isach.ultracosmetics.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Project: UltraCosmetics
 * Package: be.isach.ultracosmetics.version
 * Created by: Sacha
 * Created on: 18th June, 2016
 * at 19:11
 */
public class SoundUtil {

    public static void playSound(Location location, Sounds sound, float volume, float speed) {
        location.getWorld().playSound(location, sound.bukkitSound(), volume, speed);
    }

    public static void playSound(Location location, Sounds sound, float volume) {
        playSound(location, sound, volume, 1f);
    }

    public static void playSound(Location location, Sounds sound) {
        playSound(location, sound, 1f, 1f);
    }

    public static void playSound(Player player, Sounds sound, float volume, float speed) {
        player.playSound(player.getLocation(), sound.bukkitSound(), volume, speed);
    }

    public static void playSound(Player player, Sounds sound, float volume) {
        playSound(player, sound, volume, 1f);
    }

    public static void playSound(Player player, Sounds sound) {
        playSound(player, sound, 1f, 1f);
    }

}
