package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.util.CustomEntityFirework;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFirework;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sacha on 12/10/15.
 */
public class GadgetQuakeGun extends Gadget {

    List<Firework> fireworkList = new ArrayList<>();

    public GadgetQuakeGun(UUID owner) {
        super(Material.DIAMOND_HOE, (byte) 0x0, "QuakeGun", "ultracosmetics.gadgets.quakegun", 5, owner, GadgetType.QUAKEGUN);
        Core.registerListener(this);
    }

    @Override
    void onInteractRightClick() {
        getPlayer().playSound(getPlayer().getLocation(), Sound.BLAZE_DEATH, 1.5f, 1);

        Location location = getPlayer().getEyeLocation().subtract(0, 0.4, 0);
        Vector vector = location.getDirection();

        for (int i = 0; i < 20; i++) {
            Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            location.add(vector);
            fireworkList.add(firework);

            List<Entity> nearbyEntities = firework.getNearbyEntities(0.5d, 0.5d, 0.5d);

            if (!nearbyEntities.isEmpty()) {
                Entity entity = nearbyEntities.get(0);
                if ((entity instanceof Player || entity instanceof Creature)
                        && entity != getPlayer()) {
                    MathUtils.applyVelocity(entity, new Vector(0, 1, 0));
                    UtilParticles.play(entity.getLocation(), Effect.FLAME, 0, 0, 0, 0, 0, 0.4f, 60);
                    FireworkEffect.Builder builder = FireworkEffect.builder();
                    FireworkEffect effect = builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL_LARGE)
                            .withColor(Color.RED).withFade(Color.ORANGE).build();
                    CustomEntityFirework.spawn(location, effect);
                    break;
                }
            }
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (Firework firework : fireworkList) {
                    PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(((CraftFirework) firework).getHandle().getId());
                    ((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(packet);
                }
            }
        }, 6);
    }

    @Override
    void onInteractLeftClick() {
    }

    @Override
    void onUpdate() {
    }

    @Override
    public void clear() {
    }
}
