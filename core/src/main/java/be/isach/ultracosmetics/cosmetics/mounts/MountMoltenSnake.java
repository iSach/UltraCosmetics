package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
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

/**
 * Represents an instance of a moltonsnake mount.
 *
 * @author iSach
 * @since 11-28-2015
 */
public class MountMoltenSnake extends Mount<MagmaCube> {

    private List<Entity> entities = new ArrayList<>();
    private Location lastLocation;
    private float lastYaw;
    private float lastPitch;

    public MountMoltenSnake(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.valueOf("moltensnake"), ultraCosmetics);
    }

    @Override
    public void setup() {
        entity.setSize(2);
        entities.add(entity);
        summonTailPart(25);
    }

    @Override
    public void onUpdate() {
        if (getPlayer() == null) return;
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
                    setHelmet((ArmorStand) entity, null);
                }
                ArmorStand as = ((ArmorStand) entity);
                as.setHeadPose(new EulerAngle(Math.toRadians(lastPitch), Math.toRadians(lastYaw), 0));
            }
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
            setHelmet(armorStand, new ItemStack(Material.NETHERRACK));
            armorStand.setMetadata("NO_INTER", new FixedMetadataValue(getUltraCosmetics(), ""));
        }
    }

    @SuppressWarnings("deprecation")
    private void setHelmet(ArmorStand stand, ItemStack itemStack) {
        stand.setHelmet(itemStack);
    }

    @Override
    public void onClear() {
        super.onClear();
        for (Entity entity : entities)
            entity.remove();
        entities.clear();
    }
}
