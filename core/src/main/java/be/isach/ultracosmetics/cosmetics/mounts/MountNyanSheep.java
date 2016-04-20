package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.UtilParticles;
import com.xxmicloxx.NoteBlockAPI.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.Song;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 17/08/15.
 */
public class MountNyanSheep extends Mount {

    public MountNyanSheep(UUID owner) {
        super(owner, MountType.NYANSHEEP);
    }

    @Override
    protected void onEquip() {
        ((LivingEntity) entity).setNoDamageTicks(Integer.MAX_VALUE);
        UltraCosmetics.getInstance().getEntityUtil().clearPathfinders(entity);
        if (Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
            Song s = NBSDecoder.parse(new File(UltraCosmetics.getInstance().getDataFolder(), "/songs/NyanCat.nbs"));
            final PositionSongPlayer positionSongPlayer = new PositionSongPlayer(s);
            positionSongPlayer.setTargetLocation(((LivingEntity) entity).getEyeLocation());
            positionSongPlayer.setPlaying(true);
            for (Player p : Bukkit.getOnlinePlayers())
                positionSongPlayer.addPlayer(p);
            positionSongPlayer.setAutoDestroy(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity.isValid() && entity.getPassenger() == getPlayer()) {
                        if (UltraCosmetics.getInstance().isNoteBlockAPIEnabled())
                            positionSongPlayer.setTargetLocation(((LivingEntity) entity).getEyeLocation());
                    } else {
                        positionSongPlayer.setPlaying(false);
                        cancel();
                    }
                }
            }.runTaskTimer(UltraCosmetics.getInstance(), 0, 1);
        }
    }

    @Override
    protected void onUpdate() {
        move();

        ((Sheep) entity).setColor(DyeColor.values()[new Random().nextInt(15)]);

        List<RGBColor> colors = new ArrayList<>();

        colors.add(new RGBColor(255, 0, 0));
        colors.add(new RGBColor(255, 165, 0));
        colors.add(new RGBColor(255, 255, 0));
        colors.add(new RGBColor(154, 205, 50));
        colors.add(new RGBColor(30, 144, 255));
        colors.add(new RGBColor(148, 0, 211));

        float y = 1.2f;
        for (RGBColor rgbColor : colors) {
            for (int i = 0; i < 10; i++)
                UtilParticles.display(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue(),
                        entity.getLocation().add(entity.getLocation().getDirection()
                                .normalize().multiply(-1).multiply(1.4)).add(0, y, 0));
            y -= 0.2;
        }
    }

    private void move() {
        if (getPlayer() == null)
            return;
        try {
            Player player = getPlayer();
            Vector vel = player.getLocation().getDirection().setY(0).normalize().multiply(4);
            Location loc = player.getLocation().add(vel);

            UltraCosmetics.getInstance().getEntityUtil().move((Creature) entity, loc);
        } catch (Exception exc) {
            UltraCosmetics.getCustomPlayer(getPlayer()).removeMount();
        }
    }

    class RGBColor {

        int red;
        int green;
        int blue;

        public RGBColor(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public int getBlue() {
            return blue;
        }

        public int getGreen() {
            return green;
        }

        public int getRed() {
            return red;
        }
    }

}
