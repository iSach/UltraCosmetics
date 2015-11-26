package be.isach.ultracosmetics.cosmetics.pets.customentities;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.Zombie;

import java.lang.reflect.Field;

/**
 * Created by Sacha on 18/10/15.
 */
public class Pumpling extends EntityZombie {

    boolean isCustomEntity;

    public Pumpling(World world) {
        super(world);

        final Pumpling instance = this;

        Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (Pet.customEntities.contains(instance)) {
                    isCustomEntity = true;
                    removeSelectors();
                    setInvisible(true);
                    setBaby(true);
                    setEquipment(4, new ItemStack(Blocks.PUMPKIN));
                }
            }
        }, 4);
    }

    @Override
    protected String z() { // say
        if (isCustomEntity) {
            makeSound("mob.ghast.scream", 0.05f, 2f);
            return null;
        } else
            super.z();
        return "mob.zombie.say";
    }

    @Override
    protected String bo() { // Hurt
        if (isCustomEntity)
            return null;
        else
            super.z();
        return "mob.zombie.hurt";
    }

    @Override
    protected void a(BlockPosition blockposition, Block block) {
        if (isCustomEntity) {
            return;
        } else {
            super.a(blockposition, block);
        }
    }

    @Override
    protected String bp() { // Death
        if (isCustomEntity)
            return null;
        else
            super.z();
        return "mob.zombie.death";
    }

    @Override
    public void m() {
        super.m();
        if (isCustomEntity) {
            fireTicks = 0;
            UtilParticles.play(Particles.FLAME, 0.2f, 0.2f, 0.2f, ((Zombie) getBukkitEntity()).getEyeLocation(), 3);
        }
    }

    private void removeSelectors() {
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
