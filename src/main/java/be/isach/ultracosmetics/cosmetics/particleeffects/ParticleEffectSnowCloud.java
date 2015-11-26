package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Material;

import java.util.UUID;

/**
 * Created by sacha on 12/08/15.
 */
public class ParticleEffectSnowCloud extends ParticleEffect {

    public ParticleEffectSnowCloud(UUID owner) {
        super(Particles.SNOW_SHOVEL, Material.SNOW_BALL, (byte) 0x0, "SnowCloud", "ultracosmetics.particleeffects.snowcloud",
                owner, ParticleEffectType.SNOWCLOUD, 1,
                "&7&oThe weather forecast is\n" + "&7&otelling me it is raining.");
    }

    @Override
    void onUpdate() {
        UtilParticles.play(Particles.CLOUD, 0.5F, 0.1f, 0.5f, getPlayer().getLocation().add(0, 3, 0), 10);
        UtilParticles.play(getEffect(), 0.25F, 0.05f, 0.25f, getPlayer().getLocation().add(0, 3, 0), 1);
    }

}
