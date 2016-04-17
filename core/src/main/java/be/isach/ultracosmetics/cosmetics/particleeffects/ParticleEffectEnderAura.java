package be.isach.ultracosmetics.cosmetics.particleeffects;

import org.bukkit.Effect;

import java.util.UUID;

/**
 * Created by Sacha on 23/12/15.
 */
public class ParticleEffectEnderAura extends ParticleEffect {

    public ParticleEffectEnderAura(UUID owner) {
        super(owner, ParticleEffectType.ENDERAURA);
        ignoreMove = true;
    }

    @Override
    void onUpdate() {
        getPlayer().getWorld().playEffect(getPlayer().getLocation().add(0, 1, 0), Effect.ENDER_SIGNAL, 0);
    }
}
