package be.isach.ultracosmetics.v1_12_R1.customentities;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.pets.IPetCustomEntity;
import be.isach.ultracosmetics.util.UtilParticles;
import be.isach.ultracosmetics.v1_12_R1.pets.CustomEntityPet;
import net.minecraft.server.v1_12_R1.Block;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.Blocks;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.EntityZombie;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.LocaleI18n;
import net.minecraft.server.v1_12_R1.SoundEffect;
import net.minecraft.server.v1_12_R1.SoundEffects;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Particle;
import org.bukkit.entity.Zombie;

/**
 * @author RadBuilder
 */
public class Pumpling extends EntityZombie implements IPetCustomEntity {
	
	private CustomEntityPet pet = null;
	
	public Pumpling(World world) {
		super(world);
	}
	
	public Pumpling(World world, CustomEntityPet pet) {
		super(world);
		this.pet = pet;
	}
	
	public org.bukkit.entity.Entity getEntity() {
		return getBukkitEntity();
	}
	
	@Override
	protected SoundEffect F() { // say
		if (isCustomEntity()) {
			a(SoundEffects.bM, 0.05f, 2f);
			return null;
		} else return super.F();
	}
	
	@Override
	protected SoundEffect d(DamageSource damageSource) { // Hurt
		if (isCustomEntity()) return null;
		else return super.d(damageSource);
	}
	
	@Override
	protected SoundEffect cf() { // Death
		if (isCustomEntity()) return null;
		else return super.cf();
	}
	
	@Override
	protected SoundEffect dm() { // Step
		if (isCustomEntity()) return null;
		else return super.dm();
	}
	
	@Override
	protected void a(BlockPosition blockposition, Block block) {
		if (isCustomEntity()) return;
		super.a(blockposition, block);
	}
	
	@Override
	public String getName() {
		return LocaleI18n.get("entity.Zombie.name");
	}
	
	@Override
	public void B_() {
		super.B_();
		if (!isCustomEntity()) return;
		fireTicks = 0;
		getBukkitEntity().getWorld().spawnParticle(Particle.FLAME, ((Zombie) getBukkitEntity()).getEyeLocation(), 3, 0.2f, 0.2f, 0.2f);
		UltraCosmeticsData.get().getVersionManager().getPathfinderUtil().removePathFinders(getBukkitEntity());
		pet.getFollowTask().follow(pet.getPlayer());
		setInvisible(true);
		setBaby(true);
		setSlot(EnumItemSlot.HEAD, new ItemStack(Blocks.PUMPKIN));
	}
	
	private boolean isCustomEntity() {
		return CustomEntities.customEntities.contains(this);
	}
	
}
