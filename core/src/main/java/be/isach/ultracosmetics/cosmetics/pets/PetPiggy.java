package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Pig;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * Represents an instance of a pig pet summoned by a player.
 * 
 * @author 	iSach
 * @since 	08-12-2015
 */
public class PetPiggy extends Pet {

    Random r = new Random();

    public PetPiggy(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.PIGGY);
    }

    @Override
    public void onUpdate() {
        Pig pig = (Pig) entity;
        Item item = entity.getWorld().dropItem(pig.getEyeLocation(), ItemFactory.create(Material.PORK, (byte) 0, UUID.randomUUID().toString()));
        item.setPickupDelay(30000);
        item.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2.0 + 0.3, r.nextDouble() - 0.5).multiply(0.4));
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> item.remove(), 5);
    }
}
