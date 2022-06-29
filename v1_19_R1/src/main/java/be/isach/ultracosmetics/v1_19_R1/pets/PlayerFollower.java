package be.isach.ultracosmetics.v1_19_R1.pets;

import be.isach.ultracosmetics.cosmetics.pets.APlayerFollower;
import be.isach.ultracosmetics.cosmetics.pets.Pet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.Path;

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

        if (!player.getWorld().equals(petEntity.getBukkitEntity().getWorld())) {
            petEntity.getBukkitEntity().teleport(player.getLocation());
            return;
        }

        ((Mob) petEntity).getNavigation().setSpeedModifier(2d);
        Location targetLocation = player.getLocation();
        Path path = path((Mob) petEntity, targetLocation);

        try {
            double distanceSquared = Bukkit.getPlayer(player.getName()).getLocation().distanceSquared(petEntity.getBukkitEntity().getLocation());

            @SuppressWarnings("deprecation")
            boolean onGround = player.isOnGround();
            if (onGround && distanceSquared > 10 * 10 && petEntity.valid) {
                petEntity.moveTo(targetLocation.getX(), targetLocation.getY(), targetLocation.getZ(), 0, 0);
            }

            if (path != null && distanceSquared > 1.3 * 1.3) {
                double speed = 1.15d;

                if (pet.getType().getEntityType() == EntityType.ZOMBIE) {
                    speed *= 1.3;
                }

                ((Mob) petEntity).getNavigation().moveTo(path, speed);
                ((Mob) petEntity).getNavigation().setSpeedModifier(speed);
            }
        } catch (IllegalArgumentException exception) {
            petEntity.moveTo(targetLocation.getX(), targetLocation.getY(), targetLocation.getZ(), 0, 0);
            exception.printStackTrace();
        }
    }

    private Path path(Mob mob, Location loc) {
        return mob.getNavigation().createPath(loc.getX() + 1, loc.getY(), loc.getZ() + 1, 1);
    }
}
