package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

/**
 * Created by Sacha on 11/11/15.
 */
public class GadgetFirework extends Gadget {

    private static Random random = new Random();

    public GadgetFirework(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.FIREWORK, ultraCosmetics);
        UltraCosmetics.getInstance().registerListener(this);
    }

    @Override
    void onRightClick() {
        Firework fw = (Firework) getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        int rt = random.nextInt(5);
        FireworkEffect.Type type = FireworkEffect.Type.values()[rt];

        Color c1 = Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Color c2 = Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));

        FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean())
                .withColor(c1).withFade(c2).with(type)
                .trail(random.nextBoolean()).build();

        fwm.addEffect(effect);

        fwm.setPower(random.nextInt(3));

        fw.setFireworkMeta(fwm);
    }

    @Override
    void onUpdate() {
    }

    @Override
    public void onClear() {
    }

    @Override
    void onLeftClick() {
    }
}
