package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
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
        super(owner, GadgetType.COLORBOMB);

    }

    @Override
    void onRightClick() {
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
            Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                @Override
                public void run() {
                    onClear();
                }
            }, 100);
        }
        if (running) {
            Particles effect;
            switch (random.nextInt(5)) {
                default:
                    effect = Particles.FIREWORKS_SPARK;
                    break;
                case 1:
                    effect = Particles.FIREWORKS_SPARK;
                    break;
                case 4:
                    effect = Particles.FLAME;
                    break;
                case 5:
                    effect = Particles.SPELL_WITCH;
                    break;
            }
            UtilParticles.display(effect, bomb.getLocation(), 1, 0.2f);
            try {
                for (Item item : items) {
                    if (item.getTicksLived() > 15) {
                        item.remove();
                        items.remove(item);
                    }
                }
                Bukkit.getScheduler().runTask(UltraCosmetics.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        if (bomb == null)
                            return;
                        Item i = bomb.getWorld().dropItem(bomb.getLocation().add(0, 0.15f, 0), ItemFactory.create(Material.WOOL, (byte) random.nextInt(15), UUID.randomUUID().toString()));
                        i.setPickupDelay(500000);
                        i.setVelocity(new Vector(0, 0.5, 0).add(MathUtils.getRandomCircleVector().multiply(0.1)));
                        items.add(i);

                        switch (UltraCosmetics.getServerVersion()) {
                            case v1_8_R3:
                                i.getWorld().playSound(i.getLocation(), Sound.valueOf("CHICKEN_EGG_POP"), 0.2f, 1.0f);
                                break;
                            case v1_9_R1:
                                i.getWorld().playSound(i.getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.2f, 1.0f);
                                break;
                        }
                        for (Entity entity : bomb.getNearbyEntities(1.5, 1, 1.5)) {
                            if (entity instanceof Player)
                                if (entity.hasMetadata("NPC")) continue;
                                if (affectPlayers)
                                    MathUtils.applyVelocity(entity, new Vector(0, 0.5, 0).add(MathUtils.getRandomCircleVector().multiply(0.1)));
                        }
                    }
                });
            } catch (Exception exc) {
            }
        }
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
        HandlerList.unregisterAll(this);
    }

    @Override
    void onLeftClick() {
    }
}
