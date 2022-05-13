package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.PlayerAffectingCosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XSound;

/**
 * Represents an instance of a pig morph summoned by a player.
 *
 * @author iSach
 * @since 08-27-2015
 */
public class MorphPig extends Morph implements PlayerAffectingCosmetic, Updatable {
    private boolean cooldown = false;

    public MorphPig(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MorphType.valueOf("pig"), ultraCosmetics);
    }

    @Override
    public void onUpdate() {
        if (cooldown || !isAffectingPlayersEnabled()) return;
        for (Entity ent : getPlayer().getNearbyEntities(0.2, 0.2, 0.2)) {
            if (canAffect(ent)) {
                cooldown = true;
                Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> cooldown = false, 20);
                XSound.ENTITY_PIG_AMBIENT.play(getPlayer(), .2f, 1.5f);
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
