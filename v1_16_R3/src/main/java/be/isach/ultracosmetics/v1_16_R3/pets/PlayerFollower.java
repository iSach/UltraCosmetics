package be.isach.ultracosmetics.v1_16_R3.pets;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.pets.APlayerFollower;
import be.isach.ultracosmetics.cosmetics.pets.Pet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.PathEntity;

/**
 * @author RadBuilder
 */
public class PlayerFollower extends APlayerFollower {

    public PlayerFollower(Pet pet, Player player) {
        super(pet, player);
    }

    @Override
    public void follow() {
        Entity petEntity;

        if (pet instanceof CustomEntityPet) {
            petEntity = ((CustomEntityPet) pet).getNMSEntity();
        } else {
            petEntity = ((CraftEntity) pet.getEntity()).getHandle();
        }

        if (petEntity == null) {
            return;
        }

        // Run in sync... To enhance :S
        Bukkit.getScheduler().runTask(UltraCosmeticsData.get().getPlugin(), () -> {
            if (!player.isOnline()) return;
            if (!player.getWorld().equals(petEntity.getBukkitEntity().getWorld())) {
                petEntity.getBukkitEntity().teleport(player.getLocation());
                return;
            }

            ((EntityInsentient) petEntity).getNavigation().a(2d);
            Location targetLocation = player.getLocation();
            PathEntity path = ((EntityInsentient) petEntity).getNavigation().a(targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ() + 1, 1);

            try {
                double distanceSquared = Bukkit.getPlayer(player.getName()).getLocation().distanceSquared(petEntity.getBukkitEntity().getLocation());

                @SuppressWarnings("deprecation")
                boolean onGround = player.isOnGround();
                if (onGround && distanceSquared > 10 * 10 && petEntity.valid) {
                    petEntity.setLocation(targetLocation.getX(), targetLocation.getY(), targetLocation.getZ(), 0, 0);
                }

                if (path != null && distanceSquared > 1.3 * 1.3) {
                    double speed = 1.15d;

                    if (pet.getType().getEntityType() == EntityType.ZOMBIE) {
                        speed *= 1.3;
                    }

                    ((EntityInsentient) petEntity).getNavigation().a(path, speed);
                    ((EntityInsentient) petEntity).getNavigation().a(speed);
                }
            } catch (IllegalArgumentException exception) {
                petEntity.setLocation(targetLocation.getX(), targetLocation.getY(), targetLocation.getZ(), 0, 0);
                // exception.printStackTrace();
            }
        });
    }
}
