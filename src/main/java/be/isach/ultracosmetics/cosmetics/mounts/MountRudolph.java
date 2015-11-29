package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.PlayerUtils;
import be.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHorse;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.UUID;

/**
 * Created by Sacha on 29/11/15.
 */
public class MountRudolph extends Mount {

    ArmorStand left;
    ArmorStand right;
    Horse horse;

    public MountRudolph(UUID owner) {
        super(EntityType.HORSE, Material.DEAD_BUSH, (byte) 0, "Rudolph",
                "ultracosmetics.mounts.rudolph", owner, MountType.RUDOLPH,
                "&7&oWhat would be Christmas\n&7&owithout Rudolph!");

        if (owner != null) {
            horse = (Horse) ent;
            horse.setColor(Horse.Color.DARK_BROWN);
            horse.setVariant(Horse.Variant.MULE);
            color = Horse.Color.DARK_BROWN;
            variant = Horse.Variant.MULE;
            horse.setJumpStrength(0.7);
            EntityHorse entityHorse = ((CraftHorse) horse).getHandle();
            entityHorse.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.4);
            left = spawnArmorStand(false);
            right = spawnArmorStand(true);
            moveAntlers();
        }
    }

    private ArmorStand spawnArmorStand(boolean right) {
        ArmorStand armorStand = horse.getWorld().spawn(horse.getEyeLocation(), ArmorStand.class);
        armorStand.setBasePlate(false);
        armorStand.setGravity(false);
        armorStand.setArms(true);
        armorStand.setVisible(false);
        if (!right)
            armorStand.setRightArmPose(new EulerAngle(MathUtils.PI, Math.PI / 4, -(MathUtils.PI / 4)));
        else
            armorStand.setRightArmPose(new EulerAngle(MathUtils.PI, Math.PI / 4 + -(Math.PI / 2), MathUtils.PI / 4));
        armorStand.setItemInHand(new ItemStack(Material.DEAD_BUSH));
        armorStand.setMetadata("C_AD_ArmorStand", new FixedMetadataValue(Core.getPlugin(), getPlayer().getUniqueId().toString()));
        return armorStand;
    }

    @Override
    void onUpdate() {
        if (left != null && right != null)
            moveAntlers();
    }

    private void moveAntlers() {
        Location location = horse.getEyeLocation();

        Vector vectorLeft = getLeftVector(location).multiply(0.5).multiply(1.6);
        Vector rightVector = getRightVector(location).multiply(0.5).multiply(0.4);

        Vector playerVector = PlayerUtils.getHorizontalDirection(getPlayer()).multiply(0.75);

        location.add(vectorLeft).add(playerVector).add(0, -1.5, 0);
        left.teleport(location);

        location.subtract(vectorLeft).add(rightVector);

        right.teleport(location);
        location.subtract(rightVector).subtract(playerVector).subtract(0, -1.5, 0);

        double y = location.getY();
        location.add(location.getDirection().multiply(1.15));
        location.setY(y - 0.073);
        UtilParticles.display(255, 0, 0, location);
        new Thread() {
            @Override
            public void run() {
                for(Player player : getPlayer().getWorld().getPlayers()) {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(((CraftArmorStand) right).getHandle()));
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(((CraftArmorStand) left).getHandle()));
                }
            }
        }.run();
    }

    @Override
    public void clear() {
        super.clear();

        if (left != null)
            left.remove();
        if (right != null)
            right.remove();
    }

    public static Vector getLeftVector(Location loc) {
        final float newX = (float) (loc.getX() + (1 * Math.cos(Math.toRadians(loc.getYaw() + 0))));
        final float newZ = (float) (loc.getZ() + (1 * Math.sin(Math.toRadians(loc.getYaw() + 0))));

        return new Vector(newX - loc.getX(), 0, newZ - loc.getZ());
    }

    public static Vector getRightVector(Location loc) {
        final float newX = (float) (loc.getX() + (-1 * Math.cos(Math.toRadians(loc.getYaw() + 0))));
        final float newZ = (float) (loc.getZ() + (-1 * Math.sin(Math.toRadians(loc.getYaw() + 0))));

        return new Vector(newX - loc.getX(), 0, newZ - loc.getZ());
    }
}
