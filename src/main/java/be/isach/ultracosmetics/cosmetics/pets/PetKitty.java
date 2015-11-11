package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Ocelot;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 12/08/15.
 */
public class PetKitty extends Pet {

    Random r = new Random();

    public PetKitty(UUID owner) {
        super(EntityType.OCELOT, Material.RAW_FISH, (byte) 0x0, "Kitty", "ultracosmetics.pets.kitty", owner, PetType.KITTY, "&7&oMeoooow!");
        if (owner != null) {
            Ocelot ocelot = (Ocelot) ent;
            ocelot.setTamed(true);
            ocelot.setSitting(false);
            ocelot.setCatType(Ocelot.Type.RED_CAT);
        }
    }

    @Override
    void onUpdate() {
        final Item ITEM = ent.getWorld().dropItem(((Ocelot) ent).getEyeLocation(), ItemFactory.create(Material.RAW_FISH, (byte) 0x0, UUID.randomUUID().toString()));
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
