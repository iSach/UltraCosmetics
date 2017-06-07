package be.isach.ultracosmetics.v1_11_R1.customentities;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.pets.IPetCustomEntity;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import be.isach.ultracosmetics.v1_11_R1.pets.CustomEntityPet;
import net.minecraft.server.v1_11_R1.Block;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.Blocks;
import net.minecraft.server.v1_11_R1.EntityZombie;
import net.minecraft.server.v1_11_R1.EnumItemSlot;
import net.minecraft.server.v1_11_R1.ItemStack;
import net.minecraft.server.v1_11_R1.LocaleI18n;
import net.minecraft.server.v1_11_R1.SoundEffect;
import net.minecraft.server.v1_11_R1.SoundEffects;
import net.minecraft.server.v1_11_R1.World;
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
	protected SoundEffect G() { // say
		if (isCustomEntity()) {
			a(SoundEffects.bM, 0.05f, 2f);
			return null;
		} else return super.G();
	}
	
	@Override
	protected SoundEffect bW() { // Hurt
		if (isCustomEntity()) return null;
		else return super.bW();
	}
	
	@Override
	protected SoundEffect bX() { // Death
		if (isCustomEntity()) return null;
		else return super.bX();
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
	public void A_() {
		super.A_();
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
