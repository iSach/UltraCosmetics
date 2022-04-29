package be.isach.ultracosmetics.v1_12_R1.customentities;

import net.minecraft.server.v1_12_R1.Block;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.EntityZombie;
import net.minecraft.server.v1_12_R1.LocaleI18n;
import net.minecraft.server.v1_12_R1.SoundEffect;
import net.minecraft.server.v1_12_R1.SoundEffects;
import net.minecraft.server.v1_12_R1.World;

/**
 * @author RadBuilder
 */
public class Pumpling extends EntityZombie {

    public Pumpling(World world) {
        super(world);
    }

    @Override
    protected SoundEffect F() { // say
        if (isCustomEntity()) {
            a(SoundEffects.bM, 0.05f, 2f);
            return null;
        } else return super.F();
    }

    @Override
    protected SoundEffect d(DamageSource damageSource) { // Hurt
        if (isCustomEntity()) return null;
        else return super.d(damageSource);
    }

    @Override
    protected SoundEffect cf() { // Death
        if (isCustomEntity()) return null;
        else return super.cf();
    }

    @Override
    protected SoundEffect dm() { // Step
        if (isCustomEntity()) return null;
        else return super.dm();
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

    private boolean isCustomEntity() {
        return CustomEntities.isCustomEntity(this);
    }

}
