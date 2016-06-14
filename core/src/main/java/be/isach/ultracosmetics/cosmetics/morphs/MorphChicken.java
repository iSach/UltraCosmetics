package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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
        super(owner, MorphType.CHICKEN);

        if (owner != null) {

            UltraCosmetics.getInstance().registerListener(this);

            final MorphChicken chicken = this;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (getPlayer() == null
                            || UltraCosmetics.getCustomPlayer(getPlayer()).currentMorph != chicken) {
                        cancel();
                        return;
                    }

                    UltraCosmetics.getInstance().getEntityUtil().chickenFall(getPlayer());

                }
            }.runTaskTimer(UltraCosmetics.getInstance(), 0, 1);
        }
    }


    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer() == getPlayer() && UltraCosmetics.getCustomPlayer(getPlayer()).currentMorph == this && !cooldown) {
            final List<Item> items = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                final Item i = getPlayer().getWorld().dropItem(getPlayer().getLocation(), ItemFactory.create(Material.EGG, (byte) 0x0, UUID.randomUUID().toString()));
                items.add(i);
                Random r = new Random();
                i.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2, r.nextDouble() - 0.5));
                switch (UltraCosmetics.getServerVersion()) {
                    case v1_8_R3:
                        getPlayer().playSound(getPlayer().getLocation(), Sound.valueOf("CHICKEN_EGG_POP"), .5f, 1.5f);
                        break;
                    default:
                        getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_CHICKEN_EGG, .5f, 1.5f);
                        break;
                }
            }
            Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                BukkitRunnable followRunnable;

                @Override
                public void run() {
                    final List<Chicken> chickens = new ArrayList<>();
                    for (Item i : items) {
                        Particles.BLOCK_CRACK.display(new Particles.BlockData(Material.STAINED_CLAY, (byte) 0), 0, 0, 0, 0.3f, 50, i.getLocation(), 128);
                        switch (UltraCosmetics.getServerVersion()) {
                            case v1_8_R3:
                                i.getWorld().playSound(i.getLocation(), Sound.valueOf("ZOMBIE_WOODBREAK"), .05f, 1f);
                                break;
                            default:
                                i.getWorld().playSound(i.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD, .05f, 1f);
                                break;
                        }
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
                                    UltraCosmetics.getInstance().getEntityUtil().follow(getPlayer(), chicken);
                                }
                            } catch (Exception exc) {
                            }
                        }
                    };
                    followRunnable.runTaskTimer(UltraCosmetics.getInstance(), 0, 1);
                    Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            for (Chicken chicken : chickens) {
                                UtilParticles.display(Particles.LAVA, chicken.getLocation(), 10);
                                chicken.remove();
                            }
                            chickens.clear();
                            followRunnable.cancel();
                        }
                    }, 200);
                }
            }, 50);
            cooldown = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
                @Override
                public void run() {
                    cooldown = false;
                }
            }, 30 * 20);
        }
    }
}
