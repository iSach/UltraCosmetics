package me.isach.ultracosmetics.cosmetics.morphs;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.util.MathUtils;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by sacha on 26/08/15.
 */
public class MorphBat extends Morph {
    public MorphBat(UUID owner) {
        super(DisguiseType.BAT, Material.COAL, (byte) 0, "Bat", "ultracosmetics.morphs.bat", owner, MorphType.BAT);
        Core.registerListener(this);
        if (owner != null)
            getPlayer().setAllowFlight(true);
    }

    @EventHandler
    public void onPlayerToggleFligh(PlayerToggleFlightEvent event) {
        if (event.getPlayer() == getPlayer()
                && event.getPlayer().getGameMode() != GameMode.CREATIVE
                && !event.getPlayer().isFlying()) {
            Vector v = event.getPlayer().getLocation().getDirection();
            v.setY(0.75);
            MathUtils.applyVelocity(getPlayer(), v);
            event.getPlayer().setFlying(false);
            event.setCancelled(true);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BAT_LOOP, 0.4f, 1);
        }
    }

    @Override
    public void clear() {
        if (getPlayer().getGameMode() != GameMode.CREATIVE)
            getPlayer().setAllowFlight(false);
        DisguiseAPI.undisguiseToAll(getPlayer());
        getPlayer().sendMessage(MessageManager.getMessage("Morphs.Unmorph").replace("%morphname%", getName()));
        Core.getCustomPlayer(getPlayer()).currentMorph = null;
        owner = null;
    }
}
