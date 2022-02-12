package be.isach.ultracosmetics.v1_13_R2.pets;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.pets.IPlayerFollower;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityInsentient;
import net.minecraft.server.v1_13_R2.PathEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * @author RadBuilder
 */
public class PlayerFollower implements Runnable, IPlayerFollower {

    private final Pet pet;
    private final Player player;

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
            petEntity = ((CraftEntity) pet.getEntity()).getHandle();
        }

        if (!player.getWorld().equals(petEntity.getBukkitEntity().getWorld())) {
            petEntity.getBukkitEntity().teleport(player.getLocation());
            return;
        }

        ((EntityInsentient) petEntity).getNavigation().a(2);
        Location targetLocation = player.getLocation();
        PathEntity path = ((EntityInsentient) petEntity).getNavigation().a(targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ() + 1);

        try {
            int distance = (int) Bukkit.getPlayer(player.getName()).getLocation().distance(petEntity.getBukkitEntity().getLocation());

            if (distance > 10 && petEntity.valid && player.isOnGround()) {
                petEntity.setLocation(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ(), 0, 0);
            }

            if (path != null && distance > 3.3) {
                double speed = 1.05d;

                if (pet.getType().getEntityType() == EntityType.ZOMBIE) {
                    speed *= 1.5;
                }

                ((EntityInsentient) petEntity).getNavigation().a(path, speed);
                ((EntityInsentient) petEntity).getNavigation().a(speed);
            }
        } catch (IllegalArgumentException exception) {
            petEntity.setLocation(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ(), 0, 0);
            exception.printStackTrace();
        }
    }

    @Override
    public void run() {
        follow(player);
    }

    @Override
    public Runnable getTask() {
        return this;
    }
}
