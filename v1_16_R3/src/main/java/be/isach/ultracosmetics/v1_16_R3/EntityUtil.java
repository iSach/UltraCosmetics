package be.isach.ultracosmetics.v1_16_R3;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.treasurechests.ChestType;
import be.isach.ultracosmetics.treasurechests.TreasureChestDesign;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.PacketSender;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.v1_16_R3.pathfinders.CustomPathFinderGoalPanic;
import be.isach.ultracosmetics.version.IEntityUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.*;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.*;

import static java.lang.Math.*;

/**
 * @authors RadBuilder, iSach
 */
public class EntityUtil implements IEntityUtil {

    private final Random r = new Random();
    private Map<Player, Set<EntityArmorStand>> fakeArmorStandsMap = new HashMap<>();
    private Map<Player, Set<org.bukkit.entity.Entity>> cooldownJumpMap = new HashMap<>();

    @Override
    public void resetWitherSize(Wither wither) {
        ((CraftWither) wither).getHandle().setInvul(600);
    }

    @Override
    public void sendBlizzard(final Player player, Location loc, boolean affectPlayers, Vector v) {
        final Set<EntityArmorStand> fakeArmorStands = fakeArmorStandsMap.computeIfAbsent(player, k -> new HashSet<>());
        final Set<org.bukkit.entity.Entity> cooldownJump = cooldownJumpMap.computeIfAbsent(player, k -> new HashSet<>());
        final EntityArmorStand as = new EntityArmorStand(EntityTypes.ARMOR_STAND, ((CraftWorld) player.getWorld()).getHandle());
        as.setInvisible(true);
        as.setFlag(5, true);
        as.setSmall(true);
        as.setNoGravity(true);
        as.setArms(true);
        as.setHeadPose(new Vector3f((float) (r.nextInt(360)),
                (float) (r.nextInt(360)),
                (float) (r.nextInt(360))));
        as.setLocation(loc.getX() + MathUtils.randomDouble(-1.5, 1.5), loc.getY() + MathUtils.randomDouble(0, .5) - 0.75, loc.getZ() + MathUtils.randomDouble(-1.5, 1.5), 0, 0);
        fakeArmorStands.add(as);
        PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(as);
        PacketPlayOutEntityMetadata dataPacket = new PacketPlayOutEntityMetadata(as.getId(), as.getDataWatcher(), false);
        List<Pair<EnumItemSlot, ItemStack>> list = new ArrayList<>();
        list.add(new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(org.bukkit.Material.PACKED_ICE))));
        PacketPlayOutEntityEquipment equipmentPacket = new PacketPlayOutEntityEquipment(as.getId(), list);
        for (Player players : player.getWorld().getPlayers()) {
            PacketSender.send(players, spawnPacket);
            PacketSender.send(players, dataPacket);
            PacketSender.send(players, equipmentPacket);
        }
        Particles.CLOUD.display(loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(0, .5) - 0.75, MathUtils.randomDouble(-1.5, 1.5)), 2, 0.4f);
        Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> {
            for (Player pl : player.getWorld().getPlayers())
                PacketSender.send(pl, new PacketPlayOutEntityDestroy(as.getId()));
            fakeArmorStands.remove(as);
        }, 20);
        if (affectPlayers) {
            as.getBukkitEntity().getNearbyEntities(0.5, 0.5, 0.5).stream().filter(ent -> !cooldownJump.contains(ent) && ent != player).forEachOrdered(ent -> {
                MathUtils.applyVelocity(ent, new Vector(0, 1, 0).add(v));
                cooldownJump.add(ent);
                Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> cooldownJump.remove(ent), 20);
            });
        }
    }

    @Override
    public void clearBlizzard(Player player) {
        if (!fakeArmorStandsMap.containsKey(player)) return;

        for (EntityArmorStand as : fakeArmorStandsMap.get(player)) {
            if (as == null) {
                continue;
            }
            for (Player pl : player.getWorld().getPlayers()) {
                PacketSender.send(pl, new PacketPlayOutEntityDestroy(as.getId()));
            }
        }

        fakeArmorStandsMap.remove(player);
        cooldownJumpMap.remove(player);
    }

    @Override
    public void clearPathfinders(org.bukkit.entity.Entity entity) {
        EntityInsentient nmsEntity = (EntityInsentient) ((CraftEntity) entity).getHandle();
        PathfinderGoalSelector goalSelector = nmsEntity.goalSelector;
        PathfinderGoalSelector targetSelector = nmsEntity.targetSelector;

        try {
            // Corresponds to net.minecraft.world.entity.EntityLiving#brain
            Field brField = EntityLiving.class.getDeclaredField("bg");
            brField.setAccessible(true);
            BehaviorController<?> controller = (BehaviorController<?>) brField.get(nmsEntity);

            // Corresponds to net.minecraft.world.entity.ai.Brain#memories
            Field memoriesField = BehaviorController.class.getDeclaredField("memories");
            memoriesField.setAccessible(true);
            memoriesField.set(controller, new HashMap<>());

            // Corresponds to net.minecraft.world.entity.ai.Brain#sensors
            Field sensorsField = BehaviorController.class.getDeclaredField("sensors");
            sensorsField.setAccessible(true);
            sensorsField.set(controller, new LinkedHashMap<>());

            // Corresponds to net.minecraft.world.entity.ai.Brain#availableBehaviorsByPriority
            Field cField = BehaviorController.class.getDeclaredField("e");
            cField.setAccessible(true);
            cField.set(controller, new TreeMap<>());
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            Field dField;
            // Corresponds to net.minecraft.world.entity.ai.goal.GoalSelector#availableGoals
            dField = PathfinderGoalSelector.class.getDeclaredField("d");
            dField.setAccessible(true);
            // Clear existing set instead of replacing it with a new one,
            // maintaining Airplane compatibility.
            ((AbstractCollection<?>)dField.get(goalSelector)).clear();
            ((AbstractCollection<?>)dField.get(targetSelector)).clear();

            // Corresponds to net.minecraft.world.entity.ai.goal.GoalSelector#lockedFlags
            Field cField;
            cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            cField.set(targetSelector, new EnumMap<>(PathfinderGoal.Type.class));

            // Corresponds to net.minecraft.world.entity.ai.goal.GoalSelector#disabledFlags
            Field fField;
            fField = PathfinderGoalSelector.class.getDeclaredField("f");
            fField.setAccessible(true);
            fField.set(targetSelector, EnumSet.noneOf(PathfinderGoal.Type.class));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void makePanic(org.bukkit.entity.Entity entity) {
        EntityInsentient insentient = (EntityInsentient) ((CraftEntity) entity).getHandle();
        insentient.goalSelector.a(3, new CustomPathFinderGoalPanic((EntityCreature) insentient));
    }

    @Override
    public void sendDestroyPacket(Player player, org.bukkit.entity.Entity entity) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(((CraftEntity) entity).getHandle().getId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void move(Creature creature, Location loc) {
        EntityCreature ec = ((CraftCreature) creature).getHandle();
        ec.G = 1;

        if (loc == null) return;

        ec.aC = loc.getYaw();
        PathEntity path = ec.getNavigation().a(loc.getX(), loc.getY(), loc.getZ(), 1);
        ec.getNavigation().a(path, 2);
    }

    @Override
    public void moveDragon(Player player, Vector vector, org.bukkit.entity.Entity entity) {
        EntityEnderDragon ec = ((CraftEnderDragon) entity).getHandle();

        ec.hurtTicks = -1;
        ec.pitch = player.getLocation().getPitch();
        ec.yaw = player.getLocation().getYaw() - 180;

        float yaw = player.getPlayer().getLocation().getYaw();

        double angleInRadians = toRadians(-yaw);

        double x = sin(angleInRadians);
        double z = cos(angleInRadians);

        Vector v = ec.getBukkitEntity().getLocation().getDirection();

        ec.move(EnumMoveType.SELF, new Vec3D(x, v.getY(), z));
    }

    @Override
    public void setClimb(org.bukkit.entity.Entity entity) {
        ((CraftEntity) entity).getHandle().I = 1;
    }

    @Override
    public void moveShip(Player player, org.bukkit.entity.Entity entity, Vector vector) {
        EntityBoat ec = ((CraftBoat) entity).getHandle();

        ec.getBukkitEntity().setVelocity(vector);

        ec.pitch = player.getLocation().getPitch();
        ec.yaw = player.getLocation().getYaw() - 180;

        ec.move(EnumMoveType.SELF, new Vec3D(1, 0, 0));
    }

    @Override
    public void playChestAnimation(Block b, boolean open, TreasureChestDesign design) {
        Location location = b.getLocation();
        World world = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
        if (design.getChestType() == ChestType.ENDER) {
            TileEntityEnderChest tileChest = (TileEntityEnderChest) world.getTileEntity(position);
            world.playBlockAction(position, tileChest.getBlock().getBlock(), 1, open ? 1 : 0);
        } else {
            TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(position);
            world.playBlockAction(position, tileChest.getBlock().getBlock(), 1, open ? 1 : 0);
        }
    }

    @Override
    public void follow(org.bukkit.entity.Entity toFollow, org.bukkit.entity.Entity follower) {
        Entity pett = ((CraftEntity) follower).getHandle();
        ((EntityInsentient) pett).getNavigation().a(2);
        Object petf = ((CraftEntity) follower).getHandle();
        Location targetLocation = toFollow.getLocation();
        PathEntity path;
        path = ((EntityInsentient) petf).getNavigation().a(targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ() + 1, 1);
        if (path != null) {
            ((EntityInsentient) petf).getNavigation().a(path, 1.05D);
            ((EntityInsentient) petf).getNavigation().a(1.05D);
        }
    }

    @Override
    public void chickenFall(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        if (!entityPlayer.isOnGround() && entityPlayer.getMot().getY() < 0.0D) {
            Vector v = player.getVelocity();
            player.setVelocity(v);
            entityPlayer.setMot(entityPlayer.getMot().a(0.85));
        }
    }

    @Override
    public void sendTeleportPacket(Player player, org.bukkit.entity.Entity entity) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(((CraftEntity) entity).getHandle()));
    }

    @Override
    public boolean isMoving(Player entity) {
        return false;
    }
}
