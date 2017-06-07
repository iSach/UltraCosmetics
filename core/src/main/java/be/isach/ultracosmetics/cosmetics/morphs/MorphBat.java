package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

/**
 * Represents an instance of a bat morph summoned by a player.
 *
 * @author iSach
 * @since 08-26-2015
 */
public class MorphBat extends Morph {

	public MorphBat(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, MorphType.BAT, ultraCosmetics);
	}

	@Override
	protected void onEquip() {
		super.onEquip();
		getPlayer().setAllowFlight(true);
	}

	@Override
	public void onUpdate() {
		//--
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
		if (getPlayer().getGameMode() != GameMode.CREATIVE) {
			getPlayer().setAllowFlight(false);
		}
	}
}