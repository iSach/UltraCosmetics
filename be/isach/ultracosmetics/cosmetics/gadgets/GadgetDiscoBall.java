package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.ItemFactory;
import com.xxmicloxx.NoteBlockAPI.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.Song;
import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
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
        super(Material.BEACON, (byte) 0x0, "DiscoBall", "ultracosmetics.gadgets.discoball", 60, owner, GadgetType.DISCOBALL);
    }

    @Override
    public void clear() {
        try {
            running = false;
            armorStand.getWorld().spigot().playEffect(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d), Effect.STEP_SOUND, Material.STAINED_CLAY.getId(), 4, 0, 0, 0, 1, 200, 32);
            armorStand.remove();
            armorStand = null;
            i = 0;
            i2 = 0;
            Core.discoBalls.remove(this);
            if (Core.isNoteBlockAPIEnabled())
                positionSongPlayer.setPlaying(false);
        } catch (Exception exc) {
        }
        HandlerList.unregisterAll(this);
    }

    @Override
    void onInteractRightClick() {
        armorStand = (ArmorStand) getPlayer().getWorld().spawnEntity(getPlayer().getLocation().add(0, 3, 0), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setSmall(false);
        armorStand.setHelmet(ItemFactory.create(Material.STAINED_GLASS, (byte) r.nextInt(15), " "));
        running = true;
        Core.discoBalls.add(this);
        if (Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
            File[] files = new File(Core.getPlugin().getDataFolder().getPath() + "/songs/").listFiles();
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
            Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new BukkitRunnable() {
                @Override
                public void run() {
                    positionSongPlayer.setPlaying(false);
                }
            }, 20 * 20);
        }
        Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new BukkitRunnable() {
            @Override
            public void run() {
                clear();
            }
        }, 20 * 20);
    }

    @Override
    void onUpdate() {
        if (running) {
            armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0.2, 0));
            armorStand.setHelmet(ItemFactory.create(Material.STAINED_GLASS, (byte) r.nextInt(15), " "));
            armorStand.getWorld().spigot().playEffect(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5), Effect.SPELL, 0, 0, 0, 0, 0, 1f, 1, 64);
            armorStand.getWorld().spigot().playEffect(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5), Effect.INSTANT_SPELL, 0, 0, 0, 0, 0, 1f, 1, 64);
            armorStand.getWorld().spigot().playEffect(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5), Effect.NOTE, r.nextInt(15), r.nextInt(15), 4, 3, 4, 1f, 1, 64);
            double angle, angle2, x, x2, z, z2;
            angle = 2 * Math.PI * i / 100;
            x = Math.cos(angle) * 4;
            z = Math.sin(angle) * 4;
            drawParticleLine(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5).clone().add(x, 0, z), armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5), Effect.POTION_SWIRL, 20);
            i += 6;
            angle2 = 2 * Math.PI * i2 / 100;
            x2 = Math.cos(angle2) * 4;
            z2 = Math.sin(angle2) * 4;
            drawParticleLine(armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5), armorStand.getEyeLocation().add(-.5d, -.5d, -.5d).clone().add(0.5, 0.5, 0.5).add(x2, 0, z2), Effect.COLOURED_DUST, 50);
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

    public void drawParticleLine(Location a, Location b, Effect effect, int particles) {
        Location location = a.clone();
        Location target = b.clone();
        double amount = particles;
        Vector link = target.toVector().subtract(location.toVector());
        float length = (float) link.length();
        link.normalize();

        float ratio = length / particles;
        Vector v = link.multiply(ratio);
        if (effect == Effect.POTION_SWIRL)
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
            if (effect == Effect.COLOURED_DUST) {
                location.getWorld().spigot().playEffect(loc, effect, 0, 0, 0, 0, 0, 0, 1, 32);
                continue;
            }
            location.getWorld().spigot().playEffect(loc, effect);
        }
    }

    @Override
    void onInteractLeftClick() {
    }
}
