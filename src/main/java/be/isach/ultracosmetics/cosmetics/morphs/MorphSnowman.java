package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.Core;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

/**
 * Created by Sacha on 29/11/15.
 */
public class MorphSnowman extends Morph {

    public MorphSnowman(UUID owner) {
        super(DisguiseType.SNOWMAN, Material.SNOW_BALL, (byte) 0x0, "Snowman", "ultracosmetics.morphs.snowman", owner, MorphType.SNOWNMAN, "&7&oI'm Olaf!");
        if (owner != null)
            Core.registerListener(this);
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if ((event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getPlayer() == getPlayer()) {
            event.setCancelled(true);
            event.getPlayer().throwSnowball();
        }
    }
}
