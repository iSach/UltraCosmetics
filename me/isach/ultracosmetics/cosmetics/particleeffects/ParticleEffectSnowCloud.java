package me.isach.ultracosmetics.cosmetics.particleeffects;

import me.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Effect;
import org.bukkit.Material;

import java.util.UUID;

/**
 * Created by sacha on 12/08/15.
 */
public class ParticleEffectSnowCloud extends ParticleEffect {

    public ParticleEffectSnowCloud(UUID owner){
        super(Effect.SNOW_SHOVEL, Material.SNOW_BALL, (byte)0x0, "SnowCloud", "ultracosmetics.particleeffects.snowcloud", owner, ParticleEffectType.SNOWCLOUD, 1);
    }

    @Override
    void onUpdate() {
        UtilParticles.play(getPlayer().getLocation().add(0, 3, 0), Effect.CLOUD, 0, 0, 0.5F, 0.1f, 0.5f, 0, 10);
        UtilParticles.play(getPlayer().getLocation().add(0, 3, 0), Effect.SNOW_SHOVEL, 0, 0, 0.25F, 0.05f, 0.25f, 0, 1);
    }

}
