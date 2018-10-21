package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.version.SpawnEggs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Rabbit;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Represents an instance of an easter bunny pet summoned by a player.
 *
 * @author iSach
 * @since 08-12-2015
 */
public class PetEasterBunny extends Pet {
	private static final ArrayList<Byte> EGG_DATA = new ArrayList<>(Arrays.asList((byte) 0x32, (byte) 0x3d, (byte) 0x5e, (byte) 0x36, (byte) 0x3a, (byte) 0x38, (byte) 0x62));
	private Random r = new Random();
	
	public PetEasterBunny(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, ultraCosmetics, PetType.getByName("easterbunny"), ItemFactory.rename(SpawnEggs.getEggFromData(EGG_DATA.get(0)), UltraCosmeticsData.get().getItemNoPickupString()));
	}
	
	@Override
	public void onUpdate() {
		final Item drop = entity.getWorld().dropItem(((Rabbit) entity).getEyeLocation(), ItemFactory.rename(SpawnEggs.getEggFromData(EGG_DATA.get(r.nextInt(6))), UltraCosmeticsData.get().getItemNoPickupString()));
		drop.setPickupDelay(30000);
		drop.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2.0 + 0.3, r.nextDouble() - 0.5).multiply(0.4));
		items.add(drop);
		Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
			drop.remove();
			items.remove(drop);
		}, 5);
	}
}
