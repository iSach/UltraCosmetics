package me.isach.ultracosmetics.cosmetics.gadgets;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by sacha on 10/08/15.
 */
public class GadgetAntiGravity extends Gadget {

    public GadgetAntiGravity(UUID owner) {
        super(Material.EYE_OF_ENDER, (byte) 0x0, "AntiGravity", "ultracosmetics.gadgets.antigravity", 30, owner, GadgetType.ANTIGRAVITY);
    }

    @Override
    void onInteractRightClick() {
        final ArmorStand as = getPlayer().getWorld().spawn(getPlayer().getLocation(), ArmorStand.class);
        as.setGravity(false);
        as.setSmall(true);
        as.setVisible(false);
        as.setHelmet(new ItemStack(Material.SEA_LANTERN));
        final int taskId = Bukkit.getScheduler().runTaskTimer(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                as.setHeadPose(as.getHeadPose().add(0, 0.1, 0));
                as.getWorld().spigot().playEffect(as.getEyeLocation(), Effect.PORTAL, 0, 0, 3, 3, 3, 0, 150, 64);
                as.getWorld().spigot().playEffect(as.getEyeLocation(), Effect.WITCH_MAGIC, 0, 0, .3f, 0.3f, 0.3f, 0, 5, 64);
                for(Entity ent : as.getNearbyEntities(3, 2, 3)) {
                    MathUtils.applyVelocity(ent, new Vector(0, 0.1, 0));
                }
            }
        }, 0, 2).getTaskId();
        Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                as.remove();
                Bukkit.getScheduler().cancelTask(taskId);
            }
        }, 220);
    }

    @Override
    void onInteractLeftClick() {

    }

    @Override
    void onUpdate() {

    }

    @Override
    public void clear() {

    }
}
