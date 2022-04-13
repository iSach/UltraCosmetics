package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents an instance of a chicken morph summoned by a player.
 *
 * @author iSach
 * @since 08-27-2015
 */
public class MorphChicken extends Morph {

    private List<Item> items = new ArrayList<>();
    private List<Chicken> chickens = new ArrayList<>();
    private boolean cooldown;

    public MorphChicken(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MorphType.valueOf("chicken"), ultraCosmetics);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer() == getPlayer() && getOwner().getCurrentMorph() == this && !cooldown) {
            items = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                final Item i = getPlayer().getWorld().dropItem(getPlayer().getLocation(), ItemFactory.create(XMaterial.EGG, UltraCosmeticsData.get().getItemNoPickupString()));
                i.setMetadata("UNPICKABLEUP", new FixedMetadataValue(getUltraCosmetics(), ""));
                items.add(i);
                Random r = new Random();
                i.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2, r.nextDouble() - 0.5).multiply(0.5));
                XSound.ENTITY_CHICKEN_EGG.play(getPlayer(), .5f, 1.5f);
            }
            Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), new Runnable() {
                BukkitRunnable followRunnable;

                @Override
                public void run() {
                    chickens = new ArrayList<>();
                    for (Item i : items) {
                        if (UltraCosmeticsData.get().getServerVersion().isAtLeast(ServerVersion.v1_14_R1)) {
                            i.getWorld().spawnParticle(Particle.BLOCK_CRACK, i.getLocation(), 0, 0, 0, 0, 0, XMaterial.WHITE_TERRACOTTA.parseMaterial().createBlockData());
                        } else {
                            Particles.BLOCK_CRACK.display(new Particles.BlockData(XMaterial.WHITE_TERRACOTTA.parseMaterial(), (byte) 0), 0, 0, 0, 0.3f, 50, i.getLocation(), 128);
                        }
                        XSound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR.play(i.getLocation(), .05f, 1f);
                        final Chicken chicken = (Chicken) i.getWorld().spawnEntity(i.getLocation(), EntityType.CHICKEN);
                        chicken.setAgeLock(true);
                        chicken.setBaby();
                        chicken.setNoDamageTicks(Integer.MAX_VALUE);
                        chicken.setVelocity(new Vector(0, 0.5f, 0));
                        UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearPathfinders(chicken);
                        UltraCosmeticsData.get().getVersionManager().getEntityUtil().follow(getPlayer(), chicken);
                        i.remove();
                        chickens.add(chicken);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (chickens.isEmpty()) {
                                cancel();
                            }
                            for (Chicken chicken : chickens) {
                                UltraCosmeticsData.get().getVersionManager().getEntityUtil().follow(getPlayer(), chicken);
                            }
                        }
                    }.runTaskTimer(getUltraCosmetics(), 0, 4);
                    Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
                        for (Chicken chicken : chickens) {
                            Particles.LAVA.display(chicken.getLocation(), 10);
                            chicken.remove();
                        }
                        chickens.clear();
                        if (followRunnable != null) {
                            followRunnable.cancel();
                        }
                    }, 200);
                }
            }, 50);
            cooldown = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), () -> cooldown = false, 30 * 20);
        }
    }

    /**
     * Cancel eggs from merging
     *
     * @param event
     */
    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {
        if (items.contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }


    @Override
    protected void onClear() {
        for (Chicken chicken : chickens) {
            Particles.LAVA.display(chicken.getLocation(), 10);
            chicken.remove();
        }
        chickens.clear();
    }

    @Override
    public void onUpdate() {
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().chickenFall(getPlayer());
    }
}
