package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.DyeColor;
import org.bukkit.entity.Wolf;

/**
 * Represents an instance of a dog pet summoned by a player.
 *
 * @author iSach
 * @since 08-12-2015
 */
public class PetDog extends Pet {
    public PetDog(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("dog"), ItemFactory.create(XMaterial.BONE, UltraCosmeticsData.get().getItemNoPickupString()));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        ((Wolf)entity).setCollarColor(DyeColor.values()[random.nextInt(16)]);
    }
}
