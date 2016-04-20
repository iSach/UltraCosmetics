package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
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

            UltraCosmetics.getInstance().registerListener(this);

            final MorphBlaze blaze = this;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (getPlayer() == null
                            || UltraCosmetics.getCustomPlayer(getPlayer()).currentMorph != blaze) {
                        cancel();
                        return;
                    }

                    if (getPlayer().isSneaking()) {
                        UtilParticles.display(Particles.FLAME, getPlayer().getLocation());
                        UtilParticles.display(Particles.LAVA, getPlayer().getLocation());
                        switch (UltraCosmetics.getServerVersion()) {
                            case v1_8_R3:
                                getPlayer().playSound(getPlayer().getLocation(), Sound.valueOf("FIZZ"), 0.1f, 1.5f);
                                break;
                            case v1_9_R1:
                                getPlayer().playSound(getPlayer().getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 0.1f, 1.5f);
                                break;
                        }
                        getPlayer().setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1));
                    }
                }
            }.runTaskTimer(UltraCosmetics.getInstance(), 0, 1);
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if(event.getPlayer() == getPlayer() && UltraCosmetics.getCustomPlayer(getPlayer()).currentMorph == this && event.getReason().contains("Flying"))
            event.setCancelled(true);

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntity() == getPlayer() && UltraCosmetics.getCustomPlayer(getPlayer()).currentMorph == this && event.getCause() == EntityDamageEvent.DamageCause.FALL)
            event.setCancelled(true);
    }
}
