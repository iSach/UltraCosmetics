package me.isach.ultracosmetics.cosmetics.pets;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Pig;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 12/08/15.
 */
public class PetChick extends Pet {

    Random r = new Random();

    public PetChick(UUID owner) {
        super(EntityType.CHICKEN, Material.EGG, (byte)0x0, "Chick", "ultracosmetics.pets.chick", owner, PetType.CHICK);
    }

    @Override
    void onUpdate() {
        final Item ITEM = ent.getWorld().dropItem(((Chicken)ent).getEyeLocation(), ItemFactory.create(Material.EGG, (byte) 0, UUID.randomUUID().toString()));
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
