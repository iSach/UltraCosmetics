package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.XMaterial;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance of a sheep pet summoned by a player.
 *
 * @author iSach
 * @since 08-12-2015
 */
public class PetSheep extends Pet {
    private final List<XMaterial> woolColors = new ArrayList<>();
    public PetSheep(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("sheep"), ItemFactory.rename(XMaterial.WHITE_WOOL.parseItem(), UltraCosmeticsData.get().getItemNoPickupString()));
        for (XMaterial mat : XMaterial.VALUES) {
            if (mat.name().endsWith("_WOOL")) {
                woolColors.add(mat);
            }
        }
    }

    @Override
    public void onUpdate() {
        dropItem = woolColors.get(random.nextInt(woolColors.size())).parseItem();
        super.onUpdate();
    }
}
