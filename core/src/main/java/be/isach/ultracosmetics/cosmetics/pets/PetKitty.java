package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Ocelot;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * Represents an instance of a kitten pet summoned by a player.
 *
 * @author iSach
 * @since 08-12-2015
 */
public class PetKitty extends Pet {
	
	Random r = new Random();
	
	public PetKitty(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, ultraCosmetics, PetType.KITTY);
		Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
			if (getOwner() != null && getEntity() != null) {
				Ocelot ocelot = (Ocelot) getEntity();
				ocelot.setTamed(true);
				ocelot.setSitting(false);
				ocelot.setCatType(Ocelot.Type.RED_CAT);
			}
		}, 30);
	}
	
	@Override
	public void onUpdate() {
		final Item item = entity.getWorld().dropItem(((Ocelot) entity).getEyeLocation(), ItemFactory.create(Material.RAW_FISH, (byte) 0x0, UUID.randomUUID().toString()));
		item.setPickupDelay(30000);
		item.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2.0 + 0.3, r.nextDouble() - 0.5).multiply(0.4));
		items.add(item);
		Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
			item.remove();
			items.remove(item);
		}, 5);
	}
}
