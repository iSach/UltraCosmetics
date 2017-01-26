package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
* Represents an instance of a quake gun gadget summoned by a player.
 * 
 * @author 	iSach
 * @since 	10-12-2015
 */
public class GadgetQuakeGun extends Gadget {

    List<Firework> fireworkList = new ArrayList<>();

    public GadgetQuakeGun(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.QUAKEGUN, ultraCosmetics);
    }

    @Override
    void onRightClick() {
        SoundUtil.playSound(getPlayer(), Sounds.BLAZE_DEATH, 1.4f, 1.5f);

        Location location = getPlayer().getEyeLocation().subtract(0, 0.4, 0);
        Vector vector = location.getDirection();

        for (int i = 0; i < 20; i++) {
            Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            location.add(vector);
            fireworkList.add(firework);

            List<Entity> nearbyEntities = firework.getNearbyEntities(0.5d, 0.5d, 0.5d);

            if (affectPlayers)
                if (!nearbyEntities.isEmpty()) {
                    Entity entity = nearbyEntities.get(0);
                    if ((entity instanceof Player || entity instanceof Creature)
                            && entity != getPlayer()) {
                        MathUtils.applyVelocity(entity, new Vector(0, 1, 0));
                        UtilParticles.display(Particles.FLAME, entity.getLocation(), 60, 0.4f);
                        FireworkEffect.Builder builder = FireworkEffect.builder();
                        FireworkEffect effect = builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL_LARGE)
                                .withColor(Color.RED).withFade(Color.ORANGE).build();
                        UltraCosmeticsData.get().getVersionManager().getFireworkFactory().spawn(location, effect);
                    }
                }
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), new Runnable() {
            @Override
            public void run() {
                for (Firework firework : fireworkList)
                    UltraCosmeticsData.get().getVersionManager().getEntityUtil().sendDestroyPacket(getPlayer(), firework);
            }
        }, 6);
    }

    @Override
    void onLeftClick() {
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onClear() {
    }
}
