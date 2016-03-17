package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.cosmetics.pets.PetType;
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
public class PetPumpling_1_8_R3 extends Pet {

    Random r = new Random();

    public PetPumpling_1_8_R3(UUID owner) {
        super(owner, PetType.PUMPLING);
        if (owner != null) {

        }
    }

    @Override
    void onUpdate() {
        final Item ITEM = customEnt.getEntity().getWorld().dropItem(((Zombie)customEnt.getEntity()).getEyeLocation(), ItemFactory.create(Material.JACK_O_LANTERN, (byte) 0x0, UUID.randomUUID().toString()));
        ITEM.setPickupDelay(30000);
        ITEM.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2.0 + 0.3, r.nextDouble() - 0.5).multiply(0.4));
        items.add(ITEM);
        Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                ITEM.remove();
                items.remove(ITEM);
            }
        }, 5);
    }
}
