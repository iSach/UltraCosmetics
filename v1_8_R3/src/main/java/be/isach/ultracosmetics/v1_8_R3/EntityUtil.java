package be.isach.ultracosmetics.v1_8_R3;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.treasurechests.ChestType;
import be.isach.ultracosmetics.cosmetics.treasurechests.TreasureChestDesign;
import be.isach.ultracosmetics.util.*;
import be.isach.ultracosmetics.v1_8_R3.pathfinders.CustomPathFinderGoalPanic;
import be.isach.ultracosmetics.version.IEntityUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Sacha on 14/03/16.
 */
public class EntityUtil implements IEntityUtil {

    @Override
    public void setPassenger(Entity vehicle, Entity passenger) {
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
    Map<Player, List<Entity>> cooldownJumpMap = new HashMap<>();

    @Override
    public void sendBlizzard(final Player player, Location loc, boolean affectPlayers, Vector v) {
        if (!fakeArmorStandsMap.containsKey(player))
            fakeArmorStandsMap.put(player, new ArrayList<EntityArmorStand>());
        if (!cooldownJumpMap.containsKey(player))
            cooldownJumpMap.put(player, new ArrayList<Entity>());

        final List<EntityArmorStand> fakeArmorStands = fakeArmorStandsMap.get(player);
        final List<Entity> cooldownJump = cooldownJumpMap.get(player);

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
            PacketSender.send(players, new PacketPlayOutEntityEquipment(as.getId(), 4, CraftItemStack.asNMSCopy(new ItemStack(org.bukkit.Material.PACKED_ICE))));
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
            for (final Entity ent : as.getBukkitEntity().getNearbyEntities(0.5, 0.5, 0.5)) {
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
    public void clearPathfinders(Entity entity) {
        EntityInsentient entitySheep = (EntityInsentient) ((CraftEntity) entity).getHandle();

        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(entitySheep.goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(entitySheep.targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(entitySheep.goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(entitySheep.targetSelector, new UnsafeList<PathfinderGoalSelector>());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public void makePanic(Entity entity) {
        EntityInsentient insentient = (EntityInsentient) ((CraftEntity) entity).getHandle();
        insentient.goalSelector.a(3, new CustomPathFinderGoalPanic((EntityCreature) insentient, 0.4d));
    }

    @Override
    public void sendDestroyPacket(Player player, Entity entity) {
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
    public void moveDragon(Player player, Vector vector, Entity entity) {
        EntityEnderDragon ec = ((CraftEnderDragon) entity).getHandle();

        ec.hurtTicks = -1;

        ec.getBukkitEntity().setVelocity(vector);

        ec.pitch = player.getLocation().getPitch();
        ec.yaw = player.getLocation().getYaw() - 180;
    }

    @Override
    public void setClimb(Entity entity) {
        ((CraftEntity) entity).getHandle().S = 1;
    }

    @Override
    public void moveShip(Player player, Entity entity, Vector vector) {
        EntityBoat ec = ((CraftBoat) entity).getHandle();

        ec.getBukkitEntity().setVelocity(vector);

        ec.pitch = player.getLocation().getPitch();
        ec.yaw = player.getLocation().getYaw() - 180;
    }

    @Override
    public void playChestAnimation(Block b, boolean open, TreasureChestDesign design) {
        Location location = b.getLocation();
        World world = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
        if (design.getChestType() == ChestType.ENDER) {
            TileEntityEnderChest tileChest = (TileEntityEnderChest) world.getTileEntity(position);
            world.playBlockAction(position, tileChest.w(), 1, open ? 1 : 0);
        } else {
            TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(position);
            world.playBlockAction(position, tileChest.w(), 1, open ? 1 : 0);
        }
    }

    @Override
    public Entity spawnItem(ItemStack itemStack, Location blockLocation) {
        EntityItem ei = new EntityItem(
                ((CraftWorld)blockLocation.clone().add(0.5D, 1.2D, 0.5D).getWorld()).getHandle(),
                blockLocation.clone().add(0.5D, 1.2D, 0.5D).getX(),
                blockLocation.clone().add(0.5D, 1.2D, 0.5D).getY(),
                blockLocation.clone().add(0.5D, 1.2D, 0.5D).getZ(),
                CraftItemStack.asNMSCopy(itemStack)) {


            public boolean a(EntityItem entityitem) {
                return false;
            }
        };
        ei.getBukkitEntity().setVelocity(new Vector(0.0D, 0.25D, 0.0D));
        ei.pickupDelay = 2147483647;
        ei.getBukkitEntity().setCustomName(UUID.randomUUID().toString());
        ei.pickupDelay = 20;

        ((CraftWorld) blockLocation.clone().add(0.5D, 1.2D, 0.5D).getWorld()).getHandle().addEntity(ei);

        return ei.getBukkitEntity();
    }

    @Override
    public boolean isSameInventory(Inventory first, Inventory second) {
        return ((CraftInventory) first).getInventory().equals(((CraftInventory) second).getInventory());
    }

    @Override
    public void follow(Entity toFollow, Entity follower) {
        net.minecraft.server.v1_8_R3.Entity pett = ((CraftEntity) follower).getHandle();
        ((EntityInsentient) pett).getNavigation().a(2);
        Object petf = ((CraftEntity) follower).getHandle();
        Location targetLocation = toFollow.getLocation();
        PathEntity path;
        path = ((EntityInsentient) petf).getNavigation().a(targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ() + 1);
        if (path != null) {
            ((EntityInsentient) petf).getNavigation().a(path, 1.05D);
            ((EntityInsentient) petf).getNavigation().a(1.05D);
        }
    }

    @Override
    public void chickenFall(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        if (!entityPlayer.onGround && entityPlayer.motY < 0.0D) {
            Vector v = player.getVelocity();
            player.setVelocity(v);
            entityPlayer.motY *= 0.85;
        }
    }

    @Override
    public void sendTeleportPacket(Player player, Entity entity) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(((CraftEntity) entity).getHandle()));
    }
}
