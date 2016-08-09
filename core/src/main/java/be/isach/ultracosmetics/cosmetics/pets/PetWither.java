package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import org.bukkit.entity.Wither;

import java.util.UUID;

/**
 * Created by Sacha on 12/10/15.
 */
public class PetWither extends Pet {

    public PetWither(UUID owner) {
        super(owner, PetType.WITHER);
    }

    @Override
    protected void onUpdate() {
        UltraCosmetics.getInstance().getEntityUtil().resetWitherSize((Wither)getEntity());
    }

}
