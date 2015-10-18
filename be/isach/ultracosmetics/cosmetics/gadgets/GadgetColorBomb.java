package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public class GadgetColorBomb extends Gadget {

    private Item bomb;
    private ArrayList<Item> items = new ArrayList<>();
    Random random = new Random();
    private boolean running = false;

    public GadgetColorBomb(UUID owner) {
        super(Material.WOOL, (byte) 0x3, "ColorBomb", "ultracosmetics.gadgets.colorbomb", 30, owner, GadgetType.COLORBOMB);

    }

    @Override
    void onInteractRightClick() {
        Item bomb = getPlayer().getWorld().dropItem(getPlayer().getEyeLocation(), ItemFactory.create(Material.WOOL, (byte) random.nextInt(15), UUID.randomUUID().toString()));
        bomb.setPickupDelay(50000);
        bomb.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(0.7532));
        this.bomb = bomb;
    }

    @Override
    void onUpdate() {
        if (bomb != null && bomb.isValid() && !running && bomb.isOnGround()) {

            running = true;
            bomb.setVelocity(new Vector(0, 0, 0));
            Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    clear();
                }
            }, 100);
        }
        if (running) {
            switch (random.nextInt(5)) {
                default:
                    bomb.getWorld().spigot().playEffect(bomb.getLocation(), Effect.FIREWORKS_SPARK, 0, 0, 0, 0, 0, 0.2f, 1, 32);
                    break;
                case 1:
                    bomb.getWorld().spigot().playEffect(bomb.getLocation(), Effect.FIREWORKS_SPARK, 0, 0, 0, 0, 0, 0.2f, 1, 32);
                    break;
                case 4:
                    bomb.getWorld().spigot().playEffect(bomb.getLocation(), Effect.FLAME, 0, 0, 0, 0, 0, 0.2f, 1, 32);
                    break;
                case 5:
                    bomb.getWorld().spigot().playEffect(bomb.getLocation(), Effect.WITCH_MAGIC, 0, 0, 0, 0, 0, 0.2f, 1, 32);
                    break;
            }
            try {
                for (Item item : items) {
                    if (item.getTicksLived() > 15) {
                        item.remove();
                        items.remove(item);
                    }
                }
            } catch (Exception exc) {
            }
            Bukkit.getScheduler().runTask(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    Item i = bomb.getWorld().dropItem(bomb.getLocation().add(0, 0.15f, 0), ItemFactory.create(Material.WOOL, (byte) random.nextInt(15), UUID.randomUUID().toString()));
                    i.setPickupDelay(500000);
                    i.setVelocity(new Vector(0, 0.5, 0).add(MathUtils.getRandomCircleVector().multiply(0.1)));
                    items.add(i);
                    i.getWorld().playSound(i.getLocation(), Sound.CHICKEN_EGG_POP, 0.2f, 1);
                    for (Entity entity : bomb.getNearbyEntities(1.5, 1, 1.5)) {
                        if (entity instanceof Player)
                            entity.setVelocity(new Vector(0, 0.5, 0).add(MathUtils.getRandomCircleVector().multiply(0.1)));
                    }
                }
            });
        }
    }

    @Override
    public void clear() {
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
        HandlerList.unregisterAll(this);
    }

    @Override
    void onInteractLeftClick() {
    }
}
