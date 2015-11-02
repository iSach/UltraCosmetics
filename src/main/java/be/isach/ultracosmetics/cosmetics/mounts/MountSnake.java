package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.util.MathUtils;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.Navigation;
import net.minecraft.server.v1_8_R3.PathEntity;
import org.bukkit.Bukkit;
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
        for (Player p : tailMap.keySet())
            for (Entity ent : tailMap.get(p))
                ent.remove();
        tailMap.clear();
        if (getPlayer() != null && Core.getCustomPlayer(getPlayer()) != null) {
            Core.getCustomPlayer(getPlayer()).currentMount = null;
        }
        if (entityType != EntityType.SQUID
                && entityType != EntityType.SLIME
                && entityType != EntityType.SPIDER) {
            if (ent.getPassenger() != null)
                ent.getPassenger().eject();
            if (ent != null)
                ent.remove();
        } else {
            if (customEnt.passenger != null)
                customEnt.passenger = null;
            if (customEnt != null) {
                customEntities.remove(customEnt);
                customEnt.dead = true;
            }
        }
        if (getPlayer() != null)
            getPlayer().sendMessage(MessageManager.getMessage("Mounts.Despawn").replace("%mountname%", getMenuName()));
        owner = null;
        HandlerList.unregisterAll(this);
        HandlerList.unregisterAll(listener);
    }

    @Override
    void onUpdate() {
        if (getPlayer() != null)
            Bukkit.getScheduler().runTask(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {

                    double multiplier = 0.5D;

                    Vector vel = getPlayer().getLocation().getDirection().setY(0).normalize().multiply(4);

                    Creature before = null;
                    for (int i = 0; i < ((ArrayList) tailMap.get(getPlayer())).size(); i++) {
                        Creature tail = (Creature) ((ArrayList) tailMap.get(getPlayer())).get(i);
                        Location loc = getPlayer().getLocation().add(vel);
                        if (i == 0)
                            loc = tail.getLocation().add(vel);
                        if (before != null)
                            loc = before.getLocation();
                        if (loc.toVector().subtract(tail.getLocation().toVector()).length() > 12.0D)
                            loc = tail.getLocation().add(traj(tail.getLocation(), loc).multiply(12));
                        if (before != null) {
                            Location tp = before.getLocation().add(traj2D(before, tail).multiply(1.4D));
                            tp.setPitch(tail.getLocation().getPitch());
                            tp.setYaw(tail.getLocation().getYaw());
                            tail.teleport(tp);
                        }
                        EntityCreature ec = ((CraftCreature) tail).getHandle();
                        ec.S = 1;
                        ec.getNavigation().a(loc.getX(), loc.getY(), loc.getZ(), (1.0D + 2.0D * multiplier) * 1.0D);

                        before = tail;

                    }
                }
            });
    }

    public Vector traj2D(Entity a, Entity b) {
        return b.getLocation().toVector().subtract(a.getLocation().toVector()).setY(0).normalize();
    }

    public Vector traj(Location a, Location b) {
        return b.toVector().subtract(a.toVector()).setY(0).normalize();
    }

    public Vector traj(Entity a, Entity b) {
        return b.getLocation().toVector().subtract(a.getLocation().toVector()).setY(0).normalize();
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
            tail.setNoDamageTicks(Integer.MAX_VALUE);
            tail.setRemoveWhenFarAway(false);
            tail.teleport(loc);
            ((ArrayList) tailMap.get(player)).add(tail);
            tail.setColor(DyeColor.values()[color]);
            /*if (tail != ent)
                tail.setPassenger(tail.getWorld().spawnEntity(tail.getLocation(), EntityType.MINECART));
        */
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
