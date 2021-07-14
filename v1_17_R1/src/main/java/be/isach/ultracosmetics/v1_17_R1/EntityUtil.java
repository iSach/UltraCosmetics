package be.isach.ultracosmetics.v1_17_R1;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.treasurechests.ChestType;
import be.isach.ultracosmetics.treasurechests.TreasureChestDesign;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.PacketSender;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import be.isach.ultracosmetics.v1_17_R1.pathfinders.CustomPathFinderGoalPanic;
import be.isach.ultracosmetics.version.IEntityUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftAbstractHorse;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftBoat;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftWither;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.*;

import static java.lang.Math.*;

/**
 * @authors RadBuilder, iSach
 */
public class EntityUtil implements IEntityUtil {

    private final Random r = new Random();
    private Map<Player, List<ArmorStand>> fakeArmorStandsMap = new HashMap<>();
    private Map<Player, List<org.bukkit.entity.Entity>> cooldownJumpMap = new HashMap<>();

    @Override
    public void setPassenger(org.bukkit.entity.Entity vehicle, org.bukkit.entity.Entity passenger) {
        vehicle.setPassenger(passenger);
    }

    @Override
    public void resetWitherSize(Wither wither) {
        ((CraftWither) wither).getHandle().setInvulnerableTicks(600);
    }


    @Override
    public void setHorseSpeed(org.bukkit.entity.Entity horse, double speed) {
        ((CraftAbstractHorse) horse).getHandle().getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
    }

    @Override
    public void sendBlizzard(final Player player, Location loc, boolean affectPlayers, Vector v) {
        try {
            if (!fakeArmorStandsMap.containsKey(player))
                fakeArmorStandsMap.put(player, new ArrayList<>());
            if (!cooldownJumpMap.containsKey(player))
                cooldownJumpMap.put(player, new ArrayList<>());

            final List<ArmorStand> fakeArmorStands = fakeArmorStandsMap.get(player);
            final List<org.bukkit.entity.Entity> cooldownJump = cooldownJumpMap.get(player);

            final ArmorStand as = new ArmorStand(EntityType.ARMOR_STAND, ((CraftWorld) player.getWorld()).getHandle());
            as.setInvisible(true);
            as.setSharedFlag(5, true);
            as.setSmall(true);
            as.setNoGravity(true);
            as.setShowArms(true);
            as.setHeadPose(new Rotations((r.nextInt(360)),
                    (r.nextInt(360)),
                    (r.nextInt(360))));
            as.absMoveTo(loc.getX() + MathUtils.randomDouble(-1.5, 1.5), loc.getY() + MathUtils.randomDouble(0, .5) - 0.75, loc.getZ() + MathUtils.randomDouble(-1.5, 1.5), 0, 0);
            fakeArmorStands.add(as);
            for (Player players : player.getWorld().getPlayers()) {
                PacketSender.send(players, new ClientboundAddEntityPacket(as));
                PacketSender.send(players, new ClientboundSetEntityDataPacket(as.getId(), as.getEntityData(), false));
                List<Pair<EquipmentSlot, ItemStack>> list = new ArrayList<>();
                list.add(new Pair(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(org.bukkit.Material.PACKED_ICE))));
                PacketSender.send(players, new ClientboundSetEquipmentPacket(as.getId(), list));
            }
            UtilParticles.display(Particles.CLOUD, loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(0, .5) - 0.75, MathUtils.randomDouble(-1.5, 1.5)), 2, 0.4f);
            Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> {
                for (Player pl : player.getWorld().getPlayers())
                    PacketSender.send(pl, new ClientboundRemoveEntitiesPacket(as.getId()));
                fakeArmorStands.remove(as);
            }, 20);
            if (affectPlayers)
                as.getBukkitEntity().getNearbyEntities(0.5, 0.5, 0.5).stream().filter(ent -> !cooldownJump.contains(ent) && ent != player).forEachOrdered(ent -> {
                    MathUtils.applyVelocity(ent, new Vector(0, 1, 0).add(v));
                    cooldownJump.add(ent);
                    Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> cooldownJump.remove(ent), 20);
                });
        } catch (Exception exc) {

        }
    }

    @Override
    public void clearBlizzard(Player player) {
        if (!fakeArmorStandsMap.containsKey(player)) return;

        for (ArmorStand as : fakeArmorStandsMap.get(player)) {
            if (as == null) {
                continue;
            }
            for (Player pl : player.getWorld().getPlayers()) {
                PacketSender.send(pl, new ClientboundRemoveEntitiesPacket(as.getId()));
            }
        }

        fakeArmorStandsMap.remove(player);
        cooldownJumpMap.remove(player);
    }

    @Override
    public void clearPathfinders(org.bukkit.entity.Entity entity) {
    	Mob nmsEntity = (Mob) ((CraftEntity) entity).getHandle();
        GoalSelector goalSelector = nmsEntity.goalSelector;
        GoalSelector targetSelector = nmsEntity.targetSelector;

        Brain<?> brain = ((LivingEntity)nmsEntity).getBrain();

        try {
        	// corresponds to net.minecraft.world.entity.ai.Brain#memories
            Field memoriesField = Brain.class.getDeclaredField("d");
            memoriesField.setAccessible(true);
            memoriesField.set(brain, new HashMap<>());

        	// corresponds to net.minecraft.world.entity.ai.Brain#sensors
            Field sensorsField = Brain.class.getDeclaredField("e");
            sensorsField.setAccessible(true);
            sensorsField.set(brain, new LinkedHashMap<>());

            // this method is annotated with VisibleForTesting but it seems like the easiest thing to do at the moment
            // this clears net.minecraft.world.entity.ai.Brain#availableBehaviorsByPriority
            brain.removeAllBehaviors();
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
        	// this is also annotated VisibleForTesting
        	// this clears net.minecraft.world.entity.ai.goal.GoalSelector#availableGoals
            goalSelector.removeAllGoals();
            targetSelector.removeAllGoals();

            Field cField;
            // corresponds to net.minecraft.world.entity.ai.goal.GoalSelector#lockedFlags
            cField = GoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            // I'm  not sure what this line is supposed to do? it's just repeated
            //dField.set(goalSelector, new LinkedHashSet<>());
            cField.set(targetSelector, new EnumMap<Goal.Flag,WrappedGoal>(Goal.Flag.class));

            Field fField;
            // corresponds to net.minecraft.world.entity.ai.goal.GoalSelector#disabledFlags
            fField = GoalSelector.class.getDeclaredField("f");
            fField.setAccessible(true);
            //dField.set(goalSelector, new LinkedHashSet<>());
            fField.set(targetSelector, EnumSet.noneOf(Goal.Flag.class));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void makePanic(org.bukkit.entity.Entity entity) {
        PathfinderMob insentient = (PathfinderMob) ((CraftEntity) entity).getHandle();
        insentient.goalSelector.addGoal(3, new CustomPathFinderGoalPanic(insentient, 0.4d));
    }

    @Override
    public void sendDestroyPacket(Player player, org.bukkit.entity.Entity entity) {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(((CraftEntity) entity).getHandle().getId());
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    @Override
    public void move(Creature creature, Location loc) {
        PathfinderMob ec = ((CraftCreature) creature).getHandle();
        ec.maxUpStep = 1;

        if (loc == null) return;

        ec.yHeadRot = loc.getYaw();
        Path path = ec.getNavigation().createPath(loc.getX(), loc.getY(), loc.getZ(), 1);
        ec.getNavigation().moveTo(path, 2);
    }

    @Override
    public void moveDragon(Player player, Vector vector, org.bukkit.entity.Entity entity) {
        EnderDragon ec = ((CraftEnderDragon) entity).getHandle();

        ec.hurtTime = -1;
        ec.setXRot(player.getLocation().getPitch());
        ec.setYRot(player.getLocation().getYaw() - 180);

        float yaw = player.getPlayer().getLocation().getYaw();

        double angleInRadians = toRadians(-yaw);

        double x = sin(angleInRadians);
        double z = cos(angleInRadians);

        Vector v = ec.getBukkitEntity().getLocation().getDirection();

        ec.move(MoverType.SELF, new Vec3(x, v.getY(), z));
    }

    @Override
    public void setClimb(org.bukkit.entity.Entity entity) {
    	// TODO: this field (I) no longer exists so I'm not sure what to do here
        //((CraftEntity) entity).getHandle().I = 1;
    }

    @Override
    public void moveShip(Player player, org.bukkit.entity.Entity entity, Vector vector) {
        Boat ec = ((CraftBoat) entity).getHandle();

        ec.getBukkitEntity().setVelocity(vector);

        ec.setXRot(player.getLocation().getPitch());
        ec.setYRot(player.getLocation().getYaw() - 180);

        ec.move(MoverType.SELF, new Vec3(1, 0, 0));
    }

    @Override
    public void playChestAnimation(Block b, boolean open, TreasureChestDesign design) {
        Location location = b.getLocation();
        Level world = ((CraftWorld) location.getWorld()).getHandle();
        BlockPos position = new BlockPos(location.getX(), location.getY(), location.getZ());
        if (design.getChestType() == ChestType.ENDER) {
            EnderChestBlockEntity tileChest = (EnderChestBlockEntity) world.getBlockEntity(position);
            world.blockEvent(position, tileChest.getBlockState().getBlock(), 1, open ? 1 : 0);
        } else {
            ChestBlockEntity tileChest = (ChestBlockEntity) world.getBlockEntity(position);
            world.blockEvent(position, tileChest.getBlockState().getBlock(), 1, open ? 1 : 0);
        }
    }

    @Override
    public org.bukkit.entity.Entity spawnItem(org.bukkit.inventory.ItemStack itemStack, Location blockLocation) {
        ItemEntity ei = new ItemEntity(
                ((CraftWorld) blockLocation.clone().add(0.5D, 1.2D, 0.5D).getWorld()).getHandle(),
                blockLocation.clone().add(0.5D, 1.2D, 0.5D).getX(),
                blockLocation.clone().add(0.5D, 1.2D, 0.5D).getY(),
                blockLocation.clone().add(0.5D, 1.2D, 0.5D).getZ(),
                CraftItemStack.asNMSCopy(itemStack)) {
        };
        ei.getBukkitEntity().setVelocity(new Vector(0.0D, 0.25D, 0.0D));
        ei.pickupDelay = 2147483647;
        ei.getBukkitEntity().setCustomName(UltraCosmeticsData.get().getItemNoPickupString());
        ei.pickupDelay = 20;

        ((CraftWorld) blockLocation.clone().add(0.5D, 1.2D, 0.5D).getWorld()).getHandle().addFreshEntity(ei);

        return ei.getBukkitEntity();
    }

    @Override
    public boolean isSameInventory(Inventory first, Inventory second) {
        return ((CraftInventory) first).getInventory().equals(((CraftInventory) second).getInventory());
    }

    @Override
    public void follow(org.bukkit.entity.Entity toFollow, org.bukkit.entity.Entity follower) {
        Entity pett = ((CraftEntity) follower).getHandle();
        ((Mob) pett).getNavigation().setMaxVisitedNodesMultiplier(2);
        Object petf = ((CraftEntity) follower).getHandle();
        Location targetLocation = toFollow.getLocation();
        Path path;
        path = ((Mob) petf).getNavigation().createPath(targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ() + 1, 1);
        if (path != null) {
            ((Mob) petf).getNavigation().moveTo(path, 1.05D);
            ((Mob) petf).getNavigation().setSpeedModifier(1.05D);
        }
    }

    @Override
    public void chickenFall(Player player) {
        ServerPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        if (!entityPlayer.isOnGround() && entityPlayer.getDeltaMovement().y() < 0.0D) {
            Vector v = player.getVelocity();
            player.setVelocity(v);
            entityPlayer.setDeltaMovement(entityPlayer.getDeltaMovement().scale(0.85));
        }
    }

    @Override
    public void sendTeleportPacket(Player player, org.bukkit.entity.Entity entity) {
        ((CraftPlayer) player).getHandle().connection.send(new ClientboundTeleportEntityPacket(((CraftEntity) entity).getHandle()));
    }

    @Override
    public boolean isMoving(Player entity) {
        return false;
    }

    @Override
    public byte[] getEncodedData(String url) {
        return Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
    }
}
