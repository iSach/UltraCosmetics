package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;

import org.bukkit.entity.Axolotl;

import com.cryptomorin.xseries.XMaterial;

/**
 * Represents an instance of a axolotl pet summoned by a player.
 *
 * @author Chris6ix
 * @since 14-01-2022
 */
public class PetAxolotl extends Pet {
    public PetAxolotl(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("axolotl"), ItemFactory.create(XMaterial.AXOLOTL_BUCKET, UltraCosmeticsData.get().getItemNoPickupString()));
    }

    @Override
    public void setupEntity() {
        // For some strange reason, an axolotl has a default movement speed of 1.0, which is higher
        // than the default speed of every other entity except dolphin.
        if (!SettingsManager.getConfig().getBoolean("Pets.Axolotl.Fast")) {
            UltraCosmeticsData.get().getVersionManager().getAncientUtil().setSpeed((Axolotl)entity, 0.6);
        }
    }
}
