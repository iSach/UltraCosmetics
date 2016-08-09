package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by sacha on 27/08/15.
 */
public class MorphPig extends Morph {

    private boolean cooldown = false;

    public MorphPig(UUID owner) {
        super(owner, MorphType.PIG);
        if (owner != null) {

            final MorphPig pig = this;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (getPlayer() == null
                            || UltraCosmetics.getCustomPlayer(getPlayer()).currentMorph != pig) {
                        cancel();
                        return;
                    }
                    for(Entity ent : getPlayer().getNearbyEntities(0.2, 0.2, 0.2)) {
                        if(ent instanceof Creature || ent instanceof Player) {
                            if(!ent.hasMetadata("Mount")
                                    && !ent.hasMetadata("Pet")
                                    && ent != getPlayer()
                                    && ent != disguise.getEntity()
                                    && !cooldown) {
                                cooldown = true;
                                Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                                    @Override
                                    public void run() {
                                        cooldown = false;
                                    }
                                }, 20);
                                SoundUtil.playSound(getPlayer(), Sounds.PIG_IDLE, .2f, 1.5f);
                                Vector vEnt = ent.getLocation().toVector().subtract(getPlayer().getLocation().toVector()).add(new Vector(0, 0.6, 0));
                                Vector vPig = getPlayer().getLocation().toVector().subtract(ent.getLocation().toVector()).add(new Vector(0, 0.6, 0));
                                vEnt.setY(0.5);
                                vPig.setY(0.5);
                                MathUtils.applyVelocity(ent, vEnt.multiply(0.75));
                                MathUtils.applyVelocity(getPlayer(), vPig.multiply(0.75));
                            }
                        }
                    }
                }
            }.runTaskTimer(UltraCosmetics.getInstance(), 0, 1);
        }
    }

}
