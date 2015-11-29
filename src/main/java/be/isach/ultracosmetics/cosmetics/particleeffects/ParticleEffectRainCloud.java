package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Material;

import java.util.UUID;

/**
 * z
 * Created by sacha on 12/08/15.
 */
public class ParticleEffectRainCloud extends ParticleEffect {

    public ParticleEffectRainCloud(UUID owner) {
        super(Particles.DRIP_WATER, Material.INK_SACK, (byte) 0x4, "RainCloud", "ultracosmetics.particleeffects.raincloud",
                owner, ParticleEffectType.RAINCLOUD, 1,
                "&7&oThe weather forecast is\n&7&otelling me it is raining.");
    }

    @Override
    void onUpdate() {
        UtilParticles.display(Particles.CLOUD, 0.5F, 0.1f, 0.5f, getPlayer().getLocation().add(0, 3, 0), 10);
        UtilParticles.display(getEffect(), 0.25F, 0.05f, 0.25f, getPlayer().getLocation().add(0, 3, 0), 1);

    }
}
