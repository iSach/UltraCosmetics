package me.isach.ultracosmetics.cosmetics.gadgets;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public class GadgetMelonThrower extends Gadget implements Listener {

    Random random = new Random();

    ArrayList<Item> melons = new ArrayList<>();
    ArrayList<Item> melonBlocks = new ArrayList<>();

    public GadgetMelonThrower(UUID owner) {
        super(Material.MELON, (byte) 0x0, "MelonThrower", "ultracosmetics.gadgets.melonthrower", 1.5f, owner, GadgetType.MELONTHROWER);
        Core.registerListener(this);
    }

    @EventHandler
    public void onTakeUpMelon(PlayerPickupItemEvent event) {
        if (melons.contains(event.getItem()) && event.getItem().getTicksLived() > 5) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2));
            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BURP, 1, 1f);
            event.setCancelled(true);
            melons.remove(event.getItem());
            event.getItem().remove();
        }
        if (melonBlocks.contains(event.getItem()))
            event.setCancelled(true);
    }

    @Override
    void onInteractRightClick() {
        getPlayer().playSound(getPlayer().getLocation(), Sound.EXPLODE, 1, 1);
        Item item = getPlayer().getWorld().dropItem(getPlayer().getEyeLocation(), ItemFactory.create(Material.MELON_BLOCK, (byte) 0x0, UUID.randomUUID().toString()));
        item.setPickupDelay(0);
        item.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.3d));
        melonBlocks.add(item);
    }

    @Override
    void onUpdate() {
        try {
            for (Item item : melonBlocks) {
                if (item.isOnGround()) {
                    item.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, 103);
                    for (int i = 0; i < 8; i++) {
                        final Item melon = getPlayer().getWorld().dropItem(item.getLocation(), ItemFactory.create(Material.MELON, (byte) 0x0, UUID.randomUUID().toString()));
                        melon.setVelocity(new Vector(random.nextDouble() - 0.5, random.nextDouble() / 2.0, random.nextDouble() - 0.5).multiply(0.75D));
                        melons.add(melon);
                        Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (melon.isValid()) {
                                    melon.remove();
                                    melons.remove(melon);
                                }
                            }
                        }, 100);
                    }
                    melonBlocks.remove(item);
                    item.remove();
                }
            }
        } catch (Exception exc) {
        }
    }

    @Override
    public void clear() {
    }

    @Override
    void onInteractLeftClick() {
    }
}
