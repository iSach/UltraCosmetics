package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Particles;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.cryptomorin.xseries.XSound;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance of a fungun gadget summoned by a player.
 *
 * @author iSach
 * @since 10-12-2015
 */
public class GadgetFunGun extends Gadget {

    private List<Projectile> projectiles = new ArrayList<>();

    public GadgetFunGun(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("fungun"), ultraCosmetics);
    }

    @Override
    void onRightClick() {
        for (int i = 0; i < 5; i++)
            projectiles.add(getPlayer().launchProjectile(Snowball.class));
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (!projectiles.contains(projectile)) return;

        Location location = projectile.getLocation();

        for (Projectile snowball : projectiles)
            snowball.remove();

        Particles.LAVA.display(1.3f, 1f, 1.3f, location, 16);
        Particles.HEART.display(0.8f, 0.8f, 0.8f, location, 20);
        XSound.ENTITY_CAT_PURREOW.play(getPlayer(), 1.4f, 1.5f);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onClear() {
        HandlerList.unregisterAll(this);
    }
}
