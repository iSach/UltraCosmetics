<<<<<<< HEAD
package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.util.*;
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
import java.util.Random;

/**
 * Created by sacha on 08/08/15.
 */
public class GadgetExplosiveSheep extends Gadget {

    public static final List<GadgetExplosiveSheep> EXPLOSIVE_SHEEP = new ArrayList<>();

    private ArrayList<Sheep> sheepArrayList = new ArrayList<>();

    public GadgetExplosiveSheep(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.EXPLOSIVESHEEP, ultraCosmetics);
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
    void onLeftClick() {

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
    protected void onEquip() {

    }

    @Override
    public void onClear() {
        EXPLOSIVE_SHEEP.remove(this);
        HandlerList.unregisterAll(this);
    }

    class SheepColorRunnable extends BukkitRunnable {
        private boolean red;
        private double time;
        private Sheep s;
        private GadgetExplosiveSheep gadgetExplosiveSheep;

        public SheepColorRunnable(double time, boolean red, Sheep s, GadgetExplosiveSheep gadgetExplosiveSheep) {
            this.red = red;
            this.time = time;
            this.s = s;
            this.runTaskLater(UltraCosmeticsData.get().getPlugin(), (int) time);
            this.gadgetExplosiveSheep = gadgetExplosiveSheep;
        }


        @Override
        public void run() {
            if (red) s.setColor(DyeColor.RED);
            else s.setColor(DyeColor.WHITE);
            SoundUtil.playSound(s.getLocation(), Sounds.NOTE_STICKS, 1.4f, 1.5f);
            red = !red;
            time -= 0.2;

            if (time < 0.5) {
                SoundUtil.playSound(s.getLocation(), Sounds.EXPLODE, 1.4f, 1.5f);
                UtilParticles.display(Particles.EXPLOSION_HUGE, s.getLocation());
                for (int i = 0; i < 50; i++) {
                    final Sheep sheep = getPlayer().getWorld().spawn(s.getLocation(), Sheep.class);
                    try {
                        sheep.setColor(DyeColor.values()[MathUtils.randomRangeInt(0, 15)]);
                    } catch (Exception exc) {
                    }
                    Random r = new Random();
                    MathUtils.applyVelocity(sheep, new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2, r.nextDouble() - 0.5).multiply(2).add(new Vector(0, 0.8, 0)));
                    sheep.setBaby();
                    sheep.setAgeLock(true);
                    sheep.setNoDamageTicks(120);
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
                cancel();
            } else {
                Bukkit.getScheduler().cancelTask(getTaskId());
                new SheepColorRunnable(time, red, s, gadgetExplosiveSheep);
            }
        }

    }

}


=======
package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.util.*;
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
import java.util.Random;

/**
 * Created by sacha on 08/08/15.
 */
public class GadgetExplosiveSheep extends Gadget {

    public static final List<GadgetExplosiveSheep> EXPLOSIVE_SHEEP = new ArrayList<>();

    private ArrayList<Sheep> sheepArrayList = new ArrayList<>();

    public GadgetExplosiveSheep(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.EXPLOSIVESHEEP, ultraCosmetics);
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
    void onLeftClick() {

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
        EXPLOSIVE_SHEEP.remove(this);
        HandlerList.unregisterAll(this);
    }

    class SheepColorRunnable extends BukkitRunnable {
        private boolean red;
        private double time;
        private Sheep s;
        private GadgetExplosiveSheep gadgetExplosiveSheep;

        public SheepColorRunnable(double time, boolean red, Sheep s, GadgetExplosiveSheep gadgetExplosiveSheep) {
            this.red = red;
            this.time = time;
            this.s = s;
            this.runTaskLater(UltraCosmeticsData.get().getPlugin(), (int) time);
            this.gadgetExplosiveSheep = gadgetExplosiveSheep;
        }


        @Override
        public void run() {
            if (red) s.setColor(DyeColor.RED);
            else s.setColor(DyeColor.WHITE);
            SoundUtil.playSound(s.getLocation(), Sounds.NOTE_STICKS, 1.4f, 1.5f);
            red = !red;
            time -= 0.2;

            if (time < 0.5) {
                SoundUtil.playSound(s.getLocation(), Sounds.EXPLODE, 1.4f, 1.5f);
                UtilParticles.display(Particles.EXPLOSION_HUGE, s.getLocation());
                for (int i = 0; i < 50; i++) {
                    final Sheep sheep = getPlayer().getWorld().spawn(s.getLocation(), Sheep.class);
                    try {
                        sheep.setColor(DyeColor.values()[MathUtils.randomRangeInt(0, 15)]);
                    } catch (Exception exc) {
                    }
                    Random r = new Random();
                    MathUtils.applyVelocity(sheep, new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2, r.nextDouble() - 0.5).multiply(2).add(new Vector(0, 0.8, 0)));
                    sheep.setBaby();
                    sheep.setAgeLock(true);
                    sheep.setNoDamageTicks(120);
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
                cancel();
            } else {
                Bukkit.getScheduler().cancelTask(getTaskId());
                new SheepColorRunnable(time, red, s, gadgetExplosiveSheep);
            }
        }

    }

}


>>>>>>> refs/remotes/origin/master
