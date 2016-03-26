package be.isach.ultracosmetics.util.v1_8_R3;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.gadgets.v1_8_R3.CustomPathFinderGoalPanic;
import be.isach.ultracosmetics.util.*;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Sacha on 14/03/16.
 */
public class EntityUtil_1_8_R3 implements IEntityUtil {

    @Override
    public void setPassenger(org.bukkit.entity.Entity vehicle, org.bukkit.entity.Entity passenger) {
        vehicle.setPassenger(passenger);
    }

    @Override
    public void resetWitherSize(Wither wither) {
        ((CraftWither) wither).getHandle().r(600);
    }

    @Override
    public void setHorseSpeed(Horse horse, double speed) {
        ((CraftHorse) horse).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed);
    }

    Random r = new Random();
    Map<Player, List<EntityArmorStand>> fakeArmorStandsMap = new HashMap<>();
    Map<Player, List<org.bukkit.entity.Entity>> cooldownJumpMap = new HashMap<>();

    @Override
    public void sendBlizzard(final Player player, Location loc, boolean affectPlayers, Vector v) {
        if (!fakeArmorStandsMap.containsKey(player))
            fakeArmorStandsMap.put(player, new ArrayList<EntityArmorStand>());
        if (!cooldownJumpMap.containsKey(player))
            cooldownJumpMap.put(player, new ArrayList<org.bukkit.entity.Entity>());

        final List<EntityArmorStand> fakeArmorStands = fakeArmorStandsMap.get(player);
        final List<org.bukkit.entity.Entity> cooldownJump = cooldownJumpMap.get(player);

        final EntityArmorStand as = new EntityArmorStand(((CraftWorld) player.getWorld()).getHandle());
        as.setInvisible(true);
        as.setSmall(true);
        as.setGravity(false);
        as.setArms(true);
        as.setHeadPose(new Vector3f((float) (r.nextInt(360)),
                (float) (r.nextInt(360)),
                (float) (r.nextInt(360))));
        as.setLocation(loc.getX() + MathUtils.randomDouble(-1.5, 1.5), loc.getY() + MathUtils.randomDouble(0, .5) - 0.75, loc.getZ() + MathUtils.randomDouble(-1.5, 1.5), 0, 0);
        fakeArmorStands.add(as);
        for (Player players : player.getWorld().getPlayers()) {
            PacketSender.send(players, new PacketPlayOutSpawnEntityLiving(as));
            PacketSender.send(players, new PacketPlayOutEntityEquipment(as.getId(), 4, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(org.bukkit.Material.PACKED_ICE))));
        }
        UtilParticles.display(Particles.CLOUD, loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(0, .5) - 0.75, MathUtils.randomDouble(-1.5, 1.5)), 2, 0.4f);
        Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player pl : player.getWorld().getPlayers())
                    PacketSender.send(pl, new PacketPlayOutEntityDestroy(as.getId()));
                fakeArmorStands.remove(as);
            }
        }, 20);
        if (affectPlayers)
            for (final org.bukkit.entity.Entity ent : as.getBukkitEntity().getNearbyEntities(0.5, 0.5, 0.5)) {
                if (!cooldownJump.contains(ent) && ent != player) {
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

    @Override
    public void clearBlizzard(Player player) {
        if (!fakeArmorStandsMap.containsKey(player)) return;

        for (EntityArmorStand as : fakeArmorStandsMap.get(player))
            for (Player pl : player.getWorld().getPlayers())
                PacketSender.send(pl, new PacketPlayOutEntityDestroy(as.getId()));
        fakeArmorStandsMap.remove(player);
        cooldownJumpMap.remove(player);
    }

    @Override
    public void clearPathfinders(org.bukkit.entity.Entity entity) {
        net.minecraft.server.v1_8_R3.EntityInsentient entitySheep = (net.minecraft.server.v1_8_R3.EntityInsentient) ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity) entity).getHandle();

        try {
            Field bField = net.minecraft.server.v1_8_R3.PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = net.minecraft.server.v1_8_R3.PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(entitySheep.goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(entitySheep.targetSelector, new UnsafeList<net.minecraft.server.v1_8_R3.PathfinderGoalSelector>());
            cField.set(entitySheep.goalSelector, new UnsafeList<net.minecraft.server.v1_8_R3.PathfinderGoalSelector>());
            cField.set(entitySheep.targetSelector, new UnsafeList<net.minecraft.server.v1_8_R3.PathfinderGoalSelector>());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public void makePanic(org.bukkit.entity.Entity entity) {
        EntityInsentient insentient = (EntityInsentient) ((CraftEntity) entity).getHandle();
        insentient.goalSelector.a(3, new CustomPathFinderGoalPanic((EntityCreature) insentient, 0.4d));
    }

    @Override
    public void sendDestroyPacket(Player player, org.bukkit.entity.Entity entity) {
       PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(((CraftEntity) entity).getHandle().getId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void move(Creature creature, Location loc) {
        EntityCreature ec = ((CraftCreature) creature).getHandle();
        ec.S = 1;

        if (loc == null) return;

        ec.getNavigation().a(loc.getX(), loc.getY(), loc.getZ(), (1.0D + 2.0D * 0.5d) * 1.0D);
    }

    @Override
    public void moveDragon(Player player, Vector vector, org.bukkit.entity.Entity entity) {
        EntityEnderDragon ec = ((CraftEnderDragon) entity).getHandle();

        ec.hurtTicks = -1;

        ec.getBukkitEntity().setVelocity(vector);

        ec.pitch = player.getLocation().getPitch();
        ec.yaw = player.getLocation().getYaw() - 180;
    }

    @Override
    public void setClimb(org.bukkit.entity.Entity entity) {
        ((CraftEntity) entity).getHandle().S = 1;
    }

    @Override
    public void moveShip(Player player, org.bukkit.entity.Entity entity, Vector vector) {
        EntityBoat ec = ((CraftBoat) entity).getHandle();

        ec.getBukkitEntity().setVelocity(vector);

        ec.pitch = player.getLocation().getPitch();
        ec.yaw = player.getLocation().getYaw() - 180;
    }
}
