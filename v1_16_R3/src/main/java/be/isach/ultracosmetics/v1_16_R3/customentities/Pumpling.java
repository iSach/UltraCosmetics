package be.isach.ultracosmetics.v1_16_R3.customentities;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.DamageSource;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EntityZombie;
import net.minecraft.server.v1_16_R3.IBlockData;
import net.minecraft.server.v1_16_R3.LocaleLanguage;
import net.minecraft.server.v1_16_R3.SoundEffect;
import net.minecraft.server.v1_16_R3.SoundEffects;
import net.minecraft.server.v1_16_R3.World;

/**
 * @author RadBuilder
 */
public class Pumpling extends EntityZombie {

    public Pumpling(EntityTypes<? extends EntityZombie> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected SoundEffect getSoundAmbient() { // say (ambient)
        if (isCustomEntity()) {
            playSound(SoundEffects.BLOCK_FIRE_AMBIENT, 0.05f, 2f);
            return null;
        } else return super.getSoundAmbient();
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damageSource) { // Hurt
        if (isCustomEntity()) return null;
        else return super.getSoundHurt(damageSource);
    }

    @Override
    protected SoundEffect getSoundDeath() { // Death
        if (isCustomEntity()) return null;
        else return super.getSoundDeath();
    }

    @Override
    protected SoundEffect getSoundStep() { // Step
        if (isCustomEntity()) return null;
        else return super.getSoundStep();
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        if (isCustomEntity()) return;
        super.b(blockposition, iblockdata);
    }

    @Override
    public String getName() {
        return LocaleLanguage.a().a("entity.minecraft.zombie");
    }

    private boolean isCustomEntity() {
        return CustomEntities.isCustomEntity(this);
    }

}
