package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;
import org.bukkit.*;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public class GadgetChickenator extends Gadget {

    static Random r = new Random();
    ArrayList<Item> items = new ArrayList<>();

    public GadgetChickenator(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.CHICKENATOR, ultraCosmetics);
    }

    @Override
    void onRightClick() {
        final Chicken chicken = (Chicken) getPlayer().getWorld().spawnEntity(getPlayer().getEyeLocation(), EntityType.CHICKEN);
        chicken.setNoDamageTicks(500);
        chicken.setVelocity(getPlayer().getLocation().getDirection().multiply(Math.PI / 1.5));
        SoundUtil.playSound(getPlayer(), Sounds.CHICKEN_IDLE, 1.4f, 1.5f);
        SoundUtil.playSound(getPlayer(), Sounds.EXPLODE, 0.3f, 1.5f);
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), new Runnable() {
            @Override
            public void run() {
                spawnRandomFirework(chicken.getLocation());
                SoundUtil.playSound(getPlayer(), Sounds.CHICKEN_HURT, 1.4f, 1.5f);
                chicken.remove();
                for (int i = 0; i < 30; i++) {
                    final Item ITEM = chicken.getWorld().dropItem(chicken.getLocation(), ItemFactory.create(Material.COOKED_CHICKEN, (byte) 0, UUID.randomUUID().toString()));
                    ITEM.setPickupDelay(30000);
                    ITEM.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2.0, r.nextDouble() - 0.5));
                    items.add(ITEM);
                }
                Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), new Runnable() {
                    @Override
                    public void run() {
                        for (Item i : items)
                            i.remove();
                    }
                }, 50);
            }
        }, 9);
        getPlayer().updateInventory();
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onClear() {
        for (Item i : items)
            i.remove();
        HandlerList.unregisterAll(this);
    }

    public static FireworkEffect getRandomFireworkEffect() {
        FireworkEffect.Builder builder = FireworkEffect.builder();
        FireworkEffect effect = builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))).withFade(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))).build();
        return effect;
    }

    public void spawnRandomFirework(Location location) {
        final ArrayList<Firework> fireworks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final Firework f = getPlayer().getWorld().spawn(location, Firework.class);

            FireworkMeta fm = f.getFireworkMeta();
            fm.addEffect(getRandomFireworkEffect());
            f.setFireworkMeta(fm);
            fireworks.add(f);
        }
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), new Runnable() {
            @Override
            public void run() {
                for (Firework f : fireworks)
                    f.detonate();
            }
        }, 2);
    }

    @Override
    void onLeftClick() {
    }
}
