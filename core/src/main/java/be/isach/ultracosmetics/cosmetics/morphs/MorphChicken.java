package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
* Represents an instance of a chicken morph summoned by a player.
 * 
 * @author 	iSach
 * @since 	08-27-2015
 */
public class MorphChicken extends Morph {

    private boolean cooldown;

    public MorphChicken(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MorphType.CHICKEN, ultraCosmetics);

        if (owner != null) {
            final MorphChicken chicken = this;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (getPlayer() == null
                            || getOwner().getCurrentMorph() != chicken) {
                        cancel();
                        return;
                    }
                    UltraCosmeticsData.get().getVersionManager().getEntityUtil().chickenFall(getPlayer());
                }
            }.runTaskTimer(getUltraCosmetics(), 0, 1);
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer() == getPlayer() && getOwner().getCurrentMorph() == this && !cooldown) {
            final List<Item> items = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                final Item i = getPlayer().getWorld().dropItem(getPlayer().getLocation(), ItemFactory.create(Material.EGG, (byte) 0x0, UUID.randomUUID().toString()));
                items.add(i);
                Random r = new Random();
                i.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2, r.nextDouble() - 0.5));
                SoundUtil.playSound(getPlayer(), Sounds.CHICKEN_EGG_POP, .5f, 1.5f);
            }
            Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), new Runnable() {
                BukkitRunnable followRunnable;

                @Override
                public void run() {
                    final List<Chicken> chickens = new ArrayList<>();
                    for (Item i : items) {
                        Particles.BLOCK_CRACK.display(new Particles.BlockData(Material.STAINED_CLAY, (byte) 0), 0, 0, 0, 0.3f, 50, i.getLocation(), 128);
                        SoundUtil.playSound(i.getLocation(), Sounds.ZOMBIE_WOOD, .05f, 1f);
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
                                    UltraCosmeticsData.get().getVersionManager().getEntityUtil().follow(getPlayer(), chicken);
                                }
                            } catch (Exception exc) {
                            }
                        }
                    };
                    followRunnable.runTaskTimer(getUltraCosmetics(), 0, 1);
                    Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), new Runnable() {
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
            Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), new Runnable() {
                @Override
                public void run() {
                    cooldown = false;
                }
            }, 30 * 20);
        }
    }

    @Override
    protected void onEquip() {
    }
}
