package be.isach.ultracosmetics.v1_13_R2.customentities;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.pets.IPetCustomEntity;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import be.isach.ultracosmetics.v1_13_R2.pets.CustomEntityPet;
import net.minecraft.server.v1_13_R2.*;
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
	protected SoundEffect D() { // say (ambient)
		if (isCustomEntity()) {
			a(SoundEffects.BLOCK_FIRE_AMBIENT, 0.05f, 2f);
			return null;
		} else return super.D();
	}
	
	@Override
	protected SoundEffect d(DamageSource damageSource) { // Hurt
		if (isCustomEntity()) return null;
		else return super.d(damageSource);
	}
	
	@Override
	protected SoundEffect cs() { // Death
		if (isCustomEntity()) return null;
		else return super.cs();
	}
	
	@Override
	protected SoundEffect dA() { // Step
		if (isCustomEntity()) return null;
		else return super.dA();
	}
	
	@Override
	protected void a(BlockPosition blockposition, IBlockData iblockdata) {
		if (isCustomEntity()) return;
		super.a(blockposition, iblockdata);
	}
	
	@Override
	public String getName() {
		return LocaleLanguage.a().a("entity.Zombie.name");
	}
	
	@Override
	public void tick() {
		super.tick();
		if (!isCustomEntity()) return;
		fireTicks = 0;
		UtilParticles.display(Particles.FLAME, 0.2f, 0.2f, 0.2f, ((Zombie) getBukkitEntity()).getEyeLocation(), 3);
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
