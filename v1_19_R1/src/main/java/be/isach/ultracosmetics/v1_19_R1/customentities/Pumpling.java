package be.isach.ultracosmetics.v1_19_R1.customentities;

import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author RadBuilder
 */
public class Pumpling extends Zombie {

    public Pumpling(EntityType<? extends Zombie> entitytypes, Level world) {
        super(entitytypes, world);
    }

    @Override
    protected SoundEvent getAmbientSound() { // say (ambient)
        if (isCustomEntity()) {
            playSound(SoundEvents.FIRE_AMBIENT, 0.05f, 2f);
            return null;
        } else
            return super.getAmbientSound();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) { // Hurt
        if (isCustomEntity())
            return null;
        else
            return super.getHurtSound(damageSource);
    }

    @Override
    protected SoundEvent getDeathSound() { // Death
        if (isCustomEntity())
            return null;
        else
            return super.getDeathSound();
    }

    @Override
    protected SoundEvent getStepSound() { // Step
        if (isCustomEntity())
            return null;
        else
            return super.getStepSound();
    }

    @Override
    protected void playStepSound(BlockPos blockposition, BlockState iblockdata) {
        if (isCustomEntity())
            return;
        super.playStepSound(blockposition, iblockdata);
    }

    @Override
    public Component getName() {
        return CustomEntities.toComponent(Language.getInstance().getOrDefault("entity.minecraft.zombie"));
    }

    private boolean isCustomEntity() {
        return CustomEntities.isCustomEntity(this);
    }
}
