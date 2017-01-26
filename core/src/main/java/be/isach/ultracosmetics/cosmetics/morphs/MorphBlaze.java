package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.Sounds;
import be.isach.ultracosmetics.util.UtilParticles;
import be.isach.ultracosmetics.util.SoundUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
* Represents an instance of a blaze morph summoned by a player.
 * 
 * @author 	iSach
 * @since 	08-26-2015
 */
public class MorphBlaze extends Morph {

    public MorphBlaze(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MorphType.BLAZE, ultraCosmetics);

        if (owner != null) {
            final MorphBlaze blaze = this;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (getPlayer() == null
                            || getOwner().getCurrentMorph() != blaze) {
                        cancel();
                        return;
                    }

                    if (getPlayer().isSneaking()) {
                        UtilParticles.display(Particles.FLAME, getPlayer().getLocation());
                        UtilParticles.display(Particles.LAVA, getPlayer().getLocation());
                        SoundUtil.playSound(getPlayer(), Sounds.FIZZ, 0.1f, 1.5f);
                        getPlayer().setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1));
                    }
                }
            }.runTaskTimer(getUltraCosmetics(), 0, 1);
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if(event.getPlayer() == getPlayer() && getOwner().getCurrentMorph() == this && event.getReason().contains("Flying"))
            event.setCancelled(true);

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntity() == getPlayer() && getOwner().getCurrentMorph() == this && event.getCause() == EntityDamageEvent.DamageCause.FALL)
            event.setCancelled(true);
    }

    @Override
    protected void onEquip() {
    }
}
