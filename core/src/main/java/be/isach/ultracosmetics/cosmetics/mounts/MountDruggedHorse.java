package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Horse;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by sacha on 10/08/15.
 */
public class MountDruggedHorse extends MountHorse {
	
	public MountDruggedHorse(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, MountType.valueOf("druggedhorse"), ultraCosmetics);
	}
	
	@Override
	public void onEquip() {
		super.onEquip();
		
		UltraCosmeticsData.get().getVersionManager().getEntityUtil().setHorseSpeed(getEntity(), 1.1d);
		getEntity().setJumpStrength(1.3);
		
		Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
			try {
				getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10000000, 1));
			} catch (Exception ignored) {
			}
		}, 1);
	}
	
	@Override
	public void onUpdate() {
		Location loc = entity.getLocation().add(0, 1, 0);
		loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 5, 0.4f, 0.2f, 0.4f);
		loc.getWorld().spawnParticle(Particle.SPELL, loc, 5, 0.4f, 0.2f, 0.4f);
		loc.getWorld().spawnParticle(Particle.SPELL_MOB_AMBIENT, loc, 5, 0.4f, 0.2f, 0.4f);
		loc.getWorld().spawnParticle(Particle.SPELL_MOB, loc, 1);
	}
	
	@Override
	protected void onClear() {
		super.onClear();
		getPlayer().removePotionEffect(PotionEffectType.CONFUSION);
	}
	
	@Override
	protected Horse.Variant getVariant() {
		return Horse.Variant.HORSE;
	}
	
	@Override
	protected Horse.Color getColor() {
		return Horse.Color.CHESTNUT;
	}
}
