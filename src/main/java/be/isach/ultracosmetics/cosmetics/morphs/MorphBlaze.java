package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Created by sacha on 26/08/15.
 */
public class MorphBlaze extends Morph {

    public MorphBlaze(UUID owner) {
        super(owner, MorphType.BLAZE);

        if (owner != null) {

            Core.registerListener(this);

            final MorphBlaze blaze = this;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (getPlayer() == null
                            || Core.getCustomPlayer(getPlayer()).currentMorph != blaze) {
                        cancel();
                        return;
                    }

                    if (getPlayer().isSneaking()) {
                        UtilParticles.display(Particles.FLAME, getPlayer().getLocation());
                        UtilParticles.display(Particles.LAVA, getPlayer().getLocation());
                        getPlayer().playSound(getPlayer().getLocation(), Sound.FIZZ, 0.05f, 1);
                        getPlayer().setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1));
                    }
                }
            }.runTaskTimer(Core.getPlugin(), 0, 1);
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if(event.getPlayer() == getPlayer() && Core.getCustomPlayer(getPlayer()).currentMorph == this && event.getReason().contains("Flying"))
            event.setCancelled(true);

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntity() == getPlayer() && Core.getCustomPlayer(getPlayer()).currentMorph == this && event.getCause() == EntityDamageEvent.DamageCause.FALL)
            event.setCancelled(true);
    }
}
