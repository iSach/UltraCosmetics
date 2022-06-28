package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 * Represents an instance of a firework gadget summoned by a player.
 *
 * @author iSach
 * @since 11-11-2015
 */
public class GadgetFirework extends Gadget {

    public GadgetFirework(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("firework"), ultraCosmetics);
    }

    @Override
    protected void onRightClick() {
        Firework fw = (Firework) getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        int rt = RANDOM.nextInt(5);
        FireworkEffect.Type type = FireworkEffect.Type.values()[rt];

        Color c1 = Color.fromRGB(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256));
        Color c2 = Color.fromRGB(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256));

        FireworkEffect effect = FireworkEffect.builder().flicker(RANDOM.nextBoolean())
                .withColor(c1).withFade(c2).with(type)
                .trail(RANDOM.nextBoolean()).build();

        fwm.addEffect(effect);

        fwm.setPower(RANDOM.nextInt(3));

        fw.setFireworkMeta(fwm);
    }
}
