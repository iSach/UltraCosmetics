package me.isach.ultracosmetics.cosmetics.gadgets;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by sacha on 10/08/15.
 */
public class GadgetAntiGravity extends Gadget {

    ArmorStand as;
    boolean running;


    public GadgetAntiGravity(UUID owner) {
        super(Material.EYE_OF_ENDER, (byte) 0x0, "AntiGravity", "ultracosmetics.gadgets.antigravity", 30, owner, GadgetType.ANTIGRAVITY);
        Core.registerListener(this);
    }

    @Override
    void onInteractRightClick() {
        as = getPlayer().getWorld().spawn(getPlayer().getLocation(), ArmorStand.class);
        as.setGravity(false);
        as.setSmall(true);
        running = true;
        as.setVisible(false);
        as.setHelmet(new ItemStack(Material.SEA_LANTERN));
        Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                as.remove();
                as = null;
                Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        running = false;
                    }
                }, 20);
            }
        }, 220);
    }

    @Override
    void onInteractLeftClick() {

    }

    @Override
    void onUpdate() {
        if (as != null && as.isValid()) {
            as.setHeadPose(as.getHeadPose().add(0, 0.1, 0));
            as.getWorld().spigot().playEffect(as.getEyeLocation(), Effect.PORTAL, 0, 0, 3, 3, 3, 0, 150, 64);
            as.getWorld().spigot().playEffect(as.getEyeLocation(), Effect.WITCH_MAGIC, 0, 0, .3f, 0.3f, 0.3f, 0, 5, 64);
            for (Entity ent : as.getNearbyEntities(3, 2, 3)) {
                if (ent instanceof LivingEntity && !(ent instanceof ArmorStand))
                    MathUtils.applyVelocity(ent, new Vector(0, 0.05, 0));
            }
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        try {
            if (running) {
                if (as != null && as.isValid() && event.getReason().contains("Fly")) {
                    if (as.getLocation().distance(event.getPlayer().getLocation()) < 8) {
                        event.setCancelled(true);
                        System.out.println("UltraCosmetics >> Cancelling invalid Flight KicK.");
                        return;
                    }
                }
                event.setCancelled(true);
                System.out.println("UltraCosmetics >> Cancelling invalid Flight KicK.");
                return;
            }
        } catch (Exception exc) {
        }
    }

    @Override
    public void clear() {
        HandlerList.unregisterAll(this);
    }
}
