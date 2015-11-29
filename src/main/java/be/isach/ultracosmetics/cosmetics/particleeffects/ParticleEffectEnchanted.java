package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Material;

import java.util.UUID;

/**
 * Created by Sacha on 12/10/15.
 */
public class ParticleEffectEnchanted extends ParticleEffect {

    public ParticleEffectEnchanted(UUID owner) {
        super(Particles.ENCHANTMENT_TABLE, Material.BOOK, (byte) 0, "Enchanted", "ultracosmetics.particleeffects.enchanted", owner,
                ParticleEffectType.ENCHANTED, 4,
                "&7&oBecome an almighty enchanter!");
    }

    @Override
    void onUpdate() {
        UtilParticles.display(Particles.ENCHANTMENT_TABLE, getPlayer().getLocation().add(0, MathUtils.randomDouble(0.1, 2), 0), 60, 8f);
    }
}