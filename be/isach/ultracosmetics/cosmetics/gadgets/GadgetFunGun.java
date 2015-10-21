package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.*;
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
        super(Material.BLAZE_ROD, (byte) 0x0, "FunGun", "ultracosmetics.gadgets.fungun", 2, owner, GadgetType.FUNGUN);

        if (owner != null)
            Bukkit.getPluginManager().registerEvents(this, Core.getPlugin());
    }

    @Override
    void onInteractRightClick() {
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

        UtilParticles.play(location, Effect.LAVA_POP, 0, 0, 1.3f, 1f, 1.3f, 0, 16);
        UtilParticles.play(location, Effect.HEART, 0, 0, 0.8f, 0.8f, 0.8f, 0, 20);
        location.getWorld().playSound(location, Sound.CAT_MEOW, 2, 1);
    }

    @Override
    void onInteractLeftClick() {}

    @Override
    void onUpdate() {}

    @Override
    public void clear() {
        HandlerList.unregisterAll(this);
    }
}
