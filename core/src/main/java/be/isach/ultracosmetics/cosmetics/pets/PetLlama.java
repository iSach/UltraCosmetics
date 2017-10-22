package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Llama;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * Represents an instance of a llama pet summoned by a player.
 *
 * @author RadBuilder
 * @since 07-02-2017
 */
public class PetLlama extends Pet {
	public PetLlama(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, ultraCosmetics, PetType.getByName("llama"), ItemFactory.create(Material.WOOL, (byte) 0x0, UltraCosmeticsData.get().getItemNoPickupString()));
		Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
			if (getOwner() != null && getEntity() != null) {
				Llama llama = (Llama) entity;
				llama.setTamed(true);
			}
		}, 30);
	}
}
