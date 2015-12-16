package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.util.PacketSender;
import net.minecraft.server.v1_8_R3.EntitySnowball;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by Sacha on 15/12/15.
 */
public class GadgetSnowball extends Gadget {

    public GadgetSnowball(UUID owner) {
        super(owner, GadgetType.SNOWBALL);
    }

    @Override
    void onInteractRightClick() {
        EntitySnowball snowball = new EntitySnowball(((CraftWorld) getPlayer().getWorld()).getHandle());
        Location location = getPlayer().getEyeLocation().subtract(0, 0.2, 0);
        snowball.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);
        PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(snowball, 11);
        Vector vector = getPlayer().getEyeLocation().getDirection().multiply(1.6);
        PacketPlayOutEntityVelocity velocityPacket = new PacketPlayOutEntityVelocity(snowball.getId(), vector.getX(), vector.getY(), vector.getZ());
        PacketSender.send(getPlayer(), spawnPacket);
        PacketSender.send(getPlayer(), velocityPacket);
    }

    @Override
    void onInteractLeftClick() {
    }

    @Override
    void onUpdate() {

    }

    @Override
    public void onClear() {

    }

}
