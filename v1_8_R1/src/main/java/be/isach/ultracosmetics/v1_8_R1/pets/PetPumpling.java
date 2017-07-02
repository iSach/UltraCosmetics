package be.isach.ultracosmetics.v1_8_R1.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * @author RadBuilder
 */
public class PetPumpling extends CustomEntityPet {
	public PetPumpling(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, ultraCosmetics, PetType.getByName("pumpling"), ItemFactory.create(Material.JACK_O_LANTERN, (byte) 0x0, UltraCosmeticsData.get().getItemNoPickupString()));
	}
}
