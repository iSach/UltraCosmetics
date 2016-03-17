package be.isach.ultracosmetics.cosmetics.pets.v1_9_R1;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.pets.IPetCustomEntity;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Zombie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sacha on 18/10/15.
 */
public class Pumpling1_9_R1 extends EntityZombie implements IPetCustomEntity {

    boolean isCustomEntity;

    /**
     * Static list of all the custom entities.
     */
    public static List<Entity> customEntities = new ArrayList();

    public Pumpling1_9_R1(World world) {
        super(world);

        final Pumpling1_9_R1 instance = this;

        Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (customEntities.contains(instance)) {
                    isCustomEntity = true;
                    UltraCosmetics.getInstance().pathfinderUtil.removePathFinders(getBukkitEntity());
                    setInvisible(true);
                    setBaby(true);
                    setSlot(EnumItemSlot.HEAD, new ItemStack(Blocks.PUMPKIN));
                }
            }
        }, 4);
    }

    public org.bukkit.entity.Entity getEntity() {
        return getBukkitEntity();
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
}
