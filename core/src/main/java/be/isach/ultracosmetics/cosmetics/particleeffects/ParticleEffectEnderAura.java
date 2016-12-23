package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ParticleEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Effect;

import java.util.UUID;

/**
 * Created by Sacha on 23/12/15.
 */
public class ParticleEffectEnderAura extends ParticleEffect {

    public ParticleEffectEnderAura(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, owner, ParticleEffectType.ENDERAURA);
    }

    @Override
    public void onUpdate() {
        getPlayer().getWorld().playEffect(getPlayer().getLocation().add(0, 1, 0), Effect.ENDER_SIGNAL, 0);
    }

    @Override
    protected void onEquip() {

    }
}
