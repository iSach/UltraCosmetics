package be.isach.ultracosmetics.v1_17_R1.customentities;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;

import be.isach.ultracosmetics.v1_17_R1.morphs.MorphElderGuardian;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.level.Level;

/**
 * @author RadBuilder
 */
public class CustomGuardian extends Guardian {

    private boolean custom;

    public CustomGuardian(EntityType<? extends Guardian> entitytypes, Level world) {
        super(entitytypes, world);
    }

    public void check() {
        custom = MorphElderGuardian.customEntities.contains(this);
    }

    public void target(ArmorStand armorStand) {
        try {
            ((Entity)this).getEntityData().set(EntityDataSerializers.FLOAT.createAccessor(17), (float) (armorStand == null ? 0 : ((CraftArmorStand) armorStand).getHandle().getId()));
        } catch (Exception exc) {

        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (custom) return null;
        else return super.getAmbientSound();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource paramDamageSource) {
        if (custom) return null;
        else return super.getHurtSound(paramDamageSource);
    }

    @Override
    public Component getName() {
        return new TextComponent(Language.getInstance().getOrDefault("entity.Guardian.name"));
    }


    @Override
    protected SoundEvent getDeathSound() {
        if (custom) return null;
        else return super.getDeathSound();
    }

    @Override
    public void tick() {
        if (!custom) super.tick();
        else ((LivingEntity)this).setHealth(getMaxHealth());
    }
}
