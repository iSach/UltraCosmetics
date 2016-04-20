package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
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
        super(owner, GadgetType.BATBLASTER);
    }

    @Override
    void onRightClick() {
        this.playerVelocity.put(getPlayer(), getPlayer().getEyeLocation());
        this.isActive.put(getPlayer(), Long.valueOf(System.currentTimeMillis()));

        this.bats.put(getPlayer(), new ArrayList());

        for (int i = 0; i < 16; i++) {
            ((ArrayList) this.bats.get(getPlayer())).add(getPlayer().getWorld().spawn(getPlayer().getEyeLocation(), Bat.class));
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                onClear();
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

                    for (Player other : getPlayer().getWorld().getPlayers())
                        if (!other.equals(getPlayer()) && UltraCosmetics.getCustomPlayer(other).hasGadgetsEnabled() && hitPlayer(bat.getLocation(), other)) {

                            Vector v = bat.getLocation().getDirection();
                            v.normalize();
                            v.multiply(.4d);
                            v.setY(v.getY() + 0.2d);

                            if (v.getY() > 7.5)
                                v.setY(7.5);

                            if (other.isOnGround())
                                v.setY(v.getY() + 0.4d);

                            other.setFallDistance(0);

                            if (affectPlayers)
                                MathUtils.applyVelocity(other, bat.getLocation().getDirection().add(new Vector(0, .4f, 0)));


                            switch (UltraCosmetics.getServerVersion()) {
                                case v1_8_R3:
                                    bat.getWorld().playSound(bat.getLocation(), Sound.valueOf("BAT_HURT"), 1.0f, 1.0f);
                                    break;
                                case v1_9_R1:
                                    bat.getWorld().playSound(bat.getLocation(), Sound.ENTITY_BAT_HURT, 1.0f, 1.0f);
                                    break;
                            }
                            UtilParticles.display(Particles.SMOKE_NORMAL, bat.getLocation());

                            bat.remove();
                        }
                }

            }
        }
    }

    @Override
    public void onClear() {
        this.isActive.remove(getPlayer());
        this.playerVelocity.remove(getPlayer());
        if (this.bats.containsKey(getPlayer())) {
            for (Bat bat : this.bats.get(getPlayer())) {
                if (bat.isValid()) {
                    UtilParticles.display(Particles.SMOKE_LARGE, bat.getLocation());
                }
                bat.remove();
            }
            this.bats.remove(getPlayer());
        }
        HandlerList.unregisterAll(this);
    }

    @Override
    void onLeftClick() {
    }
}
