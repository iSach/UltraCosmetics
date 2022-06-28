package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.version.VersionManager;

import org.bukkit.Particle;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

/**
 * Represents an instance of a party popper gadget summoned by a player.
 *
 * @author iSach
 * @since 12-16-2015
 */
public class GadgetPartyPopper extends Gadget {

    public GadgetPartyPopper(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("partypopper"), ultraCosmetics);

        setAsynchronous(true);
    }

    @Override
    protected void onRightClick() {
        for (int i = 0; i < 30; i++) {
            Vector rand = new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);

            if (VersionManager.IS_VERSION_1_13) {
                Vector v = getPlayer().getEyeLocation().getDirection().add(rand.multiply(0.2)).multiply(3.2);
                getPlayer().getWorld().spawnParticle(Particle.ITEM_CRACK, getPlayer().getEyeLocation(), 10, v.getX(), v.getY(), v.getZ(), 0.2, ItemFactory.getRandomDye());
            } else {
                Particles.ITEM_CRACK.display(new Particles.ItemData(XMaterial.INK_SAC.parseMaterial(), (byte) RANDOM.nextInt(15)),
                        getPlayer().getEyeLocation().getDirection().add(rand.multiply(0.2)).multiply(1.2), 0.6f, getPlayer().getEyeLocation(), 128);
            }
        }
        for (int i = 0; i < 3; i++) {
            XSound.ENTITY_CHICKEN_EGG.play(getPlayer().getLocation(), 1.0f, 1.0f);
        }
    }
}
