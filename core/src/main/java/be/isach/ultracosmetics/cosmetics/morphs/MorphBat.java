package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;
import me.libraryaddict.disguise.DisguiseAPI;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

/**
 * Created by sacha on 26/08/15.
 */
public class MorphBat extends Morph {

    public MorphBat(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MorphType.BAT, ultraCosmetics);
        if (owner != null) {
            getPlayer().setAllowFlight(true);
        }
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
    public void onClear() {
        if (getPlayer().getGameMode() != GameMode.CREATIVE)
            getPlayer().setAllowFlight(false);
        DisguiseAPI.undisguiseToAll(getPlayer());
        getOwner().setCurrentMorph(null);
        owner = null;
        
        try {
            HandlerList.unregisterAll(this);
        } catch (Exception exc) {
        }
    }

    @Override
    protected void onEquip() {

    }
}
