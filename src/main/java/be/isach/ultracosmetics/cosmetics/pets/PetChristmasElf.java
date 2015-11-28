package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Villager;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Sacha on 29/11/15.
 */
public class PetChristmasElf extends Pet {

    Random r = new Random();


    public PetChristmasElf(UUID owner) {
        super(EntityType.VILLAGER, Material.MONSTER_EGG, (byte) 0x78, "ChristmasElf", "ultracosmetics.pets.christmaself", owner, Pet.PetType.CHRISTMASELF, "&7&oI can make presents for you!");
    }

    @Override
    void onUpdate() {
        final Item ITEM = ent.getWorld().dropItem(((Villager) ent).getEyeLocation(), ItemFactory.create(Material.BEACON, (byte) 0, UUID.randomUUID().toString()));
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
