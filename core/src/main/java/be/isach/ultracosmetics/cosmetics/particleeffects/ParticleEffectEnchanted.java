package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;

import java.util.UUID;

/**
 * Created by Sacha on 12/10/15.
 */
public class ParticleEffectEnchanted extends ParticleEffect {

    public ParticleEffectEnchanted(UUID owner) {
        super(owner, ParticleEffectType.ENCHANTED);
    }

    @Override
    void onUpdate() {
        UtilParticles.display(Particles.ENCHANTMENT_TABLE, getPlayer().getLocation().add(0, MathUtils.randomDouble(0.1, 2), 0), 60, 8f);
    }
}