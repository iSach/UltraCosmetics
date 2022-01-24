package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Wolf;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * Represents an instance of a dog pet summoned by a player.
 *
 * @author iSach
 * @since 08-12-2015
 */
public class PetDog extends Pet {

    Random r = new Random();

    public PetDog(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("dog"), ItemFactory.create(XMaterial.BONE, UltraCosmeticsData.get().getItemNoPickupString()));
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            if (getOwner() != null && getEntity() != null) {
                Wolf w = (Wolf) entity;
                w.setTamed(true);
                w.setSitting(false);
            }
        }, 30);
    }

    @Override
    public void onUpdate() {
        Wolf wolf = (Wolf) entity;
        wolf.setCollarColor(DyeColor.values()[r.nextInt(15)]);
        final Item drop = entity.getWorld().dropItem(((Wolf) entity).getEyeLocation(), ItemFactory.create(XMaterial.BONE, UltraCosmeticsData.get().getItemNoPickupString()));
        drop.setPickupDelay(30000);
        drop.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2.0 + 0.3, r.nextDouble() - 0.5).multiply(0.4));
        items.add(drop);
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            drop.remove();
            items.remove(drop);
        }, 5);
    }
}
