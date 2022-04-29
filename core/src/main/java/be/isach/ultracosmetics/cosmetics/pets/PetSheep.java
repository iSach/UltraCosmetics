package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;

/**
 * Represents an instance of a sheep pet summoned by a player.
 *
 * @author iSach
 * @since 08-12-2015
 */
public class PetSheep extends Pet {
    public PetSheep(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("sheep"), XMaterial.WHITE_WOOL);
    }

    @Override
    public void onUpdate() {
        dropItem = ItemFactory.randomItemFromTag(XTag.WOOL);
        super.onUpdate();
    }
}
