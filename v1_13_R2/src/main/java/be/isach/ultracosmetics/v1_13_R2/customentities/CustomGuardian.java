package be.isach.ultracosmetics.v1_13_R2.customentities;

import be.isach.ultracosmetics.v1_13_R2.morphs.MorphElderGuardian;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftArmorStand;
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
    protected SoundEffect D() {
        if (custom) return null;
        else return super.D();
    }

    @Override
    protected SoundEffect d(DamageSource paramDamageSource) {
        if (custom) return null;
        else return super.d(paramDamageSource);
    }

    @Override
    public String getName() {
        return LocaleLanguage.a().a("entity.Guardian.name");
    }


    @Override
    protected SoundEffect cs() {
        if (custom) return null;
        else return super.cs();
    }

    @Override
    public void tick() {
        if (!custom) super.tick();
        else setHealth(getMaxHealth());
    }
}
