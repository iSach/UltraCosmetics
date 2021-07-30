package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents an instance of a bat blaster gadget summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public class GadgetBatBlaster extends Gadget {

    private boolean active = false;
    private Location playerVelocity;
    private List<Bat> bats;

    public GadgetBatBlaster(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("batblaster"), ultraCosmetics);
    }

    @Override
    void onRightClick() {
        this.active = true;
        this.playerVelocity = getPlayer().getEyeLocation();
        this.bats = new ArrayList<>();

        for (int i = 0; i < 16; i++) {
            this.bats.add(getPlayer().getWorld().spawn(getPlayer().getEyeLocation(), Bat.class));
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), this::clean, 60);
    }

    public boolean hitPlayer(Location location, Player player) {
        Vector locVec = location.add(0, -location.getY(), 0).toVector();
        Vector playerVec = player.getLocation().add(0, -player.getLocation().getY(), 0).toVector();
        double vecLength = locVec.subtract(playerVec).length();

        if (vecLength < 0.8D) {
            return true;
        }

        if (vecLength < 1.2) {
            return (location.getY() > player.getLocation().getY()) && (location.getY() < player.getEyeLocation().getY());
        }

        return false;
    }

    @Override
    public void onUpdate() {
        try {
            if (active && bats != null && !bats.isEmpty()) {
                bats.stream().filter(Entity::isValid).forEach(bat -> {
                    Vector rand = new Vector((Math.random() - 0.5D) / 3.0D, (Math.random() - 0.5D) / 3.0D,
                            (Math.random() - 0.5D) / 3.0D);
                    if (bat != null && playerVelocity != null) {
                        bat.setVelocity(playerVelocity.getDirection().clone().multiply(0.5D).add(rand));
                    }
                    getPlayer().getWorld().getPlayers().stream()
                            .filter(other -> !other.equals(getPlayer()) && getOwner().hasGadgetsEnabled() && hitPlayer(
                                    bat.getLocation(), other)).forEachOrdered(other -> {

                        Vector v = bat.getLocation().getDirection();
                        v.normalize();
                        v.multiply(.4d);
                        v.setY(v.getY() + 0.2d);

                        if (v.getY() > 7.5) {
                            v.setY(7.5);
                        }

                        if (other.isOnGround()) {
                            v.setY(v.getY() + 0.4d);
                        }

                        other.setFallDistance(0);

                        if (affectPlayers) {
                            MathUtils.applyVelocity(other, bat.getLocation().getDirection().add(new Vector(0, .4f, 0)));
                        }

                        SoundUtil.playSound(bat.getLocation(), Sounds.BAT_HURT, 1.0f, 1.0f);
                        UtilParticles.display(Particles.SMOKE_NORMAL, bat.getLocation());

                        bat.remove();
                    });
                });
            } else {
                playerVelocity = null;
                clean();
            }
        } catch (Exception e) {
            // TODO
        }
    }

    private void clean() {
        active = false;
        playerVelocity = null;
        if (bats != null) {
            synchronized (bats) {
                Bukkit.getScheduler().runTask(getUltraCosmetics(), () -> {
                    for (Iterator<Bat> iterator = bats.iterator(); iterator.hasNext(); ) {
                        Bat bat = iterator.next();
                        if (bat.isValid()) {
                            UtilParticles.display(Particles.SMOKE_LARGE, bat.getLocation());
                        }
                        bat.remove();
                        iterator.remove();
                    }
                });
            }
            bats.clear();
        }
    }

    @Override
    public void onClear() {
        clean();
    }

    @Override
    void onLeftClick() {
    }
}
