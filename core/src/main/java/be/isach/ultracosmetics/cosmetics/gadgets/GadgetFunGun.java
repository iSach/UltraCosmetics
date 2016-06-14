package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sacha on 12/10/15.
 */
public class GadgetFunGun extends Gadget {

    private List<Projectile> projectiles = new ArrayList<>();

    public GadgetFunGun(UUID owner) {
        super(owner, GadgetType.FUNGUN);

        if (owner != null)
            Bukkit.getPluginManager().registerEvents(this, UltraCosmetics.getInstance());
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

        UtilParticles.display(Particles.LAVA, 1.3f, 1f, 1.3f, location, 16);
        UtilParticles.display(Particles.HEART, 0.8f, 0.8f, 0.8f, location, 20);
        switch (UltraCosmetics.getServerVersion()) {
            case v1_8_R3:
                getPlayer().playSound(getPlayer().getLocation(), Sound.valueOf("CAT_MEOW"), 1.4f, 1.5f);
                break;
            default:
                getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_CAT_PURREOW, 1.4f, 1.5f);
                break;
        }
    }

    @Override
    void onLeftClick() {
    }

    @Override
    void onUpdate() {}

    @Override
    public void onClear() {
        HandlerList.unregisterAll(this);
    }
}
