package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.cryptomorin.xseries.XSound;

/**
 * Represents an instance of a cow morph summoned by a player.
 *
 * @author RadBuilder
 * @since 07-03-2017
 */
public class MorphCow extends Morph {
    // TODO: Add something better for this morph - having it just "moo" isn't much.
    private long coolDown = 0;

    public MorphCow(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MorphType.valueOf("cow"), ultraCosmetics);
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if ((event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getPlayer() == getPlayer()) {
            if (coolDown > System.currentTimeMillis()) return;
            event.setCancelled(true);
            XSound.ENTITY_COW_AMBIENT.play(event.getPlayer().getLocation());
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
