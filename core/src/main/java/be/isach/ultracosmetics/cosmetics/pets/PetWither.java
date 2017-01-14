package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.entity.Wither;

/**
 * Represents an instance of a wither pet summoned by a player.
 * 
 * @author 	iSach
 * @since 	08-12-2015
 */
public class PetWither extends Pet {

    public PetWither(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.WITHER);
    }

    @Override
    public void onUpdate() {
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().resetWitherSize((Wither)getEntity());
    }
}
