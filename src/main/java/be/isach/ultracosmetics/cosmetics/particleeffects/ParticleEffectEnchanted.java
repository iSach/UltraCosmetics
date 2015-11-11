package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Effect;
import org.bukkit.Material;

import java.util.UUID;

/**
 * Created by Sacha on 12/10/15.
 */
public class ParticleEffectEnchanted extends ParticleEffect {

    public ParticleEffectEnchanted(UUID owner) {
        super(Effect.FLYING_GLYPH, Material.BOOK, (byte) 0, "Enchanted", "ultracosmetics.particleeffects.enchanted", owner,
                ParticleEffectType.ENCHANTED, 4,
                "&7&oBecome an almighty enchanter!");
    }

    @Override
    void onUpdate() {
        for (int i = 0; i < 60; i++)
            UtilParticles.play(getPlayer().getLocation().add(0, MathUtils.randomDouble(0.1, 2), 0), Effect.FLYING_GLYPH, 0, 0, 0, 0, 0, 8f, 1);
    }
}