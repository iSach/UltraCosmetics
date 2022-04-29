package be.isach.ultracosmetics.cosmetics.pets;

import com.cryptomorin.xseries.XMaterial;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.version.VersionManager;

import org.bukkit.entity.Horse;

/**
 * Represents an instance of a horse pet summoned by a player.
 *
 * @author Chris6ix
 * @since 06-04-2022
 */
public class PetHorse extends Pet {
    public PetHorse(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("horse"), ItemFactory.create(VersionManager.IS_VERSION_1_13 ? XMaterial.LEATHER_HORSE_ARMOR : XMaterial.LEATHER, UltraCosmeticsData.get().getItemNoPickupString()));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setupEntity() {
        if (UltraCosmeticsData.get().getServerVersion() == ServerVersion.v1_8_R3) {
            // if we don't set this, it sometimes spawns a donkey instead of a horse
            ((Horse)entity).setVariant(org.bukkit.entity.Horse.Variant.HORSE);
        }
    }
}
