package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import me.libraryaddict.disguise.disguisetypes.watchers.LlamaWatcher;
import org.bukkit.entity.Llama;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Represents an instance of a llama morph summoned by a player.
 *
 * @author RadBuilder
 * @since 07-03-2017
 */
public class MorphLlama extends Morph {
	private long coolDown = 0;

	public MorphLlama(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, MorphType.valueOf("llama"), ultraCosmetics);
	}

	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {
		if ((event.getAction() == Action.LEFT_CLICK_AIR
		     || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getPlayer() == getPlayer()) {
			if (coolDown > System.currentTimeMillis()) return;
			event.setCancelled(true);
			LlamaSpit llamaSpit = event.getPlayer().launchProjectile(LlamaSpit.class);
			System.out.println("llama spit is from: " + llamaSpit.getShooter());
			coolDown = System.currentTimeMillis() + 500;
		}
	}

	@Override
	public void onUpdate() {
	}

	@Override
	protected void onClear() {
	}
}
