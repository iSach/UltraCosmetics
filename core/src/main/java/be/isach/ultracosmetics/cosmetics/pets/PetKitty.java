package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.UCMaterial;
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
	public PetKitty(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
		super(owner, ultraCosmetics, PetType.getByName("kitty"), ItemFactory.create(UCMaterial.TROPICAL_FISH, UltraCosmeticsData.get().getItemNoPickupString()));
		Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
			if (getOwner() != null && getEntity() != null) {
				Ocelot ocelot = (Ocelot) getEntity();
				// ocelot.set(true);
				// ocelot.setSitting(false);
				// TODO
				ocelot.setCatType(Ocelot.Type.RED_CAT);
			}
		}, 30);
	}
}
