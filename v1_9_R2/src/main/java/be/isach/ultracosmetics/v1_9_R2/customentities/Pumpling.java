package be.isach.ultracosmetics.v1_9_R2.customentities;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.pets.IPetCustomEntity;
import be.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_9_R2.Block;
import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.Blocks;
import net.minecraft.server.v1_9_R2.EntityZombie;
import net.minecraft.server.v1_9_R2.EnumItemSlot;
import net.minecraft.server.v1_9_R2.ItemStack;
import net.minecraft.server.v1_9_R2.SoundEffect;
import net.minecraft.server.v1_9_R2.SoundEffects;
import net.minecraft.server.v1_9_R2.World;
import org.bukkit.Particle;
import org.bukkit.entity.Zombie;

/**
 * Created by Sacha on 18/10/15.
 */
public class Pumpling extends EntityZombie implements IPetCustomEntity {
	
	public Pumpling(World world) {
		super(world);
		
		final Pumpling instance = this;
	}
	
	public org.bukkit.entity.Entity getEntity() {
		return getBukkitEntity();
	}
	
	@Override
	protected SoundEffect G() { // say
		if (isCustomEntity()) {
			a(SoundEffects.bM, 0.05f, 2f);
			return null;
		} else return super.G();
	}
	
	@Override
	protected SoundEffect bT() { // Hurt
		if (isCustomEntity()) return null;
		else return super.bT();
	}
	
	@Override
	protected SoundEffect bS() { // Death
		if (isCustomEntity()) return null;
		else return super.bS();
	}
	
	@Override
	protected void a(BlockPosition blockposition, Block block) {
		if (isCustomEntity()) return;
		super.a(blockposition, block);
	}
	
	@Override
	public void m() {
		super.m();
		if (!isCustomEntity()) return;
		fireTicks = 0;
		getBukkitEntity().getWorld().spawnParticle(Particle.FLAME, ((Zombie) getBukkitEntity()).getEyeLocation(), 3, 0.2f, 0.2f, 0.2f);
		UltraCosmeticsData.get().getVersionManager().getPathfinderUtil().removePathFinders(getBukkitEntity());
		setInvisible(true);
		setBaby(true);
		setSlot(EnumItemSlot.HEAD, new ItemStack(Blocks.PUMPKIN));
	}
	
	private boolean isCustomEntity() {
		return CustomEntities.customEntities.contains(this);
	}
}
