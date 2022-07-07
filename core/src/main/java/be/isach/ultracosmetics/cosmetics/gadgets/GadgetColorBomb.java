package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.PlayerAffectingCosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.XTag;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents an instance of a color bomb gadget summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public class GadgetColorBomb extends Gadget implements PlayerAffectingCosmetic, Updatable {

    private Item bomb;
    private ArrayList<Item> items = new ArrayList<>();
    private boolean running = false;

    public GadgetColorBomb(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("colorbomb"), ultraCosmetics);
    }

    @Override
    protected void onRightClick() {
        bomb = ItemFactory.createUnpickableItemDirectional(ItemFactory.randomFromTag(XTag.WOOL), getPlayer(), 0.7532);
    }

    @Override
    public void onUpdate() {
        if (bomb == null || !bomb.isValid()) return;
        if (!running && bomb.isOnGround()) {
            running = true;
            bomb.setVelocity(new Vector(0, 0, 0));
            Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), this::onClear, 100);
        }

        if (!running) return;

        Particles effect;
        switch (RANDOM.nextInt(5)) {
        default:
            effect = Particles.FIREWORKS_SPARK;
            break;
        case 3:
            effect = Particles.FLAME;
            break;
        case 4:
            effect = Particles.SPELL_WITCH;
            break;
        }

        effect.display(bomb.getLocation(), 1, 0.2f);

        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            Item item = iter.next();
            if (item.getTicksLived() > 15) {
                item.remove();
                iter.remove();
            }
        }

        Bukkit.getScheduler().runTask(getUltraCosmetics(), () -> {
            if (bomb == null) {
                return;
            }

            Vector velocity = new Vector(0, 0.5, 0).add(MathUtils.getRandomCircleVector().multiply(0.1));
            Item item = ItemFactory.spawnUnpickableItem(ItemFactory.randomItemFromTag(XTag.WOOL), bomb.getLocation().add(0, 0.15, 0), velocity);
            items.add(item);
            play(XSound.ENTITY_CHICKEN_EGG, item.getLocation(), 0.2f, 1.0f);

            for (Entity entity : bomb.getNearbyEntities(1.5, 1, 1.5)) {
                if (entity instanceof Player && canAffect(entity)) {
                    MathUtils.applyVelocity(entity, new Vector(0, 0.5, 0).add(MathUtils.getRandomCircleVector().multiply(0.1)));
                }
            }
        });
    }

    @Override
    public void onClear() {
        if (bomb != null) {
            bomb.remove();
            bomb = null;
        }

        if (items != null) {
            for (Item item : items) {
                item.remove();
            }
            items.clear();
        }

        running = false;
    }
}
