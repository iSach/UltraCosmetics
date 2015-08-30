package me.isach.ultracosmetics.cosmetics.morphs;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.util.ItemFactory;
import me.isach.ultracosmetics.util.UtilParticles;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PathEntity;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 27/08/15.
 */
public class MorphChicken extends Morph {

    private boolean cooldown;

    public MorphChicken(UUID owner) {
        super(DisguiseType.CHICKEN, Material.EGG, (byte) 0x0, "Chicken", "ultracosmetics.morphs.chicken", owner, MorphType.CHICKEN);

        if (owner != null) {

            Core.registerListener(this);

            final MorphChicken chicken = this;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (getPlayer() == null
                            || Core.getCustomPlayer(getPlayer()).currentMorph != chicken) {
                        cancel();
                        return;
                    }

                    EntityPlayer entityPlayer = ((CraftPlayer) getPlayer()).getHandle();
                    if (!entityPlayer.onGround && entityPlayer.motY < 0.0D) {
                        Vector v = getPlayer().getVelocity();
                        getPlayer().setVelocity(v);
                        entityPlayer.motY *= 0.85;
                    }

                }
            }.runTaskTimer(Core.getPlugin(), 0, 1);
        }
    }


    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer() == getPlayer() && Core.getCustomPlayer(getPlayer()).currentMorph == this && !cooldown) {
            final List<Item> items = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                final Item i = getPlayer().getWorld().dropItem(getPlayer().getLocation(), ItemFactory.create(Material.EGG, (byte) 0x0, UUID.randomUUID().toString()));
                items.add(i);
                Random r = new Random();
                i.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2, r.nextDouble() - 0.5));
                i.getWorld().playSound(i.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
            }
            Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                BukkitRunnable followRunnable;

                @Override
                public void run() {
                    final List<Chicken> chickens = new ArrayList<>();
                    for (Item i : items) {
                        UtilParticles.play(i.getLocation(), Effect.STEP_SOUND, Material.STAINED_CLAY.getId(), 0, 0, 0, 0, 0.3f, 50);
                        i.getWorld().playSound(i.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.05f, 1);
                        final Chicken chicken = (Chicken) i.getWorld().spawnEntity(i.getLocation(), EntityType.CHICKEN);
                        chicken.setAgeLock(true);
                        chicken.setBaby();
                        chicken.setNoDamageTicks(Integer.MAX_VALUE);
                        chicken.setVelocity(new Vector(0, 0.5f, 0));
                        i.remove();
                        chickens.add(chicken);
                    }
                    followRunnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                for (Chicken chicken : chickens) {
                                    net.minecraft.server.v1_8_R3.Entity pett = ((CraftEntity) chicken).getHandle();
                                    ((EntityInsentient) pett).getNavigation().a(2);
                                    Object petf = ((CraftEntity) chicken).getHandle();
                                    Location targetLocation = getPlayer().getLocation();
                                    PathEntity path;
                                    path = ((EntityInsentient) petf).getNavigation().a(targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ() + 1);
                                    if (path != null) {
                                        ((EntityInsentient) petf).getNavigation().a(path, 1.05D);
                                        ((EntityInsentient) petf).getNavigation().a(1.05D);
                                    }
                                }
                            } catch (Exception exc) {
                            }
                        }
                    };
                    followRunnable.runTaskTimer(Core.getPlugin(), 0, 1);
                    Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            for (Chicken chicken : chickens) {
                                UtilParticles.play(chicken.getLocation(), Effect.LAVA_POP, 0, 0, 0, 0, 0, 1, 10);
                                chicken.remove();
                            }
                            chickens.clear();
                            followRunnable.cancel();
                        }
                    }, 200);
                }
            }, 50);
            cooldown = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    cooldown = false;
                }
            }, 30 * 20);
        }
    }
}
