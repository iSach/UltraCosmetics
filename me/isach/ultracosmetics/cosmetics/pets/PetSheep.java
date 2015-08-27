package me.isach.ultracosmetics.cosmetics.pets;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Sheep;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 12/08/15.
 */
public class PetSheep extends Pet {

    Random r = new Random();

    public PetSheep(UUID owner) {
        super(EntityType.SHEEP, Material.WOOL, (byte) 0x0, "Sheep", "ultracosmetics.pets.sheep", owner, PetType.SHEEP);
    }

    @Override
    void onUpdate() {
        final Item ITEM = ent.getWorld().dropItem(((Sheep) ent).getEyeLocation(), ItemFactory.create(Material.WOOL, (byte) r.nextInt(017), UUID.randomUUID().toString()));
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
