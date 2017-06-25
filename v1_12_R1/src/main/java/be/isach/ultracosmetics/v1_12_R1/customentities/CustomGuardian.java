package be.isach.ultracosmetics.v1_12_R1.customentities;

import be.isach.ultracosmetics.v1_12_R1.morphs.MorphElderGuardian;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.DataWatcherRegistry;
import net.minecraft.server.v1_12_R1.EntityGuardian;
import net.minecraft.server.v1_12_R1.LocaleI18n;
import net.minecraft.server.v1_12_R1.SoundEffect;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;

/**
 * @author RadBuilder
 */
public class CustomGuardian extends EntityGuardian {
	
	private boolean custom;
	
	public CustomGuardian(World world) {
		super(world);
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
	protected SoundEffect F() {
		if (custom) return null;
		else return super.F();
	}
	
	@Override
	protected SoundEffect d(DamageSource paramDamageSource) {
		if (custom) return null;
		else return super.d(paramDamageSource);
	}
	
	@Override
	public String getName() {
		return LocaleI18n.get("entity.Guardian.name");
	}
	
	
	@Override
	protected SoundEffect cf() {
		if (custom) return null;
		else return super.cf();
	}
	
	@Override
	public void B_() {
		if (!custom) super.B_();
		else setHealth(getMaxHealth());
	}
}
