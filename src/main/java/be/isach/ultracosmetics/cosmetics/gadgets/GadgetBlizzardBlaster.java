package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 08/08/15.
 */
public class GadgetBlizzardBlaster extends Gadget {

    GadgetBlizzardBlaster instance;
    Random r = new Random();
    List<Entity> cooldownJump = new ArrayList<>();
    List<ArmorStand> armorStands = new ArrayList<>();

    public GadgetBlizzardBlaster(UUID owner) {
        super(Material.PACKED_ICE, (byte) 0x0, "BlizzardBlaster", "ultracosmetics.gadgets.blizzardblaster", 5, owner, GadgetType.BLIZZARD_BLASTER, "&7&oLet it go!");
        instance = this;
    }

    @Override
    void onInteractRightClick() {
        final Vector v = getPlayer().getLocation().getDirection().normalize().multiply(0.3);
        v.setY(0);
        final Location loc = getPlayer().getLocation().subtract(0, 1, 0).add(v);
        final int i = Bukkit.getScheduler().runTaskTimer(Core.getPlugin(), new BukkitRunnable() {
            @Override
            public void run() {
                if (Core.getCustomPlayer(getPlayer()).currentGadget != instance) {
                    cancel();
                    return;
                }
                if (loc.getBlock().getType() != Material.AIR
                        && net.minecraft.server.v1_8_R3.Block.getById(loc.getBlock().getTypeId()).getMaterial().isSolid()) {
                    loc.add(0, 1, 0);
                }
                if (loc.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
                    if (loc.clone().getBlock().getTypeId() != 43 && loc.clone().getBlock().getTypeId() != 44)
                        loc.add(0, -1, 0);
                }
                for (int i = 0; i < 3; i++) {
                    final ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(0, .5) - 0.75, MathUtils.randomDouble(-1.5, 1.5)), EntityType.ARMOR_STAND);
                    as.setVisible(false);
                    as.setSmall(true);
                    as.setGravity(false);
                    as.setHelmet(new ItemStack(Material.PACKED_ICE));
                    as.setHeadPose(new EulerAngle(r.nextInt(50), r.nextInt(50), r.nextInt(50)));
                    armorStands.add(as);
                    UtilParticles.display(Particles.CLOUD, loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(0, .5) - 0.75, MathUtils.randomDouble(-1.5, 1.5)), 2, 0.4f);
                    Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            armorStands.remove(as);
                            as.remove();
                        }
                    }, 20);
                    if (affectPlayers)
                        for (final Entity ent : as.getNearbyEntities(0.5, 0.5, 0.5)) {
                            if (!cooldownJump.contains(ent) && ent != getPlayer()) {
                                MathUtils.applyVelocity(ent, new Vector(0, 1, 0).add(v));
                                cooldownJump.add(ent);
                                Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                                    @Override
                                    public void run() {
                                        cooldownJump.remove(ent);
                                    }
                                }, 20);
                            }
                        }
                }
                loc.add(v);
            }
        }, 0, 1).getTaskId();

        Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().cancelTask(i);
            }
        }, 40);

    }

    @Override
    void onInteractLeftClick() {

    }

    @Override
    void onUpdate() {

    }

    @Override
    public void onClear() {
        for (ArmorStand as : armorStands)
            as.remove();
        HandlerList.unregisterAll(this);
    }
}
