package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;
import be.isach.ultracosmetics.util.UtilParticles;

import org.bukkit.Bukkit;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance of a explosive sheep gadget summoned by a player.
 *
 * @author iSach
 * @since 08-08-2015
 */
public class GadgetExplosiveSheep extends Gadget {

    public static final List<GadgetExplosiveSheep> EXPLOSIVE_SHEEP = new ArrayList<>();

    private ArrayList<Sheep> sheepArrayList = new ArrayList<>();

    public GadgetExplosiveSheep(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("explosivesheep"), ultraCosmetics);
    }

    @Override
    void onRightClick() {
        Location loc = getPlayer().getLocation().add(getPlayer().getEyeLocation().getDirection().multiply(0.5));
        loc.setY(getPlayer().getLocation().getBlockY() + 1);
        Sheep s = getPlayer().getWorld().spawn(loc, Sheep.class);

        s.setNoDamageTicks(100000);
        sheepArrayList.add(s);

        UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearPathfinders(s);

        EXPLOSIVE_SHEEP.add(this);

        new SheepColorRunnable(7, true, s, this);
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
        if (sheepArrayList.contains(event.getEntity()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onShear(EntityDamageEvent event) {
        if (sheepArrayList.contains(event.getEntity()))
            event.setCancelled(true);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onClear() {
        for (Sheep sheep : sheepArrayList) {
            sheep.remove();
        }
        EXPLOSIVE_SHEEP.remove(this);
        HandlerList.unregisterAll(this);
    }

    private class SheepColorRunnable extends BukkitRunnable {
        private boolean red;
        private double time;
        private Sheep s;
        private GadgetExplosiveSheep gadgetExplosiveSheep;

        private SheepColorRunnable(double time, boolean red, Sheep s, GadgetExplosiveSheep gadgetExplosiveSheep) {
            this.red = red;
            this.time = time;
            this.s = s;
            this.runTaskLater(getUltraCosmetics(), (int) time);
            this.gadgetExplosiveSheep = gadgetExplosiveSheep;
        }

        @Override
        public void run() {
            if (getOwner() == null || getPlayer() == null) {
                cancel();
                return;
            }
            s.setColor(red ? DyeColor.RED : DyeColor.WHITE);
            SoundUtil.playSound(s.getLocation(), Sounds.NOTE_STICKS, 1.4f, 1.5f);
            red = !red;
            time -= 0.2;

            if (time >= 0.5) {
                new SheepColorRunnable(time, red, s, gadgetExplosiveSheep);
                return;
            }
            SoundUtil.playSound(s.getLocation(), Sounds.EXPLODE, 1.4f, 1.5f);
            UtilParticles.display(Particles.EXPLOSION_HUGE, s.getLocation());
            for (int i = 0; i < 50; i++) {
                if (getOwner() == null || getPlayer() == null) {
                    return;
                }
                final Sheep sheep = getPlayer().getWorld().spawn(s.getLocation(), Sheep.class);
                sheep.setColor(DyeColor.values()[MathUtils.randomRangeInt(0, 15)]);
                MathUtils.applyVelocity(sheep, new Vector(RANDOM.nextDouble() - 0.5, RANDOM.nextDouble() / 2, RANDOM.nextDouble() - 0.5).multiply(2).add(new Vector(0, 0.8, 0)));
                sheep.setBaby();
                sheep.setAgeLock(true);
                sheep.setNoDamageTicks(120);
                sheepArrayList.add(sheep);
                UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearPathfinders(sheep);
                UltraCosmeticsData.get().getVersionManager().getEntityUtil().makePanic(sheep);
                Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
                    UtilParticles.display(Particles.LAVA, sheep.getLocation(), 5);
                    sheep.remove();
                    EXPLOSIVE_SHEEP.remove(gadgetExplosiveSheep);
                }, 110);
            }
            sheepArrayList.remove(s);
            s.remove();
        }
    }
}
