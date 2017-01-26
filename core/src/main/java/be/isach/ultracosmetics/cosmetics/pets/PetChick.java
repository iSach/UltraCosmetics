package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * Represents an instance of a chick pet summoned by a player.
 * 
 * @author 	iSach
 * @since 	08-12-2015
 */
public class PetChick extends Pet {

    Random r = new Random();

    public PetChick(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.CHICK);
    }

    @Override
    public void onUpdate() {
        final Item ITEM = entity.getWorld().dropItem(((Chicken) entity).getEyeLocation(), ItemFactory.create(Material.EGG, (byte) 0, UUID.randomUUID().toString()));
        ITEM.setPickupDelay(30000);
        ITEM.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2.0 + 0.3, r.nextDouble() - 0.5).multiply(0.4));
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), new Runnable() {
            @Override
            public void run() {
                ITEM.remove();
            }
        }, 5);
    }
}
