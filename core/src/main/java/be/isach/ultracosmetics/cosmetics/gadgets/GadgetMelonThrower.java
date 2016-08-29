package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public class GadgetMelonThrower extends Gadget implements Listener {

    Random random = new Random();

    ArrayList<Item> melons = new ArrayList<>();
    ArrayList<Item> melonBlocks = new ArrayList<>();

    public GadgetMelonThrower(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.MELONTHROWER,ultraCosmetics);
    }

    @EventHandler
    public void onTakeUpMelon(PlayerPickupItemEvent event) {
        if (melons.contains(event.getItem()) && event.getItem().getTicksLived() > 5
                && affectPlayers) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2));
            SoundUtil.playSound(getPlayer().getLocation(), Sounds.BURP, 1.4f, 1.5f);
            event.setCancelled(true);
            melons.remove(event.getItem());
            event.getItem().remove();
        }
        if (melonBlocks.contains(event.getItem()))
            event.setCancelled(true);
    }

    @Override
    void onRightClick() {
        SoundUtil.playSound(getPlayer().getLocation(), Sounds.EXPLODE, 1.4f, 1.5f);
        Item item = getPlayer().getWorld().dropItem(getPlayer().getEyeLocation(), ItemFactory.create(Material.MELON_BLOCK, (byte) 0x0, UUID.randomUUID().toString()));
        item.setPickupDelay(0);
        item.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.3d));
        melonBlocks.add(item);
    }

    @Override
    public void onUpdate() {
        try {
            Bukkit.getScheduler().runTask(getUltraCosmetics(), new Runnable() {
                @Override
                public void run() {
                    Iterator<Item> melonBlockIterator = melonBlocks.iterator();
                    while (melonBlockIterator.hasNext()) {
                        Item item = melonBlockIterator.next();
                        if (item.isOnGround()) {
                            item.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, 103);
                            for (int i = 0; i < 8; i++) {
                                final Item melon = getPlayer().getWorld().dropItem(item.getLocation(), ItemFactory.create(Material.MELON, (byte) 0x0, UUID.randomUUID().toString()));
                                melon.setVelocity(new Vector(random.nextDouble() - 0.5, random.nextDouble() / 2.0, random.nextDouble() - 0.5).multiply(0.75D));
                                melons.add(melon);
                                Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (melon.isValid()) {
                                            melon.remove();
                                            melons.remove(melon);
                                        }
                                    }
                                }, 100);
                            }
                            item.remove();
                            melonBlockIterator.remove();
                        }
                    }
                }
            });
        } catch (Exception exc) {
        }
    }

    @Override
    public void onClear() {
        for (Item melon : melons)
            melon.remove();

        for (Item melonBlock : melonBlocks)
            melonBlock.remove();

    }

    @Override
    void onLeftClick() {
    }
}
