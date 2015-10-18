package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.Core;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Pig;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 12/08/15.
 */
public class PetPiggy extends Pet {

    Random r = new Random();

    public PetPiggy(UUID owner) {
        super(EntityType.PIG, Material.MONSTER_EGG, (byte) 0x5a, "Piggy", "ultracosmetics.pets.piggy", owner, PetType.PIGGY);
    }

    @Override
    void onUpdate() {
        final Item ITEM = ent.getWorld().dropItem(((Pig) ent).getEyeLocation(), ItemFactory.create(Material.PORK, (byte) 0, UUID.randomUUID().toString()));
        ITEM.setPickupDelay(30000);
        ITEM.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2.0 + 0.3, r.nextDouble() - 0.5).multiply(0.4));
        Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                ITEM.remove();
            }
        }, 5);
    }
}
