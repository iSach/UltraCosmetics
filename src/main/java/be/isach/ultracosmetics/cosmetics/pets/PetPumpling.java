package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Sacha on 18/10/15.
 */
public class PetPumpling extends Pet {

    Random r = new Random();

    public PetPumpling(UUID owner) {
        super(owner, PetType.PUMPLING);
        if (owner != null) {

        }
    }

    @Override
    void onUpdate() {
        final Item ITEM = customEnt.getBukkitEntity().getWorld().dropItem(((Zombie)customEnt.getBukkitEntity()).getEyeLocation(), ItemFactory.create(Material.JACK_O_LANTERN, (byte) 0x0, UUID.randomUUID().toString()));
        ITEM.setPickupDelay(30000);
        ITEM.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2.0 + 0.3, r.nextDouble() - 0.5).multiply(0.4));
        items.add(ITEM);
        Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                ITEM.remove();
                items.remove(ITEM);
            }
        }, 5);
    }
}
