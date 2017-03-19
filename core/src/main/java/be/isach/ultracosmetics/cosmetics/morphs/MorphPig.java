package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
* Represents an instance of a pig morph summoned by a player.
 * 
 * @author 	iSach
 * @since 	08-27-2015
 */
public class MorphPig extends Morph {

    private boolean cooldown = false;

    public MorphPig(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MorphType.PIG, ultraCosmetics);
    }

    @Override
    public void onUpdate() {
        for(Entity ent : getPlayer().getNearbyEntities(0.2, 0.2, 0.2)) {
            if(ent instanceof Creature || ent instanceof Player) {
                if(!ent.hasMetadata("Mount")
                        && !ent.hasMetadata("Pet")
                        && ent != getPlayer()
                        && ent != disguise.getEntity()
                        && !cooldown) {
                    cooldown = true;
                    Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> cooldown = false, 20);
                    SoundUtil.playSound(getPlayer(), Sounds.PIG_IDLE, .2f, 1.5f);
                    Vector v = new Vector(0, 0.6, 0);
                    Vector vEnt = ent.getLocation().toVector().subtract(getPlayer().getLocation().toVector()).add(v);
                    Vector vPig = getPlayer().getLocation().toVector().subtract(ent.getLocation().toVector()).add(v);
                    vEnt.setY(0.5);
                    vPig.setY(0.5);
                    MathUtils.applyVelocity(ent, vEnt.multiply(0.75));
                    MathUtils.applyVelocity(getPlayer(), vPig.multiply(0.75));
                }
            }
        }
    }
}
