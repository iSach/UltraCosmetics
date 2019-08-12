package be.isach.ultracosmetics.v1_14_R1.customentities;

import be.isach.ultracosmetics.v1_14_R1.morphs.MorphElderGuardian;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;

/**
 * @author RadBuilder
 */
public class CustomGuardian extends EntityGuardian {
	
	private boolean custom;
	
	public CustomGuardian(EntityTypes<? extends EntityGuardian> entitytypes, World world) {
		super(entitytypes, world);
	}
	
	public void check() {
		custom = MorphElderGuardian.customEntities.contains(this);
	}
	
	public void target(ArmorStand armorStand) {
		try {
			getDataWatcher().set(DataWatcherRegistry.c.a(17), (float) (armorStand == null ? 0 : ((CraftArmorStand) armorStand).getHandle().getId()));
		} catch (Exception exc) {
		
		}
	}
	
	@Override
	protected SoundEffect getSoundAmbient() {
		if (custom) return null;
		else return super.getSoundAmbient();
	}
	
	@Override
	protected SoundEffect getSoundHurt(DamageSource paramDamageSource) {
		if (custom) return null;
		else return super.getSoundHurt(paramDamageSource);
	}
	
	@Override
	public String getName() {
		return LocaleLanguage.a().a("entity.Guardian.name");
	}
	
	
	@Override
	protected SoundEffect getSoundDeath() {
		if (custom) return null;
		else return super.getSoundDeath();
	}
	
	@Override
	public void tick() {
		if (!custom) super.tick();
		else setHealth(getMaxHealth());
	}
}
