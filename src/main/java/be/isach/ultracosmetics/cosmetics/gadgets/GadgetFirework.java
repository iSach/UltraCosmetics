package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.Core;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Sacha on 11/11/15.
 */
public class GadgetFirework extends Gadget {

    private static Random random = new Random();

    public GadgetFirework(UUID owner) {
        super(Material.FIREWORK, (byte) 0x0, 0.1, owner, GadgetType.FIREWORK, "&7&oNeed to celebrate?\n&7&oUse fireworks");
        Core.registerListener(this);
    }

    @Override
    void onInteractRightClick() {
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
    void onInteractLeftClick() {
    }
}
