package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sacha on 12/10/15.
 */
public class GadgetParachute extends Gadget {

    List<Chicken> chickens = new ArrayList<>();
    boolean active;

    public GadgetParachute(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.PARACHUTE, ultraCosmetics);

        if (owner != null)
            Bukkit.getPluginManager().registerEvents(this, UltraCosmetics.getInstance());
    }


    @Override
    void onRightClick() {

        Location loc = getPlayer().getLocation();

        getPlayer().teleport(loc.clone().add(0, 35, 0));

        getPlayer().setVelocity(new Vector(0, 0, 0));

        for (int i = 0; i < 20; i++) {
            Chicken chicken = (Chicken) getPlayer().getWorld().spawnEntity(getPlayer().getLocation().add(MathUtils.randomDouble(0, 0.5), 3, MathUtils.randomDouble(0, 0.5)), EntityType.CHICKEN);
            chickens.add(chicken);
            chicken.setLeashHolder(getPlayer());
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                active = true;
            }
        }, 5);
    }

    @Override
    void onLeftClick() {
    }

    private void killParachute() {
        for (Chicken chicken : chickens) {
            chicken.setLeashHolder(null);
            chicken.remove();
        }
        MathUtils.applyVelocity(getPlayer(), new Vector(0, 0.15, 0));
        active = false;
    }

    @EventHandler
    public void onLeashBreak(EntityUnleashEvent event) {
        if (chickens.contains(event.getEntity()))
            for (Entity ent : event.getEntity().getNearbyEntities(1, 1, 1))
                if (ent instanceof Item && ((Item) ent).getItemStack().getType() == Material.LEASH)
                    ent.remove();
    }

    @Override
    void onUpdate() {
        if (active) {
            if (!getPlayer().isOnGround() && getPlayer().getVelocity().getY() < -0.3)
                MathUtils.applyVelocity(getPlayer(), getPlayer().getVelocity().add(new Vector(0, 0.1, 0)), true);
            if (getPlayer().isOnGround())
                killParachute();

        }
    }

    @Override
    public void onClear() {
        killParachute();
        HandlerList.unregisterAll(this);
    }
}
