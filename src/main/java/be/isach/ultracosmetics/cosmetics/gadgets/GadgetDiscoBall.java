package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.*;
import com.xxmicloxx.NoteBlockAPI.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.Song;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public class GadgetDiscoBall extends Gadget {

    Random r = new Random();
    int i = 0;
    double i2 = 0;
    ArmorStand armorStand;
    boolean running = false;
    PositionSongPlayer positionSongPlayer;

    public GadgetDiscoBall(UUID owner) {
        super(owner, GadgetType.DISCOBALL);
    }

    @Override
    public void onClear() {
        try {
            running = false;
//            if (UltraCosmetics.usingSpigot())
//                armorStand.playEffect(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d), Effect.STEP_SOUND, Material.STAINED_CLAY.getId(), 4, 0, 0, 0, 1, 200, 32);
            armorStand.remove();
            armorStand = null;
            i = 0;
            i2 = 0;
            UltraCosmetics.getInstance().discoBalls.remove(this);
            if (UltraCosmetics.getInstance().isNoteBlockAPIEnabled())
                positionSongPlayer.setPlaying(false);
        } catch (Exception exc) {
        }
        HandlerList.unregisterAll(this);
    }

    @Override
    void onRightClick() {
        armorStand = (ArmorStand) getPlayer().getWorld().spawnEntity(getPlayer().getLocation().add(0, 3, 0), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setSmall(false);
        armorStand.setHelmet(ItemFactory.create(Material.STAINED_GLASS, (byte) r.nextInt(15), " "));
        running = true;
        UltraCosmetics.getInstance().discoBalls.add(this);
        if (Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
            File[] files = new File(UltraCosmetics.getInstance().getDataFolder().getPath() + "/songs/").listFiles();
            List<File> songs = new ArrayList<>();
            for (File f : files)
                if (f.getName().contains(".nbs")) songs.add(f);
            File song = songs.get(new Random().nextInt(songs.size()));
            Song s = NBSDecoder.parse(song);
            positionSongPlayer = new PositionSongPlayer(s);

            positionSongPlayer.setTargetLocation(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d));

            positionSongPlayer.setPlaying(true);

            for (Player p : Bukkit.getOnlinePlayers()) {
                positionSongPlayer.addPlayer(p);
            }

            positionSongPlayer.setVolume((byte) 100);
            positionSongPlayer.setFadeStart((byte) 25);
            Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmetics.getInstance(), new BukkitRunnable() {
                @Override
                public void run() {
                    positionSongPlayer.setPlaying(false);
                }
            }, 20 * 20);
        }
        Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                onClear();
            }
        }, 20 * 20);
    }

    @Override
    void onUpdate() {
        if (running) {
            armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0.2, 0));
            armorStand.setHelmet(ItemFactory.create(Material.STAINED_GLASS, (byte) r.nextInt(15), " "));
            UtilParticles.display(Particles.SPELL, armorStand.getEyeLocation(), 1, 1f);
            UtilParticles.display(Particles.SPELL_INSTANT, armorStand.getEyeLocation(), 1, 1f);
            Location loc = armorStand.getEyeLocation().add(MathUtils.randomDouble(-4, 4), MathUtils.randomDouble(-3, 3), MathUtils.randomDouble(-4, 4));
            Particles.NOTE.display(new Particles.NoteColor(r.nextInt(25)), loc, 128);
            double angle, angle2, x, x2, z, z2;
            angle = 2 * Math.PI * i / 100;
            x = Math.cos(angle) * 4;
            z = Math.sin(angle) * 4;
            if (UltraCosmetics.usingSpigot())
                drawParticleLine(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5).clone().add(x, 0, z), armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5), false, 20);
            i += 6;
            angle2 = 2 * Math.PI * i2 / 100;
            x2 = Math.cos(angle2) * 4;
            z2 = Math.sin(angle2) * 4;
            drawParticleLine(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5), armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5).add(x2, 0, z2), true, 50);
            i2 += 0.4;
            for (Entity ent : getNearbyEntities(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d), 7.5))
                if (ent.isOnGround()
                        && affectPlayers)
                    MathUtils.applyVelocity(ent, new Vector(0, 0.3, 0));


            for (Block b : BlockUtils.getBlocksInRadius(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d), 10, false))
                if (b.getType() == Material.WOOL || b.getType() == Material.CARPET)
                    BlockUtils.setToRestore(b, b.getType(), (byte) r.nextInt(15), 4);

        }
    }

    public ArrayList<Entity> getNearbyEntities(Location loc, double distance) {
        ArrayList<Entity> entities = new ArrayList<>();
        for (Entity ent : loc.getWorld().getEntities()) {
            if (ent.getLocation().distance(loc) <= distance) {
                entities.add(ent);
            }
        }
        return entities;
    }

    public void drawParticleLine(Location a, Location b, boolean dust, int particles) {
        Location location = a.clone();
        Location target = b.clone();
        double amount = particles;
        Vector link = target.toVector().subtract(location.toVector());
        float length = (float) link.length();
        link.normalize();

        float ratio = length / particles;
        Vector v = link.multiply(ratio);
        if (!dust)
            MathUtils.rotateAroundAxisX(v, i);
        else {
            MathUtils.rotateAroundAxisZ(v, i2 / 5);
            MathUtils.rotateAroundAxisX(v, i2 / 5);
        }
        Location loc = location.clone().subtract(v);
        int step = 0;
        for (int i = 0; i < particles; i++) {
            if (step >= amount)
                step = 0;
            step++;
            loc.add(v);
            if (dust) {
                UtilParticles.display(MathUtils.random(255), MathUtils.random(255), MathUtils.random(255), loc);
                continue;
            }
//            location.getWorld().spigot().playEffect(loc, Effect.POTION_SWIRL);
        }
    }

    @Override
    void onLeftClick() {
    }
}
