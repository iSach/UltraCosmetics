package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.PlayerAffectingCosmetic;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance of a tsunami gadget summoned by a player.
 *
 * @author iSach
 * @since 08-08-2015
 */
public class GadgetTsunami extends Gadget implements PlayerAffectingCosmetic {

    private final List<Entity> cooldownJump = new ArrayList<>();

    public GadgetTsunami(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("tsunami"), ultraCosmetics);
    }

    @Override
    void onRightClick() {
        final Vector v = getPlayer().getLocation().getDirection().normalize().multiply(0.3);
        v.setY(0);
        final Location loc = getPlayer().getLocation().subtract(0, 1, 0).add(v);
        final int i = Bukkit.getScheduler().runTaskTimerAsynchronously(getUltraCosmetics(), () -> {
            if (loc.getBlock().getType().isSolid()) {
                loc.add(0, 1, 0);
            }
            if (BlockUtils.isAir(loc.clone().subtract(0, 1, 0).getBlock().getType())) {
                loc.add(0, -1, 0);
            }
            Location loc1 = loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(0, .5) - 0.75, MathUtils.randomDouble(-1.5, 1.5));
            Location loc2 = loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(1.3, 1.8) - 0.75, MathUtils.randomDouble(-1.5, 1.5));
            for (int i1 = 0; i1 < 5; i1++) {
                Particles.EXPLOSION_NORMAL.display(0.2d, 0.2d, 0.2d, loc1, 1);
                Particles.DRIP_WATER.display(0.4d, 0.4d, 0.4d, loc2, 2);
            }
            for (int a = 0; a < 100; a++) {
                Particles.REDSTONE.display(0, 0, 255, loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(1, 1.6) - 0.75, MathUtils.randomDouble(-1.5, 1.5)));
            }
            if (isAffectingPlayersEnabled()) {
                Bukkit.getScheduler().runTask(getUltraCosmetics(), () -> {
                    for (final Entity ent : getPlayer().getWorld().getNearbyEntities(loc, 0.6, 0.6, 0.6)) {
                        if (!cooldownJump.contains(ent) && ent != getPlayer() && canAffect(ent)) {
                            MathUtils.applyVelocity(ent, new Vector(0, 1, 0).add(v.clone().multiply(2)));
                            cooldownJump.add(ent);
                            Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> cooldownJump.remove(ent), 20);
                        }
                    }
                });
            }
            loc.add(v);
        }, 0, 1).getTaskId();
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> Bukkit.getScheduler().cancelTask(i), 40);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onClear() {
        HandlerList.unregisterAll(this);
    }
}
