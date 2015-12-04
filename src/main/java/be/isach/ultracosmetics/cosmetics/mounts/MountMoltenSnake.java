package be.isach.ultracosmetics.cosmetics.mounts;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MagmaCube;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sacha on 28/11/15.
 */
public class MountMoltenSnake extends Mount {

    List<Entity> entities = new ArrayList<>();
    Entity last;
    Location lastLocation;
    float lastYaw;
    float lastPitch;

    public MountMoltenSnake(UUID owner) {
        super(EntityType.MAGMA_CUBE, Material.MAGMA_CREAM, (byte) 0, "MoltenSnake", "ultracosmetics.mounts.moltensnake", owner, MountType.MOLTENSNAKE, "&7&oDeep under the Earth's surface, there\n&7&oexists a mythical species of Molten\n&7&oSnakes. This one will serve you eternally.");

        if (owner == null) return;

        MagmaCube magmaCube = (MagmaCube) ent;
        magmaCube.setSize(3);
        entities.add(magmaCube);
        summonTailPart(25);
    }

    @Override
    void onUpdate() {
        Vector playerVector = getPlayer().getLocation().getDirection().multiply(0.7);
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            Location loc = entity.getLocation();
            if (i == 0)
                entity.setVelocity(playerVector);
            else {
                entity.teleport(lastLocation);
                ArmorStand as = ((ArmorStand) entity);
                as.setHeadPose(new EulerAngle(Math.toRadians(lastPitch), Math.toRadians(lastYaw), 0));
            }
            last = entity;
            lastLocation = loc;
            lastYaw = getPlayer().getLocation().getYaw();
            lastPitch = getPlayer().getLocation().getPitch();
        }
    }

    private void summonTailPart(int j) {
        Location location = getPlayer().getLocation().add(0, -1.5, 0);
        Vector v = getPlayer().getLocation().getDirection().multiply(-0.25);
        for (int i = 0; i < j; i++) {
            ArmorStand armorStand = getPlayer().getWorld().spawn(location.add(v), ArmorStand.class);
            entities.add(armorStand);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setHelmet(new ItemStack(Material.NETHERRACK));
        }
    }

    @Override
    public void clear() {
        super.clear();
        for (Entity entity : entities)
            entity.remove();
        entities.clear();
    }
}
