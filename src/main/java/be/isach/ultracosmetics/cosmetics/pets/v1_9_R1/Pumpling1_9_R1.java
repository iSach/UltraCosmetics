package be.isach.ultracosmetics.cosmetics.pets.v1_9_R1;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R1.util.UnsafeList;
import org.bukkit.entity.Zombie;

import java.lang.reflect.Field;

/**
 * Created by Sacha on 18/10/15.
 */
public class Pumpling1_9_R1 extends EntityZombie {

    boolean isCustomEntity;

    public Pumpling1_9_R1(World world) {
        super(world);

        final Pumpling1_9_R1 instance = this;

        Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (Pet.customEntities.contains(instance)) {
                    isCustomEntity = true;
                    removeSelectors();
                    setInvisible(true);
                    setBaby(true);
                    setEquipment(EnumItemSlot.HEAD, new ItemStack(Blocks.PUMPKIN));
                }
            }
        }, 4);
    }

    @Override
    protected SoundEffect G() { // say
        if (isCustomEntity) {
            a(SoundEffects.bM, 0.05f, 2f);
            return null;
        } else return super.G();
    }

    @Override
    protected SoundEffect bR() { // Hurt
        if (isCustomEntity) return null;
        else return super.bR();
    }

    @Override
    protected SoundEffect bS() { // Death
        if (isCustomEntity) return null;
        else return super.bS();
    }

    @Override
    protected void a(BlockPosition blockposition, Block block) {
        if (isCustomEntity) return;
        else super.a(blockposition, block);
    }

    @Override
    public void m() {
        super.m();
        if (isCustomEntity) {
            fireTicks = 0;
            UtilParticles.display(Particles.FLAME, 0.2f, 0.2f, 0.2f, ((Zombie) getBukkitEntity()).getEyeLocation(), 3);
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
