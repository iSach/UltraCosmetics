package be.isach.ultracosmetics.v1_8_R3.pets;

import be.isach.ultracosmetics.cosmetics.pets.APlayerFollower;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created by Sacha on 6/03/16.
 */
public class PlayerFollower extends APlayerFollower {

    public PlayerFollower(Pet pet, Player player) {
        super(pet, player);
    }

    @Override
    public void follow() {
        Entity petEntity;
        if (pet instanceof CustomEntityPet)  {
            petEntity = ((CustomEntityPet) pet).getNMSEntity();
        } else {
            petEntity = ((CraftEntity) pet.getEntity()).getHandle();
        }
        ((EntityInsentient) petEntity).getNavigation().a(2);
        Location targetLocation = player.getLocation();
        PathEntity path;
        path = ((EntityInsentient) petEntity).getNavigation().a(targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ() + 1);
        try {
            double distanceSquared = Bukkit.getPlayer(player.getName()).getLocation().distanceSquared(petEntity.getBukkitEntity().getLocation());
            @SuppressWarnings("deprecation")
            boolean onGround = player.isOnGround();
            if (onGround && distanceSquared > 10 * 10 && petEntity.valid) {
                petEntity.setLocation(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ(), 0, 0);
            }
            if (path != null && distanceSquared > 3.3 * 3.3) {
                double speed = 1.05d;
                if (pet.getType().getEntityType() == EntityType.ZOMBIE)
                    speed *= 1.5;
                ((EntityInsentient) petEntity).getNavigation().a(path, speed);
                ((EntityInsentient) petEntity).getNavigation().a(speed);
            }
        } catch (IllegalArgumentException exception) {
            petEntity.setLocation(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ(), 0, 0);
        }
    }
}
