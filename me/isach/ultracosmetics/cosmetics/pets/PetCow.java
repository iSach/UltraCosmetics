package me.isach.ultracosmetics.cosmetics.pets;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Pig;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 12/08/15.
 */
public class PetCow extends Pet {

    Random r = new Random();

    public PetCow(UUID owner) {
        super(EntityType.COW, Material.MILK_BUCKET, (byte)0x0, "Cow", "ultracosmetics.pets.cow", owner, PetType.COW);
    }

    @Override
    void onUpdate() {
        final Item ITEM = ent.getWorld().dropItem(((Cow)ent).getEyeLocation(), ItemFactory.create(Material.MILK_BUCKET, (byte) 0, UUID.randomUUID().toString()));
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
