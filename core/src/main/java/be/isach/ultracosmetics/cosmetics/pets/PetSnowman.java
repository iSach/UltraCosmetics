package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Snowman;

/**
 * Represents an instance of a snowman pet summoned by a player.
 *
 * @author RadBuilder
 * @since 07-02-2017
 */
public class PetSnowman extends Pet {
    public PetSnowman(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("snowman"), ItemFactory.create(XMaterial.SNOWBALL, UltraCosmeticsData.get().getItemNoPickupString()));
        // setDerp was added in 1.9, so skip the task if we're on 1.8
        if (UltraCosmeticsData.get().getServerVersion() == ServerVersion.v1_8_R3) return;
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            if (getOwner() != null && getEntity() != null) {
                Snowman snowman = (Snowman) getEntity();
                snowman.setDerp(false);
            }
        }, 30);
    }
}
