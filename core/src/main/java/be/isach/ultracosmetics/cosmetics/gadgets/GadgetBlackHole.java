package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Represents an instance of a blackhole gadget summoned by a player.
 *
 * @author iSach
 * @since 08-17-2015
 */
public class GadgetBlackHole extends Gadget {

    private Item item;

    public GadgetBlackHole(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("blackhole"), ultraCosmetics);
    }

    @Override
    void onRightClick() {
        if (item != null) {
            item.remove();
            item = null;
        }

        Item newItem = getPlayer().getWorld().dropItem(getPlayer().getEyeLocation(), ItemFactory.createColored("STAINED_CLAY", (byte) 0xf, UltraCosmeticsData.get().getItemNoPickupString()));
        newItem.setPickupDelay(Integer.MAX_VALUE);
        newItem.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.3d));
        this.item = newItem;
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            if (item != null) {
                item.remove();
                item = null;
            }
        }, 140);
    }

    @Override
    public void onUpdate() {
        if (item != null && item.isOnGround()) {
            int strands = 6;
            int particles = 25;
            float radius = 5;
            float curve = 10;
            double rotation = Math.PI / 4;

            Location location = item.getLocation();
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

            if (affectPlayers && item != null)
                for (final Entity entity : item.getNearbyEntities(5, 3, 5)) {
                    Vector vector = item.getLocation().toVector().subtract(entity.getLocation().toVector());
                    MathUtils.applyVelocity(entity, vector);
                    Bukkit.getScheduler().runTask(getUltraCosmetics(), () -> {
                        if (entity instanceof Player)
                            ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 40));
                    });
                }
        }
    }

    @Override
    public void onClear() {
        if (item != null) {
            item.remove();
        }
    }

    @Override
    void onLeftClick() {
    }
}
