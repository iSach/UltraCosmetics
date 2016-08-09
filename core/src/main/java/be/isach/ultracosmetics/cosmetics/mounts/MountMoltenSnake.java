package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sacha on 28/11/15.
 */
public class MountMoltenSnake extends Mount {

    private List<Entity> entities = new ArrayList<>();
    private Entity last;
    private Location lastLocation;
    private float lastYaw;
    private float lastPitch;

    public MountMoltenSnake(UUID owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.MOLTENSNAKE, ultraCosmetics);
    }

    @Override
    protected void onEquip() {
        MagmaCube magmaCube = (MagmaCube) entity;
        magmaCube.setSize(2);
        entities.add(magmaCube);
        summonTailPart(25);
    }

    @Override
    protected void onUpdate() {
        Vector playerVector = getPlayer().getLocation().getDirection().multiply(0.7);
        for (int i = 0; i < entities.size(); i++) {
            final Entity entity = entities.get(i);
            Location loc = entity.getLocation();
            if (i == 0) {
                entity.setVelocity(playerVector);
                entity.teleport(loc.clone().add(0, -1.3, 0));
            } else {
                if (i != 1)
                    entity.teleport(lastLocation);
                else {
                    entity.teleport(lastLocation.clone().add(0, -1.3, 0));
                    ((ArmorStand)entity).setHelmet(null);
                }
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
        Location location = getPlayer().getLocation();
        Vector v = getPlayer().getLocation().getDirection().multiply(-0.25);
        for (int i = 0; i < j; i++) {
            ArmorStand armorStand = getPlayer().getWorld().spawn(location.add(v), ArmorStand.class);
            entities.add(armorStand);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setHelmet(new ItemStack(Material.NETHERRACK));
            armorStand.setMetadata("NO_INTER", new FixedMetadataValue(UltraCosmetics.getInstance(), ""));
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
