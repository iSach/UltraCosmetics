package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
            llamaSpit.setShooter(event.getPlayer());
            coolDown = System.currentTimeMillis() + 1500;
        }
    }

    @Override
    public void onUpdate() {
    }

    @Override
    protected void onClear() {
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (getOwner() != null && getPlayer() != null && event.getDamager() == getPlayer()) {
            event.setCancelled(true);
        }
    }
}
