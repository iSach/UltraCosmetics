package me.isach.ultracosmetics.cosmetics.gadgets;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.util.MathUtils;
import org.bukkit.*;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public class GadgetBatBlaster extends Gadget {

    private HashMap<Player, Long> isActive = new HashMap();
    private HashMap<Player, Location> playerVelocity = new HashMap();
    private HashMap<Player, ArrayList<Bat>> bats = new HashMap();

    public GadgetBatBlaster(UUID owner) {
        super(Material.IRON_BARDING, (byte) 0x0, "BatBlaster", "ultracosmetics.gadgets.batblaster", 7, owner, GadgetType.BATBLASTER);
    }

    @Override
    void onInteractRightClick() {
        this.playerVelocity.put(getPlayer(), getPlayer().getEyeLocation());
        this.isActive.put(getPlayer(), Long.valueOf(System.currentTimeMillis()));

        this.bats.put(getPlayer(), new ArrayList());

        for (int i = 0; i < 16; i++) {
            ((ArrayList) this.bats.get(getPlayer())).add(getPlayer().getWorld().spawn(getPlayer().getEyeLocation(), Bat.class));
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                clear();
            }
        }, 60);
    }

    public boolean hitPlayer(Location loc, Player player) {
        if (loc.add(0, -loc.getY(), 0).toVector().subtract(player.getLocation().add(0, -player.getLocation().getY(), 0).toVector()).length() < 0.8D) {
            return true;
        }
        if (loc.add(0, -loc.getY(), 0).toVector().subtract(player.getLocation().add(0, -player.getLocation().getY(), 0).toVector()).length() < 1.2) {
            if ((loc.getY() > player.getLocation().getY()) && (loc.getY() < player.getEyeLocation().getY())) {
                return true;
            }
        }
        return false;
    }

    @Override
    void onUpdate() {
        Location loc = this.playerVelocity.get(getPlayer());
        if (this.isActive.containsKey(getPlayer())) {
            for (Bat bat : this.bats.get(getPlayer())) {
                if (bat.isValid()) {
                    Vector rand = new Vector((Math.random() - 0.5D) / 3.0D, (Math.random() - 0.5D) / 3.0D, (Math.random() - 0.5D) / 3.0D);
                    bat.setVelocity(loc.getDirection().clone().multiply(0.5D).add(rand));

                    for (Player other : getPlayer().getWorld().getPlayers()) {
                        if (!other.equals(getPlayer())) {

                            if (hitPlayer(bat.getLocation(), other)) {

                                Vector v = bat.getLocation().getDirection();
                                v.normalize();
                                v.multiply(.4d);
                                v.setY(v.getY() + 0.2d);

                                if (v.getY() > 7.5)
                                    v.setY(7.5);

                                if (other.isOnGround())
                                    v.setY(v.getY() + 0.2d);

                                other.setFallDistance(0);

                                MathUtils.applyVelocity(other, bat.getLocation().getDirection().add(new Vector(0, .4f, 0)));


                                bat.getWorld().playSound(bat.getLocation(), Sound.BAT_HURT, 1.0F, 1.0F);
                                bat.getWorld().spigot().playEffect(bat.getLocation(), Effect.SMOKE);

                                bat.remove();
                            }
                        }

                    }

                }
            }
        }
    }

    @Override
    public void clear() {
        this.isActive.remove(getPlayer());
        this.playerVelocity.remove(getPlayer());
        if (this.bats.containsKey(getPlayer())) {
            for (Bat bat : this.bats.get(getPlayer())) {
                if (bat.isValid()) {
                    bat.getWorld().spigot().playEffect(bat.getLocation(), Effect.SMOKE);
                }
                bat.remove();
            }
            this.bats.remove(getPlayer());
        }
    }

    @Override
    void onInteractLeftClick() {
    }
}
