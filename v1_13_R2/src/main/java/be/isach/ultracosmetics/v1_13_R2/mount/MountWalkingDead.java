package be.isach.ultracosmetics.v1_13_R2.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Horse;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.inventory.ItemStack;

/**
 * @author RadBuilder
 */
public class MountWalkingDead extends MountHorse<ZombieHorse> {
	
	public MountWalkingDead(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, MountType.valueOf("walkingdead"), ultraCosmetics);
	}
	
	@Override
	public void onEquip() {
		super.onEquip();
		entity.setJumpStrength(0.7);
		entity.getInventory().setItem(0, new ItemStack(Material.SADDLE));
		UltraCosmeticsData.get().getVersionManager().getEntityUtil().setHorseSpeed(entity, 0.4d);
	}
	
	@Override
	public void onUpdate() {
		entity.getWorld().spawnParticle(Particle.CRIT_MAGIC, entity.getLocation().clone().add(0, 1, 0), 5, 0.4f, 0.2f, 0.4f);
		entity.getWorld().spawnParticle(Particle.SPELL_MOB_AMBIENT, entity.getLocation().clone().add(0, 1, 0), 5, 0.4f, 0.2f, 0.4f);
	}
	
	@Override
	protected Horse.Variant getVariant() {
		return Horse.Variant.UNDEAD_HORSE;
	}
	
	@Override
	protected Horse.Color getColor() {
		return Horse.Color.WHITE;
	}
}
