package be.isach.ultracosmetics.cosmetics.pets.v1_8_R3;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.pets.CustomEntityPet_1_8_R3;
import be.isach.ultracosmetics.cosmetics.pets.IPlayerFollower;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created by Sacha on 6/03/16.
 */
public class PlayerFollower_1_8_R3 implements Runnable, IPlayerFollower {

    private final Pet pet;
    private final Player player;

    public PlayerFollower_1_8_R3(Pet pet, Player player) {
        this.pet = pet;
        this.player = player;
    }

    @Override
    public void follow(Player player) {
        if (player == null)
            return;
        if (UltraCosmetics.getCustomPlayer(player).currentTreasureChest != null)
            return;

        net.minecraft.server.v1_8_R3.Entity petEntity;
        if (pet.isCustomEntity()) petEntity = ((CustomEntityPet_1_8_R3) pet).getCustomEntity();
        else petEntity = ((CraftEntity) pet.entity).getHandle();
        ((net.minecraft.server.v1_8_R3.EntityInsentient) petEntity).getNavigation().a(2);
        Location targetLocation = player.getLocation();
        net.minecraft.server.v1_8_R3.PathEntity path;
        path = ((net.minecraft.server.v1_8_R3.EntityInsentient) petEntity).getNavigation().a(targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ() + 1);
        try {
            int distance = (int) Bukkit.getPlayer(player.getName()).getLocation().distance(petEntity.getBukkitEntity().getLocation());
            if (distance > 10 && petEntity.valid && player.isOnGround()) {
                petEntity.setLocation(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ(), 0, 0);
            }
            if (path != null && distance > 3.3) {
                double speed = 1.05d;
                if (pet.getType().getEntityType() == EntityType.ZOMBIE)
                    speed *= 1.5;
                ((net.minecraft.server.v1_8_R3.EntityInsentient) petEntity).getNavigation().a(path, speed);
                ((net.minecraft.server.v1_8_R3.EntityInsentient) petEntity).getNavigation().a(speed);
            }
        } catch (IllegalArgumentException exception) {
            petEntity.setLocation(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ(), 0, 0);
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
