package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.PacketSender;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
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
    List<EntityArmorStand> fakeArmorStands = new ArrayList<>();

    public GadgetBlizzardBlaster(UUID owner) {
        super(owner, GadgetType.BLIZZARDBLASTER);
        instance = this;
    }

    @Override
    void onRightClick() {
        final Vector v = getPlayer().getLocation().getDirection().normalize().multiply(0.3);
        v.setY(0);
        final Location loc = getPlayer().getLocation().subtract(0, 1, 0).add(v);
        final int i = Bukkit.getScheduler().runTaskTimerAsynchronously(UltraCosmetics.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                if (UltraCosmetics.getCustomPlayer(getPlayer()).currentGadget != instance) {
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
                    final EntityArmorStand as = new EntityArmorStand(((CraftWorld) getPlayer().getWorld()).getHandle());
                    as.setInvisible(true);
                    as.setSmall(true);
                    as.setGravity(false);
                    as.setArms(true);
                    as.setHeadPose(new Vector3f((float) (r.nextInt(360)),
                            (float) (r.nextInt(360)),
                            (float) (r.nextInt(360))));
                    as.setLocation(loc.getX() + MathUtils.randomDouble(-1.5, 1.5), loc.getY() + MathUtils.randomDouble(0, .5) - 0.75, loc.getZ() + MathUtils.randomDouble(-1.5, 1.5), 0, 0);
                    fakeArmorStands.add(as);
                    for (Player player : getPlayer().getWorld().getPlayers()) {
                        PacketSender.send(player, new PacketPlayOutSpawnEntityLiving(as));
                        PacketSender.send(player, new PacketPlayOutEntityEquipment(as.getId(), 4, CraftItemStack.asNMSCopy(new ItemStack(Material.PACKED_ICE))));
                    }
                    UtilParticles.display(Particles.CLOUD, loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(0, .5) - 0.75, MathUtils.randomDouble(-1.5, 1.5)), 2, 0.4f);
                    Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            for (Player player : getPlayer().getWorld().getPlayers())
                                PacketSender.send(player, new PacketPlayOutEntityDestroy(as.getId()));
                            fakeArmorStands.remove(as);
                        }
                    }, 20);
                    if (affectPlayers)
                        for (final Entity ent : as.getBukkitEntity().getNearbyEntities(0.5, 0.5, 0.5)) {
                            if (!cooldownJump.contains(ent) && ent != getPlayer()) {
                                MathUtils.applyVelocity(ent, new Vector(0, 1, 0).add(v));
                                cooldownJump.add(ent);
                                Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
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

        Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().cancelTask(i);
            }
        }, 40);

    }

    @Override
    void onLeftClick() {

    }

    @Override
    void onUpdate() {

    }

    @Override
    public void onClear() {
        for (EntityArmorStand as : fakeArmorStands)
            for (Player player : getPlayer().getWorld().getPlayers())
                PacketSender.send(player, new PacketPlayOutEntityDestroy(as.getId()));
        fakeArmorStands.clear();
        fakeArmorStands = null;
        HandlerList.unregisterAll(this);
    }
}
