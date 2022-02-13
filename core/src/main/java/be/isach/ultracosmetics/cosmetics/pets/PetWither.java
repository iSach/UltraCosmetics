package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

/**
 * Represents an instance of a wither pet summoned by a player.
 *
 * @author iSach
 * @since 08-12-2015
 */
public class PetWither extends Pet {

    public PetWither(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, ultraCosmetics, PetType.getByName("wither"), null);
    }

    @Override
    public void onUpdate() {
        if (!SettingsManager.getConfig().getBoolean("Pets-Are-Babies")) return;
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().resetWitherSize((Wither) getEntity());
    }

    @EventHandler
    public void onShoot(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() == entity) {
            event.setCancelled(true);
        }
    }
}
