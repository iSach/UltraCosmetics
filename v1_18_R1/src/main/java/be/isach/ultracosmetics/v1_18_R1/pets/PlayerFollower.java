package be.isach.ultracosmetics.v1_18_R1.pets;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.pets.IPlayerFollower;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * @author RadBuilder
 */
public class PlayerFollower implements Runnable, IPlayerFollower {

    private final Pet pet;
    private final Player player;
    private static Method pathMethod = null;

    public PlayerFollower(Pet pet, Player player) {
        this.pet = pet;
        this.player = player;
    }

    @Override
    public void follow(Player player) {
        if (player == null) {
            return;
        }

        if (UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(player).getCurrentTreasureChest() != null) {
            return;
        }

        Entity petEntity;

        if (pet.isCustomEntity()) {
            petEntity = ((CustomEntityPet) pet).getCustomEntity();
        } else {
            petEntity = ((CraftEntity) pet.entity).getHandle();
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

            ((Mob) petEntity).getNavigation().setSpeedModifier(2d);
            Location targetLocation = player.getLocation();
            Path path = path((Mob)petEntity, targetLocation);

            try {
                int distance = (int) Bukkit.getPlayer(player.getName()).getLocation().distance(petEntity.getBukkitEntity().getLocation());

                if (distance > 10 && petEntity.valid && player.isOnGround()) {
                    petEntity.moveTo(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ(), 0, 0);
                }

                if (path != null && distance > 1.3) {
                    double speed = 1.15d;

                    if (pet.getType().getEntityType() == EntityType.ZOMBIE) {
                        speed *= 1.3;
                    }

                    ((Mob) petEntity).getNavigation().moveTo(path, speed);
                    ((Mob) petEntity).getNavigation().setSpeedModifier(speed);
                }
            } catch (IllegalArgumentException exception) {
                petEntity.moveTo(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ(), 0, 0);
                //exception.printStackTrace();
            }
        });
    }

    @Override
    public void run() {
        follow(player);
    }

    @Override
    public Runnable getTask() {
        return this;
    }
    
    private Path path(Mob mob, Location loc) {
        return mob.getNavigation().createPath(loc.getX() + 1, loc.getY(), loc.getZ() + 1, 1);
    }
}
