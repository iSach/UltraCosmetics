package me.isach.ultracosmetics.cosmetics.gadgets;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.util.ItemFactory;
import me.isach.ultracosmetics.util.MathUtils;
import me.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by sacha on 17/08/15.
 */
public class GadgetBlackHole extends Gadget {

    Item i;

    public GadgetBlackHole(UUID owner) {
        super(Material.STAINED_CLAY, (byte) 0xf, "BlackHole", "ultracosmetics.gadgets.blackhole", 45, owner, GadgetType.BLACKHOLE);
    }

    @Override
    void onInteractRightClick() {
        if (i != null) {
            i.remove();
            i = null;
        }
        Item item = getPlayer().getWorld().dropItem(getPlayer().getEyeLocation(), ItemFactory.create(Material.STAINED_CLAY, (byte) 0xf, UUID.randomUUID().toString()));
        item.setPickupDelay(Integer.MAX_VALUE);
        item.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.3d));
        i = item;
        Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
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
    void onUpdate() {
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
                    UtilParticles.play(location, Effect.LARGE_SMOKE);
                    location.subtract(x, 0, z);
                }
            }
            for (Entity ent : i.getNearbyEntities(5, 3, 5)) {
                Vector vector = i.getLocation().toVector().subtract(ent.getLocation().toVector());
                MathUtils.applyVelocity(ent, vector);
                if (ent instanceof Player)
                    ((Player) ent).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 40));
            }
        }
    }

    @Override
    public void clear() {
        if (i != null)
            i.remove();
    }

    @Override
    void onInteractLeftClick() {
    }
}
