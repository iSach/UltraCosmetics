package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.EntityBlockFormEvent;

/**
 * Represents an instance of a snowman pet summoned by a player.
 *
 * @author RadBuilder
 * @since 07-02-2017
 */
public class PetSnowman extends Pet {
    public PetSnowman(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("snowman"), ItemFactory.create(XMaterial.SNOWBALL, UltraCosmeticsData.get().getItemNoPickupString()));
    }

    @EventHandler
    public void onTrail(EntityBlockFormEvent event) {
        if (event.getEntity() == entity) {
            event.setCancelled(true);
        }
    }
}
