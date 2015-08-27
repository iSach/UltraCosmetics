package me.isach.ultracosmetics.cosmetics.morphs;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.util.MathUtils;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.Sound;
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

    public MorphPig(UUID owner) {
        super(DisguiseType.PIG, Material.PORK, (byte) 0x0, "Pig", "ultracosmetics.morphs.pig", owner, MorphType.PIG);
        if (owner != null) {

            final MorphPig pig = this;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (getPlayer() == null
                            || Core.getCustomPlayer(getPlayer()).currentMorph != pig) {
                        cancel();
                        return;
                    }
                    for(Entity ent : getPlayer().getNearbyEntities(0.2, 0.2, 0.2)) {
                        if(ent instanceof Creature || ent instanceof Player) {
                            ent.getWorld().playSound(ent.getLocation(), Sound.PIG_IDLE, 0.3f, 1);
                            Vector vEnt = ent.getLocation().toVector().subtract(getPlayer().getLocation().toVector()).add(new Vector(0, 0.6, 0));
                            Vector vPig = getPlayer().getLocation().toVector().subtract(ent.getLocation().toVector()).add(new Vector(0, 0.6, 0));
                            vEnt.setY(0.5);
                            vPig.setY(0.5);
                            MathUtils.applyVelocity(ent, vEnt.multiply(0.75));
                            MathUtils.applyVelocity(getPlayer(), vPig.multiply(0.75));
                        }
                    }
                }
            }.runTaskTimer(Core.getPlugin(), 0, 1);
        }
    }

}
