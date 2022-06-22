package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.PlayerAffectingCosmetic;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XSound;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance of a quake gun gadget summoned by a player.
 *
 * @author iSach
 * @since 10-12-2015
 */
public class GadgetQuakeGun extends Gadget implements PlayerAffectingCosmetic {
    private static final FireworkEffect FIREWORK_EFFECT;
    static {
        FIREWORK_EFFECT = FireworkEffect.builder().flicker(false).trail(false)
                .with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).withFade(Color.ORANGE).build();
    }

    private List<Firework> fireworkList = new ArrayList<>();

    public GadgetQuakeGun(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("quakegun"), ultraCosmetics);
    }

    @Override
    void onRightClick() {
        XSound.ENTITY_BLAZE_DEATH.play(getPlayer(), 1.4f, 1.5f);

        Location location = getPlayer().getEyeLocation().subtract(0, 0.4, 0);
        Vector vector = location.getDirection();

        for (int i = 0; i < 20; i++) {
            Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            location.add(vector);
            fireworkList.add(firework);

            List<Entity> nearbyEntities = firework.getNearbyEntities(0.5d, 0.5d, 0.5d);

            for (Entity entity : nearbyEntities) {
                if ((entity instanceof Player || entity instanceof Creature)
                        && entity != getPlayer() && canAffect(entity)) {
                    MathUtils.applyVelocity(entity, new Vector(0, 1, 0));
                    Particles.FLAME.display(entity.getLocation(), 60, 0.4f);
                    UltraCosmeticsData.get().getVersionManager().getFireworkFactory().spawn(location, FIREWORK_EFFECT);
                }
            }
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), () -> {
            for (Firework firework : fireworkList) {
                UltraCosmeticsData.get().getVersionManager().getEntityUtil().sendDestroyPacket(getPlayer(), firework);
            }
        }, 6);
    }
}
