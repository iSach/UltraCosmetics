package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by sacha on 26/08/15.
 */
public class MorphBat extends Morph {
    public MorphBat(UUID owner) {
        super(owner, MorphType.BAT);
        UltraCosmetics.getInstance().registerListener(this);
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
            SoundUtil.playSound(getPlayer(), Sounds.BAT_LOOP, 0.4f, 1.0f);
        }
    }

    @Override
    public void clear() {
        if (getPlayer().getGameMode() != GameMode.CREATIVE)
            getPlayer().setAllowFlight(false);
        super.clear();
    }
}
