package me.isach.ultracosmetics.cosmetics.gadgets;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.util.MathUtils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSheep;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 08/08/15.
 */
public class GadgetExplosiveSheep extends Gadget {

    ArrayList<Sheep> sheepArrayList = new ArrayList<>();

    public GadgetExplosiveSheep(UUID owner) {
        super(Material.SHEARS, (byte) 0x0, "ExplosiveSheep", "ultracosmetics.gadgets.explosivesheep", 40, owner, GadgetType.EXPLOSIVESHEEP);
        Core.registerListener(this);
    }

    @Override
    void onInteractRightClick() {
        Location loc = getPlayer().getLocation().add(getPlayer().getEyeLocation().getDirection().multiply(0.5));
        loc.setY(getPlayer().getLocation().getBlockY() + 1);
        Sheep s = getPlayer().getWorld().spawn(loc, Sheep.class);

        s.setNoDamageTicks(100000);
        sheepArrayList.add(s);

        EntitySheep entitySheep = ((CraftSheep) s).getHandle();

        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(entitySheep.goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(entitySheep.targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(entitySheep.goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(entitySheep.targetSelector, new UnsafeList<PathfinderGoalSelector>());
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        Core.explosiveSheep.add(this);

        new SheepColorRunnable(7, true, s, this);
    }

    @Override
    void onInteractLeftClick() {

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
    void onUpdate() {

    }

    @Override
    public void clear() {
        Core.explosiveSheep.remove(this);
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
            this.runTaskLater(Core.getPlugin(), (int) time);
            this.gadgetExplosiveSheep = gadgetExplosiveSheep;
        }

        @Override
        public void run() {
            if (red) {
                s.setColor(DyeColor.RED);
            } else {
                s.setColor(DyeColor.WHITE);
            }
            s.getWorld().playSound(s.getLocation(), Sound.CLICK, 5, 1);
            red = !red;
            time -= 0.2;

            if (time < 0.5) {
                s.getWorld().playSound(s.getLocation(), Sound.EXPLODE, 2, 1);
                s.getWorld().spigot().playEffect(s.getLocation(), Effect.EXPLOSION_HUGE);
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
                    EntitySheep entitySheep = ((CraftSheep) sheep).getHandle();

                    try {
                        Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
                        bField.setAccessible(true);
                        Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
                        cField.setAccessible(true);
                        bField.set(entitySheep.goalSelector, new UnsafeList<PathfinderGoalSelector>());
                        bField.set(entitySheep.targetSelector, new UnsafeList<PathfinderGoalSelector>());
                        cField.set(entitySheep.goalSelector, new UnsafeList<PathfinderGoalSelector>());
                        cField.set(entitySheep.targetSelector, new UnsafeList<PathfinderGoalSelector>());


                        entitySheep.goalSelector.a(3, new CustomPathFinderGoalPanic(entitySheep, 0.4d));

                        entitySheep.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(1.4D);

                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                    Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            sheep.getWorld().spigot().playEffect(sheep.getLocation(), Effect.LAVA_POP, 0, 0, 0, 0, 0, 0, 5, 32);
                            sheep.remove();
                            Core.explosiveSheep.remove(gadgetExplosiveSheep);
                        }
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


