package me.isach.ultracosmetics.cosmetics.mounts;

import com.xxmicloxx.NoteBlockAPI.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.Song;
import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.Navigation;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature;
import org.bukkit.entity.EntityType;
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
        super(EntityType.SHEEP, Material.STAINED_GLASS, (byte) new Random().nextInt(15), "NyanSheep", "ultracosmetics.mounts.nyansheep", owner, MountType.NYANSHEEP);

        if (owner == null) return;
        ((LivingEntity) ent).setNoDamageTicks(Integer.MAX_VALUE);
        if (Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
            Song s = NBSDecoder.parse(new File(Core.getPlugin().getDataFolder(), "/songs/NyanCat.nbs"));
            final PositionSongPlayer positionSongPlayer = new PositionSongPlayer(s);
            positionSongPlayer.setTargetLocation(((LivingEntity) ent).getEyeLocation());
            positionSongPlayer.setPlaying(true);
            for (Player p : Bukkit.getOnlinePlayers())
                positionSongPlayer.addPlayer(p);
            positionSongPlayer.setAutoDestroy(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(ent.isValid()) {
                        if (Core.isNoteBlockAPIEnabled())
                            positionSongPlayer.setTargetLocation(((LivingEntity) ent).getEyeLocation());
                    } else {
                        positionSongPlayer.setPlaying(false);
                        cancel();
                    }
                }
            }.runTaskTimer(Core.getPlugin(), 0, 1);
        }
    }

    @Override
    public void clear() {
        getPlayer().sendMessage(MessageManager.getMessage("Mounts.Despawn").replace("%mountname%", getMenuName()));
        Core.getCustomPlayer(getPlayer()).currentMount = null;
        ent.remove();
    }

    @Override
    void onUpdate() {
        if (ent.getPassenger() == null)
            clear();
        move();



        ((Sheep) ent).setColor(DyeColor.values()[new Random().nextInt(15)]);

        List<RGBColor> colors = new ArrayList<>();

        colors.add(new RGBColor(255, -255, -255));
        colors.add(new RGBColor(255, 165, -255));
        colors.add(new RGBColor(255, 255, -255));
        colors.add(new RGBColor(154, 205, 50));
        colors.add(new RGBColor(30, 144, 255));
        colors.add(new RGBColor(148, -255, 211));

        float y = 1.2f;
        for (RGBColor rgbColor : colors) {
            for (int i = 0; i < 10; i++)
                UtilParticles.play(ent.getLocation().add(ent.getLocation().getDirection().normalize().multiply(-1).multiply(1.4)).add(0, y, 0), Effect.COLOURED_DUST, 0, 0, rgbColor.getRed() / 255, rgbColor.getGreen() / 255, rgbColor.getBlue() / 255, 1, 0);
            y -= 0.2;
        }

    }

    private void move() {
        Player player = getPlayer();
        double mult = 0.4D;
        Vector vel = player.getLocation().getDirection().setY(0).normalize().multiply(4);
        Location loc = player.getLocation().add(vel);
        EntityCreature ec = ((CraftCreature) ent).getHandle();
        Navigation nav = (Navigation) ec.getNavigation();
        nav.a(loc.getX(), loc.getY(), loc.getZ(), (1.0D + 2.0D * mult) * 1.0D);
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
