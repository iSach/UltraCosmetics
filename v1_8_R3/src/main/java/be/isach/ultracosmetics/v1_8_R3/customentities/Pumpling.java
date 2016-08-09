package be.isach.ultracosmetics.v1_8_R3.customentities;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.pets.IPetCustomEntity;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import be.isach.ultracosmetics.v1_8_R3.pets.PlayerFollower;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sacha on 18/10/15.
 */
public class Pumpling extends EntityZombie implements IPetCustomEntity {

    Player player;

    public Pumpling(World world, Player player) {
        super(world);

        this.player = player;
    }

    public org.bukkit.entity.Entity getEntity() {
        return getBukkitEntity();
    }

    @Override
    protected String z() { // say
        if (isCustomEntity()) {
            makeSound("mob.ghast.scream", 0.05f, 2f);
            return null;
        } else
            super.z();
        return "mob.zombie.say";
    }

    @Override
    protected String bo() { // Hurt
        if (isCustomEntity())
            return null;
        else
            super.z();
        return "mob.zombie.hurt";
    }

    @Override
    protected String bp() { // Death
        if (isCustomEntity())
            return null;
        else
            super.z();
        return "mob.zombie.death";
    }

    @Override
    protected void a(BlockPosition blockposition, Block block) {
        if (isCustomEntity()) {
            return;
        } else {
            super.a(blockposition, block);
        }
    }

    @Override
    public void m() {
        super.m();
        if (isCustomEntity()) {
            fireTicks = 0;
            UtilParticles.display(Particles.FLAME, 0.2f, 0.2f, 0.2f, ((Zombie) getBukkitEntity()).getEyeLocation(), 3);
            UltraCosmeticsData.get().getVersionManager().getPathfinderUtil().removePathFinders(getBukkitEntity());
            setInvisible(true);
            setBaby(true);
            setEquipment(4, new ItemStack(Blocks.PUMPKIN));
            follow();
        }
    }

    private void follow() {
        Location petLoc = getBukkitEntity().getLocation();
        Location loc = player.getLocation();
        if (!petLoc.getWorld().equals(loc.getWorld())
                || (petLoc.distance(loc) > 10 && valid && player.isOnGround())) {
            getBukkitEntity().teleport(player);
            return;
        }
        if (petLoc.distance(loc) > 3.3d) {
            PathEntity pathEntity = this.navigation.a(loc.getX(), loc.getY(), loc.getZ());
            this.navigation.a(pathEntity, 1f);
        }
    }

    private boolean isCustomEntity() {
        return CustomEntities.customEntities.contains(this);
    }
}
