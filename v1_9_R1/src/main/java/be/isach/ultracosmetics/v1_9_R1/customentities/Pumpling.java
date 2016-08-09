package be.isach.ultracosmetics.v1_9_R1.customentities;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
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
public class Pumpling extends EntityZombie implements IPetCustomEntity {

    public Pumpling(World world) {
        super(world);
    }

    public org.bukkit.entity.Entity getEntity() {
        return getBukkitEntity();
    }

    @Override
    protected SoundEffect G() { // say
        if (!CustomEntities.customEntities.contains(this)) {
            a(SoundEffects.bM, 0.05f, 2f);
            return null;
        } else return super.G();
    }

    @Override
    protected SoundEffect bR() { // Hurt
        if (!CustomEntities.customEntities.contains(this)) return null;
        else return super.bR();
    }

    @Override
    protected SoundEffect bS() { // Death
        if (!CustomEntities.customEntities.contains(this)) return null;
        else return super.bS();
    }

    @Override
    protected void a(BlockPosition blockposition, Block block) {
        if (!CustomEntities.customEntities.contains(this)) return;
        else super.a(blockposition, block);
    }

    @Override
    public void m() {
        super.m();
        if (!CustomEntities.customEntities.contains(this)) return;
        fireTicks = 0;
        UtilParticles.display(Particles.FLAME, 0.2f, 0.2f, 0.2f, ((Zombie) getBukkitEntity()).getEyeLocation(), 3);
        UltraCosmeticsData.get().getVersionManager().getPathfinderUtil().removePathFinders(getBukkitEntity());
        setInvisible(true);
        setBaby(true);
        setSlot(EnumItemSlot.HEAD, new ItemStack(Blocks.PUMPKIN));
    }
}
