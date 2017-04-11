package be.isach.ultracosmetics.version;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IFireworkFactory {
    void spawn(Location location, FireworkEffect effect, Player... players);
}
