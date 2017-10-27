package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Represents an instance of a witch morph summoned by a player.
 *
 * @author RadBuilder
 * @since 07-03-2017
 */
public class MorphWitch extends Morph {
	private long coolDown = 0;
	private Random r = new Random();

	public MorphWitch(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, MorphType.valueOf("witch"), ultraCosmetics);
	}

	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {
		if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
				&& event.getPlayer() == getPlayer()) {
			if (coolDown > System.currentTimeMillis()) return;
			event.setCancelled(true);
			getPlayer().launchProjectile(ThrownPotion.class);
			coolDown = System.currentTimeMillis() + 2000;
		}
	}

	@Override
	public void onUpdate() {
	}

	@Override
	protected void onClear() {
	}
}
