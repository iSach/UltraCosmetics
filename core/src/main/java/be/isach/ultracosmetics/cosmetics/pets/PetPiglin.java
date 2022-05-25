package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.entity.Piglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PiglinBarterEvent;

import com.cryptomorin.xseries.XMaterial;

/**
 * Represents an instance of a piglin pet summoned by a player.
 *
 * @author Chris6ix
 * @since 16-01-2022
 */
public class PetPiglin extends Pet {
    public PetPiglin(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("piglin"), XMaterial.GOLD_INGOT);
    }

    @Override
    public void setupEntity() {
        Piglin piglin = (Piglin) entity;
        piglin.setImmuneToZombification(true);
        piglin.getEquipment().clear();
    }

    @EventHandler
    public void onBarter(PiglinBarterEvent event) {
        if (event.getEntity() == entity) {
            event.setCancelled(true);
        }
    }
}
