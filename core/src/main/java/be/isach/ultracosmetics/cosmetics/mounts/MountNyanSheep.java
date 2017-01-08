package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by sacha on 17/08/15.
 */
public class MountNyanSheep extends Mount {

    public MountNyanSheep(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.NYANSHEEP, ultraCosmetics);
    }

    @Override
    public void onEquip() {
        super.onEquip();
        ((LivingEntity) entity).setNoDamageTicks(Integer.MAX_VALUE);
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearPathfinders(entity);
    }

    @Override
    public void onUpdate() {
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

            UltraCosmeticsData.get().getVersionManager().getEntityUtil().move((Creature) entity, loc);
        } catch (Exception exc) {
            getOwner().removeMount();
        }
    }

    private class RGBColor {

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