package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by Sacha on 12/10/15.
 */
public class ParticleEffectEnchanted extends ParticleEffect {

    public ParticleEffectEnchanted(UUID owner) {
        super(Effect.FLYING_GLYPH, Material.BOOK, (byte) 0, "Enchanted", "ultracosmetics.particleeffects.enchanted", owner, ParticleEffectType.ENCHANTED, 4);
    }

    @Override
    void onUpdate() {
        UtilParticles.play(getPlayer().getLocation().add(0, 2, 0), Effect.FLYING_GLYPH, 0, 0, 0, 0, 0, 8f, 60);
    }
}