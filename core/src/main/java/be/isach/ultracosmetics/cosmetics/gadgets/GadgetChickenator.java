package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance of a chickenator gadget summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public class GadgetChickenator extends Gadget {

    private List<Item> items = new ArrayList<>();

    public GadgetChickenator(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("chickenator"), ultraCosmetics);
    }

    @Override
    protected void onRightClick() {
        final Chicken chicken = (Chicken) getPlayer().getWorld().spawnEntity(getPlayer().getEyeLocation(), EntityType.CHICKEN);
        chicken.setNoDamageTicks(500);
        chicken.setVelocity(getPlayer().getLocation().getDirection().multiply(Math.PI / 1.5));
        play(XSound.ENTITY_CHICKEN_AMBIENT, getPlayer(), 1.4f, 1.5f);
        play(XSound.ENTITY_GENERIC_EXPLODE, getPlayer(), 0.3f, 1.5f);
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            spawnRandomFirework(chicken.getLocation(), Color.WHITE, Color.WHITE);
            play(XSound.ENTITY_CHICKEN_HURT, getPlayer(), 1.4f, 1.5f);
            chicken.remove();
            for (int i = 0; i < 30; i++) {
                items.add(ItemFactory.createUnpickableItemVariance(XMaterial.COOKED_CHICKEN, chicken.getLocation(), RANDOM, 1));
            }
            Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> items.forEach(Item::remove), 50);
        }, 9);
        getPlayer().updateInventory();
    }

    @Override
    public void onClear() {
        for (Item i : items) {
            i.remove();
        }
        items.clear();
    }
}
