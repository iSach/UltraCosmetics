package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by sacha on 08/08/15.
 */
public class GadgetTsunami extends Gadget {

    List<Entity> cooldownJump = new ArrayList<>();

    public GadgetTsunami(UUID owner) {
        super(owner, GadgetType.TSUNAMI);
    }

    @Override
    void onRightClick() {
        final Vector v = getPlayer().getLocation().getDirection().normalize().multiply(0.3);
        v.setY(0);
        final Location loc = getPlayer().getLocation().subtract(0, 1, 0).add(v);
        final int i = Bukkit.getScheduler().runTaskTimerAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (loc.getBlock().getType() != Material.AIR
                        && loc.getBlock().getType().isSolid())
                    loc.add(0, 1, 0);
                if (loc.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR)
                    loc.add(0, -1, 0);
                Location loc1 = loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(0, .5) - 0.75, MathUtils.randomDouble(-1.5, 1.5));
                Location loc2 = loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(1.3, 1.8) - 0.75, MathUtils.randomDouble(-1.5, 1.5));
                for (int i = 0; i < 5; i++) {
                    UtilParticles.display(Particles.EXPLOSION_NORMAL, 0.2d, 0.2d, 0.2d, loc1, 1);
                    UtilParticles.display(Particles.DRIP_WATER, 0.4d, 0.4d, 0.4d, loc2, 2);
                }
                for (int a = 0; a < 100; a++)
                    UtilParticles.display(0, 0, 255, loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(1, 1.6) - 0.75, MathUtils.randomDouble(-1.5, 1.5)));
                if (affectPlayers)
                    for (final Entity ent : getPlayer().getWorld().getEntities()) {
                        if (ent.getLocation().distance(loc) < 0.6 &&
                                !cooldownJump.contains(ent) &&
                                ent != getPlayer() && !(ent instanceof ArmorStand)) {
                            MathUtils.applyVelocity(ent, new Vector(0, 1, 0).add(v.clone().multiply(2)));
                            cooldownJump.add(ent);
                            Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                                @Override
                                public void run() {
                                    cooldownJump.remove(ent);
                                }
                            }, 20);
                        }
                    }

                loc.add(v);
            }
        }, 0, 1).getTaskId();

        Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().cancelTask(i);
            }
        }, 40);

    }

    @Override
    void onLeftClick() {

    }

    @Override
    void onUpdate() {

    }

    @Override
    public void onClear() {
        HandlerList.unregisterAll(this);
    }
}
