package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Effect;
import org.bukkit.Material;

import java.util.UUID;

/**
 * z
 * Created by sacha on 12/08/15.
 */
public class ParticleEffectRainCloud extends ParticleEffect {

    public ParticleEffectRainCloud(UUID owner) {
        super(Effect.WATERDRIP, Material.INK_SACK, (byte) 0x4, "RainCloud", "ultracosmetics.particleeffects.raincloud", owner, ParticleEffectType.RAINCLOUD, 1);
    }

    @Override
    void onUpdate() {
        UtilParticles.play(getPlayer().getLocation().add(0, 3, 0), Effect.CLOUD, 0, 0, 0.5F, 0.1f, 0.5f, 0, 10);
        UtilParticles.play(getPlayer().getLocation().add(0, 3, 0), Effect.WATERDRIP, 0, 0, 0.25F, 0.05f, 0.25f, 0, 1);
    }
}
