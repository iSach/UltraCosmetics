package me.isach.ultracosmetics.cosmetics.mounts;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.util.MathUtils;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.PathEntity;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by sacha on 10/08/15.
 */
public class MountSnake extends Mount {

    private HashMap<Player, ArrayList<Entity>> tailMap = new HashMap();
    private int color = 1;


    public MountSnake(UUID owner) {
        super(EntityType.SHEEP, Material.SEEDS, (byte) 0, "Snake", "ultracosmetics.mounts.snake", owner, MountType.SNAKE);

        if (owner == null) return;
        color = MathUtils.randomRangeInt(0, 14);
        ((LivingEntity) ent).setNoDamageTicks(Integer.MAX_VALUE);
        ((Sheep) ent).setColor(DyeColor.values()[color]);
        tailMap.put(getPlayer(), new ArrayList());
        ((ArrayList) tailMap.get(getPlayer())).add(ent);
        addSheepToTail(4);
        Core.registerListener(this);
    }

    @Override
    public void clear() {
        for (Player p : tailMap.keySet()) {
            for (Entity ent : tailMap.get(p)) {
                ent.remove();
            }
        }
        tailMap.clear();
        if (getPlayer() != null)
            getPlayer().sendMessage(MessageManager.getMessage("Mounts.Despawn").replace("%mountname%", getMenuName()));
        Core.getCustomPlayer(getPlayer()).currentMount = null;
        ent.remove();
        HandlerList.unregisterAll(this);
    }

    @Override
    void onUpdate() {
        if (tailMap.containsKey(getPlayer())) {
            if (getPlayer().getVehicle() == null) {
                clear();
                return;
            }
            Player player = getPlayer();
            double mult = 0.4D;
            if (this.tailMap.containsKey(player)) {
                mult += Math.min(0.7D, ((ArrayList) this.tailMap.get(player)).size() / 180);
            }
            Vector vel = player.getLocation().getDirection().setY(0).normalize().multiply(4);
            Creature before = null;
            for (int i = 0; i < ((ArrayList) this.tailMap.get(player)).size(); i++) {
                Creature tail = (Creature) ((ArrayList) this.tailMap.get(player)).get(i);
                Location loc = player.getLocation().add(vel);
                if (i == 0) {
                    loc = tail.getLocation().add(vel);
                }
                if (MathUtils.offset(loc, tail.getLocation()) > 10) {
                    loc = tail.getLocation().add((loc.toVector().subtract(tail.getLocation().toVector().normalize()).multiply(12)));
                }
                if (before != null) {
                    loc = before.getLocation();
                }
                if (before != null) {
                    Location tp = before.getLocation().add(tail.getLocation().toVector().subtract(before.getLocation().toVector()).setY(0).normalize().multiply(1.4D));
                    tp.setPitch(tail.getLocation().getPitch());
                    tp.setYaw(tail.getLocation().getYaw());
                    tail.teleport(tp);
                }
                EntityCreature ec = ((CraftCreature) tail).getHandle();
                PathEntity path;
                path = ec.getNavigation().a(loc.getX() + 1, loc.getY(), loc.getZ() + 1);
                ec.getNavigation().a(path, (1.0D + 2.0D * mult) * 1.0D);
                ec.getNavigation().a((1.0D + 2.0D * mult) * 1.0D);

                before = tail;
            }

        }
    }

    public Vector traj(Entity a, Entity b) {
        return b.getLocation().toVector().subtract(a.getLocation().toVector()).normalize();
    }

    public void addSheepToTail(int amount) {
        Player player = getPlayer();
        for (int i = 0; i < amount; i++) {
            Location loc = player.getLocation();
            if (!((ArrayList) tailMap.get(player)).isEmpty()) {
                loc = ((Creature) ((ArrayList) tailMap.get(player)).get(((ArrayList) tailMap.get(player)).size() - 1)).getLocation();
            }
            if (((ArrayList) tailMap.get(player)).size() > 1) {
                loc.add(traj((Entity) ((ArrayList) tailMap.get(player)).get(((ArrayList) tailMap.get(player)).size() - 2), (Entity) ((ArrayList) tailMap.get(player)).get(((ArrayList) tailMap.get(player)).size() - 1)));
            } else {
                loc.subtract(player.getLocation().getDirection().setY(0));
            }
            Sheep tail = (loc.getWorld().spawn(loc, Sheep.class));
            ((LivingEntity) tail).setNoDamageTicks(Integer.MAX_VALUE);
            tail.setRemoveWhenFarAway(false);
            tail.teleport(loc);
            ((ArrayList) tailMap.get(player)).add(tail);
            tail.setColor(DyeColor.values()[color]);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        try {
            if (tailMap.get(getPlayer()).contains(event.getEntity()))
                event.setCancelled(true);
        } catch (Exception exc) {
        }
    }

}
