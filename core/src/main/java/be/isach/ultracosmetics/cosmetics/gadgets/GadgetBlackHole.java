package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by sacha on 17/08/15.
 */
public class GadgetBlackHole extends Gadget {

    Item i;

    public GadgetBlackHole(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.BLACKHOLE, ultraCosmetics);
    }

    @Override
    void onRightClick() {
        if (i != null) {
            i.remove();
            i = null;
        }
        Item item = getPlayer().getWorld().dropItem(getPlayer().getEyeLocation(), ItemFactory.create(Material.STAINED_CLAY, (byte) 0xf, UUID.randomUUID().toString()));
        item.setPickupDelay(Integer.MAX_VALUE);
        item.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.3d));
        i = item;
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), new Runnable() {
            @Override
            public void run() {
                if (i != null) {
                    i.remove();
                    i = null;
                }
            }
        }, 140);
    }

    @Override
    public void onUpdate() {

        if (i != null && i.isOnGround()) {
            int strands = 6;
            int particles = 25;
            float radius = 5;
            float curve = 10;
            double rotation = Math.PI / 4;

            Location location = i.getLocation();
            for (int i = 1; i <= strands; i++) {
                for (int j = 1; j <= particles; j++) {
                    float ratio = (float) j / particles;
                    double angle = curve * ratio * 2 * Math.PI / strands + (2 * Math.PI * i / strands) + rotation;
                    double x = Math.cos(angle) * ratio * radius;
                    double z = Math.sin(angle) * ratio * radius;
                    location.add(x, 0, z);
                    UtilParticles.display(Particles.SMOKE_LARGE, location);
                    location.subtract(x, 0, z);
                }
            }
            if (affectPlayers)
                for (final Entity entity : i.getNearbyEntities(5, 3, 5)) {
                    Vector vector = i.getLocation().toVector().subtract(entity.getLocation().toVector());
                    MathUtils.applyVelocity(entity, vector);
                    Bukkit.getScheduler().runTask(getUltraCosmetics(), new Runnable() {
                        @Override
                        public void run() {
                            if (entity instanceof Player)
                                ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 40));
                        }
                    });
                }
        }
    }

    @Override
    public void onClear() {
        if (i != null)
            i.remove();
        HandlerList.unregisterAll(this);
    }

    @Override
    void onLeftClick() {
    }
}
