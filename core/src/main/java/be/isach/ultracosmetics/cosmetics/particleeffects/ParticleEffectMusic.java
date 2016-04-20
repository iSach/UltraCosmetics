package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Sacha on 12/10/15.
 */
public class ParticleEffectMusic extends ParticleEffect {

    public ParticleEffectMusic(UUID owner) {
        super(owner, ParticleEffectType.MUSIC
        );
    }

    @Override
    void onUpdate() {
        for (int i = 0; i < 12; i++) {
            Random random = new Random();
            int j = random.nextInt(25);
            Particles.ParticleColor particleColor = new Particles.NoteColor(j);
            Particles.NOTE.display(particleColor, getPlayer().getLocation().add(MathUtils.randomDouble(-1.5, 1.5),
                    MathUtils.randomDouble(0, 2.5), MathUtils.randomDouble(-1.5, 1.5)), 32);
        }
    }
}
