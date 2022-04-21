package be.isach.ultracosmetics.v1_8_R3.customentities;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.util.Particles;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PathEntity;
import net.minecraft.server.v1_8_R3.World;

/**
 * Created by Sacha on 18/10/15.
 */
public class Pumpling extends EntityZombie {

    Player player;

    public Pumpling(World world, Player player) {
        super(world);

        this.player = player;
    }

    public Pumpling(World world) {
        super(world);
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
        } else {
            super.a(blockposition, block);
        }
    }

    @Override
    public void m() {
        super.m();
        if (isCustomEntity()) {
            fireTicks = 0;
            Particles.FLAME.display(0.2f, 0.2f, 0.2f, ((Zombie) getBukkitEntity()).getEyeLocation(), 3);
            UltraCosmeticsData.get().getVersionManager().getEntityUtil().clearPathfinders(getBukkitEntity());
            setInvisible(true);
            setBaby(true);
            setEquipment(4, new ItemStack(Blocks.PUMPKIN));
            follow();
        }
    }

    private void follow() {
        Location petLoc = getBukkitEntity().getLocation();
        Location loc = player.getLocation();
        double distanceSquared = petLoc.distanceSquared(loc);
        @SuppressWarnings("deprecation")
        boolean onGround = player.isOnGround();
        if (!petLoc.getWorld().equals(loc.getWorld())
                || (distanceSquared > 10 * 10 && valid && onGround)) {
            getBukkitEntity().teleport(player);
            return;
        }
        if (distanceSquared > 3.3 * 3.3) {
            PathEntity pathEntity = this.navigation.a(loc.getX(), loc.getY(), loc.getZ());
            this.navigation.a(pathEntity, 1f);
        }
    }

    private boolean isCustomEntity() {
        return CustomEntities.isCustomEntity(this);
    }
}
