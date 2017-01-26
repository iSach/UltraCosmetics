package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

/**
* Represents an instance of a snake mount.
 * 
 * @author 	iSach
 * @since 	08-10-2015
 */
public class MountSnake extends Mount<Sheep> {

    private HashMap<Player, ArrayList<Entity>> tailMap = new HashMap<Player, ArrayList<Entity>>();
    private int color = 1;


    public MountSnake(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.SNAKE, ultraCosmetics);
    }

    @Override
    public void onEquip() {
        super.onEquip();
        color = MathUtils.randomRangeInt(0, 14);
        ((LivingEntity) entity).setNoDamageTicks(Integer.MAX_VALUE);
        ((Sheep) entity).setColor(DyeColor.values()[color]);
        tailMap.put(getPlayer(), new ArrayList());
        ((ArrayList) tailMap.get(getPlayer())).add(entity);
        addSheepToTail(4);
    }

    @Override
    public void onClear() {
        super.onClear();
        for (Player p : tailMap.keySet())
            for (Entity ent : tailMap.get(p))
                ent.remove();
        tailMap.clear();
    }

    @Override
    public void onUpdate() {
        if (getPlayer() != null)
            Bukkit.getScheduler().runTask(getUltraCosmetics(), new Runnable() {
                @Override
                public void run() {
                    if (getPlayer() != null) {
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

                            UltraCosmeticsData.get().getVersionManager().getEntityUtil().move(tail, loc);

                            before = tail;

                        }
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
