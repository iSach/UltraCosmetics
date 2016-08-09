package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sacha on 15/12/15.
 */
public class GadgetSnowball extends Gadget {

    private List<Snowball> snowballs;

    public GadgetSnowball(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.SNOWBALL, ultraCosmetics);

        if (owner != null)
            snowballs = new ArrayList<>();
    }

    @Override
    void onRightClick() {
        Snowball snowball = getPlayer().launchProjectile(Snowball.class);
        snowball.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.85d));
        snowball.setMetadata("NO_DAMAGE", new FixedMetadataValue(UltraCosmetics.getInstance(), ""));
    }

    @Override
    void onLeftClick() {
    }

    @Override
    void onUpdate() {

    }

    @Override
    public void onClear() {
        for (Snowball snowball : snowballs)
            snowball.remove();
        snowballs.clear();
        snowballs = null;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().hasMetadata("NO_DAMAGE"))
            event.setCancelled(true);
    }

}
