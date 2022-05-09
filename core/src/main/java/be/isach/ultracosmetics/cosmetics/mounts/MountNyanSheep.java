package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Particles;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Sheep;
import org.bukkit.util.Vector;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance of a nyansheep mount.
 *
 * @author iSach
 * @since 08-17-2015
 */
public class MountNyanSheep extends Mount {
    private static final List<Color> COLORS = new ArrayList<>();

    static {
        COLORS.add(new Color(255, 0, 0));
        COLORS.add(new Color(255, 165, 0));
        COLORS.add(new Color(255, 255, 0));
        COLORS.add(new Color(154, 205, 50));
        COLORS.add(new Color(30, 144, 255));
        COLORS.add(new Color(148, 0, 211));
    }

    public MountNyanSheep(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.valueOf("nyansheep"), ultraCosmetics);
    }

    @Override
    public void setupEntity() {
        ((Sheep)entity).setNoDamageTicks(Integer.MAX_VALUE);
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearPathfinders(entity);
    }

    @Override
    public void onUpdate() {
        move();

        ((Sheep)entity).setColor(DyeColor.values()[RANDOM.nextInt(16)]);

        Location particleLoc = entity.getLocation().add(entity.getLocation().getDirection().normalize().multiply(-2)).add(0, 1.2, 0);
        for (Color rgbColor : COLORS) {
            for (int i = 0; i < 10; i++) {
                Particles.REDSTONE.display(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue(), particleLoc);
            }
            particleLoc.subtract(0, 0.2, 0);
        }
    }

    private void move() {
        Location playerLoc = getPlayer().getLocation();
        Vector vel = playerLoc.getDirection().setY(0).normalize().multiply(4);
        playerLoc.add(vel);

        UltraCosmeticsData.get().getVersionManager().getEntityUtil().move(((Sheep)entity), playerLoc);
    }
}
