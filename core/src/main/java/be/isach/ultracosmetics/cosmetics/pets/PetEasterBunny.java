package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Rabbit;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 12/08/15.
 */
public class PetEasterBunny extends Pet {

    ArrayList<Byte> eggDatas = new ArrayList<>();
    Random r = new Random();

    public PetEasterBunny(UUID owner) {
        super(owner, PetType.EASTERBUNNY);
        if (owner != null) {
            eggDatas.add((byte) 0x32);
            eggDatas.add((byte) 0x3d);
            eggDatas.add((byte) 0x5e);
            eggDatas.add((byte) 0x36);
            eggDatas.add((byte) 0x3a);
            eggDatas.add((byte) 0x38);
            eggDatas.add((byte) 0x62);
        }
    }

    @Override
    protected void onUpdate() {
        final Item ITEM = entity.getWorld().dropItem(((Rabbit) entity).getEyeLocation(), ItemFactory.create(Material.MONSTER_EGG, eggDatas.get(r.nextInt(6)), UUID.randomUUID().toString()));
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
