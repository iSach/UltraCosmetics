package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.MushroomCow;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * Represents an instance of a mooshroom pet summoned by a player.
 *
 * @author ataranlen
 * @since 06-26-2015
 */
public class PetMooshroom extends Pet {
	Random r = new Random();
	
	public PetMooshroom(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, ultraCosmetics, PetType.MOOSHROOM);
	}
	
	@Override
	public void onUpdate() {
		Item item = entity.getWorld().dropItem(((MushroomCow) entity).getEyeLocation(), ItemFactory.create(Material.RED_MUSHROOM, (byte) 0, UltraCosmeticsData.get().getItemNoPickupString()));
		item.setPickupDelay(30000);
		item.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2.0 + 0.3, r.nextDouble() - 0.5).multiply(0.4));
		Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), item::remove, 5);
	}
}
