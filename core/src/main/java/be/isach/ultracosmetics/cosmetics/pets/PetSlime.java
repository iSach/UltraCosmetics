package be.isach.ultracosmetics.cosmetics.pets;

import org.bukkit.entity.Slime;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import com.cryptomorin.xseries.XMaterial;

/**
 * Represents an instance of a slime pet summoned by a player.
 *
 * @author datatags
 * @since 18-01-2022
 */

public class PetSlime extends Pet {
    public PetSlime(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("slime"), XMaterial.SLIME_BALL);
    }

    @Override
    public void setupEntity() {
        ((Slime)entity).setSize(1);
    }
}
