package me.isach.ultracosmetics.cosmetics.pets;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 12/08/15.
 */
public class PetDog extends Pet {

    Random r = new Random();

    public PetDog(UUID owner) {
        super(EntityType.WOLF, Material.BONE, (byte) 0x0, "Dog", "ultracosmetics.pets.dog", owner, PetType.DOG);
        if(owner != null) {
            Wolf w = (Wolf) ent;
            w.setTamed(true);
            w.setSitting(false);
        }
    }

    @Override
    void onUpdate() {
        Wolf w = (Wolf)ent;
        w.setCollarColor(DyeColor.values()[r.nextInt(15)]);
        final Item ITEM = ent.getWorld().dropItem(((Wolf)ent).getEyeLocation(), ItemFactory.create(Material.BONE, (byte) 0x0, UUID.randomUUID().toString()));
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
