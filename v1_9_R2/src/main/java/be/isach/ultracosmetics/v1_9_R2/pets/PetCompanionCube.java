package be.isach.ultracosmetics.v1_9_R2.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.pets.PetType;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;
/**
 * Created by ataranlen on 6/26/2016.
 */
public class PetCompanionCube extends CustomEntityPet {

    Random r = new Random();

    public PetCompanionCube(UUID owner) {
        super(owner, PetType.PUMPLING);
        if (owner != null) {

        }
    }

    @Override
    protected void onUpdate() {
        final Item ITEM = customEntity.getEntity().getWorld().dropItem(((Zombie) customEntity.getEntity()).getEyeLocation(), ItemFactory.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTE3NWJkZjQ3YWVhMWE0YmYxZDM0OWJlNmI3ZmE0YWIzN2Y0Nzk2NzJmNGM0M2FjYTU3NTExYjQyN2FiNCJ9fX0=", "§8§oHat"));
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
