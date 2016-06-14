package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by Sacha on 16/12/15.
 */
public class GadgetPartyPopper extends Gadget {

    public GadgetPartyPopper(UUID owner) {
        super(owner, GadgetType.PARTYPOPPER);

        asyncAction = true;
    }

    @Override
    void onRightClick() {
        for (int i = 0; i < 30; i++) {
            Vector rand = new Vector(Math.random() - 0.5D,
                    Math.random() - 0.5D, Math.random() - 0.5D);
            Particles.ITEM_CRACK.display(new Particles.ItemData(Material.INK_SACK,
                            MathUtils.randomByte(15)), getPlayer().getEyeLocation().getDirection().add(rand.multiply(0.2)).multiply(1.2),
                    0.6f, getPlayer().getEyeLocation(), 128);
        }
        for (int i = 0; i < 3; i++)
            switch (UltraCosmetics.getServerVersion()) {
                case v1_8_R3:
                    getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.valueOf("CHICKEN_EGG_POP"), 1.0f, 1.0f);
                    break;
                default:
                    getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
                    break;
            }
    }

    @Override
    void onLeftClick() {

    }

    @Override
    void onUpdate() {

    }

    @Override
    public void onClear() {

    }

}
