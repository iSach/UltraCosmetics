package be.isach.ultracosmetics.cosmetics.pets;

import org.bukkit.entity.Slime;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.UCMaterial;

public class PetSlime extends Pet {
    public PetSlime(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("slime"), ItemFactory.create(UCMaterial.SLIME_BALL, UltraCosmeticsData.get().getItemNoPickupString()));
    }

    @Override
    public void onEquip() {
        super.onEquip();
        ((Slime)entity).setSize(1);
    }
}
