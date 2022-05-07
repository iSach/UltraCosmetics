package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XSound;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an instance of a explosive sheep gadget summoned by a player.
 *
 * @author iSach
 * @since 08-08-2015
 */
public class GadgetExplosiveSheep extends Gadget {

    public static final Set<GadgetExplosiveSheep> EXPLOSIVE_SHEEP = new HashSet<>();

    // I know 'sheeps' isn't the plural form of 'sheep' but it's funny
    // and it distinguishes it from the local variables named 'sheep' (singular)
    private Set<Sheep> sheeps = new HashSet<>();
    private BukkitRunnable sheepExplosionRunnable = null;

    public GadgetExplosiveSheep(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("explosivesheep"), ultraCosmetics);
    }

    @Override
    void onRightClick() {
        Location loc = getPlayer().getLocation().add(getPlayer().getEyeLocation().getDirection().multiply(0.5));
        loc.setY(getPlayer().getLocation().getBlockY() + 1);
        Sheep sheep = getPlayer().getWorld().spawn(loc, Sheep.class);

        sheep.setNoDamageTicks(100000);
        sheeps.add(sheep);

        UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearPathfinders(sheep);

        EXPLOSIVE_SHEEP.add(this);

        new SheepColorRunnable(7, true, sheep);
    }

    @Override
    protected boolean checkRequirements(PlayerInteractEvent event) {
        if (GadgetExplosiveSheep.EXPLOSIVE_SHEEP.size() > 0) {
            getPlayer().sendMessage(MessageManager.getMessage("Gadgets.ExplosiveSheep.Already-Active"));
            return false;
        }
        return true;
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent event) {
        if (sheeps.contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (sheeps.contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onClear() {
        for (Sheep sheep : sheeps) {
            sheep.remove();
        }
        EXPLOSIVE_SHEEP.remove(this);
        HandlerList.unregisterAll(this);
        if (sheepExplosionRunnable != null) {
            sheepExplosionRunnable.cancel();
        }
    }

    private class SheepColorRunnable extends BukkitRunnable {
        private boolean red;
        private double time;
        private Sheep s;

        private SheepColorRunnable(double time, boolean red, Sheep s) {
            this.red = red;
            this.time = time;
            this.s = s;
            this.runTaskLater(getUltraCosmetics(), (int) time);
        }

        @Override
        public void run() {
            if (getOwner() == null || getPlayer() == null) {
                cancel();
                return;
            }
            s.setColor(red ? DyeColor.RED : DyeColor.WHITE);
            XSound.BLOCK_NOTE_BLOCK_HAT.play(s.getLocation(), 1.4f, 1.5f);
            red = !red;
            time -= 0.2;

            if (time >= 0.5) {
                new SheepColorRunnable(time, red, s);
                return;
            }
            XSound.ENTITY_GENERIC_EXPLODE.play(s.getLocation(), 1.4f, 1.5f);
            Particles.EXPLOSION_HUGE.display(s.getLocation());
            sheeps.remove(s);
            s.remove();
            for (int i = 0; i < 50; i++) {
                final Sheep sheep = getPlayer().getWorld().spawn(s.getLocation(), Sheep.class);
                sheep.setColor(DyeColor.values()[MathUtils.randomRangeInt(0, 15)]);
                MathUtils.applyVelocity(sheep, new Vector(RANDOM.nextDouble() - 0.5, RANDOM.nextDouble() / 2, RANDOM.nextDouble() - 0.5).multiply(2).add(new Vector(0, 0.8, 0)));
                sheep.setBaby();
                sheep.setAgeLock(true);
                sheep.setNoDamageTicks(120);
                sheeps.add(sheep);
                UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearPathfinders(sheep);
                UltraCosmeticsData.get().getVersionManager().getEntityUtil().makePanic(sheep);
            }
            sheepExplosionRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Sheep sheep : sheeps) {
                        Particles.LAVA.display(sheep.getLocation(), 5);
                        sheep.remove();
                        EXPLOSIVE_SHEEP.remove(GadgetExplosiveSheep.this);
                    }
                    sheepExplosionRunnable = null;
                }
            };
            sheepExplosionRunnable.runTaskLater(getUltraCosmetics(), 110);
        }
    }
}
