package be.isach.ultracosmetics.v1_13_R2.ridable;

import be.isach.ultracosmetics.v1_13_R2.customentities.CustomSlime;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.PathfinderGoal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
/**
 * @author BillyGalbreath
 *
 * Author of plugin: "Ridables"
 * Thanks for authorizing using Ridables code to make UC work!
 */
public class AISlimeAttack extends PathfinderGoal {
    private final CustomSlime slime;
    private int timer;

    public AISlimeAttack(CustomSlime slime) {
        this.slime = slime;
        a(2); // setMutexBits
    }

    // shouldExecute
    @Override
    public boolean a() {
        if (slime.getRider() != null) {
            return false;
        }
        EntityLiving target = slime.getGoalTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (target instanceof EntityHuman && ((EntityHuman) target).abilities.isInvulnerable) {
            return false;
        }
        return false;
       // TODO return slime.canWander() && new SlimeTargetLivingEntityEvent((Slime) slime.getBukkitEntity(), (LivingEntity) target.getBukkitEntity()).callEvent();
    }

    // shouldContinueExecuting
    @Override
    public boolean b() {
        if (slime.getRider() != null) {
            return false;
        }
        EntityLiving target = slime.getGoalTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (target instanceof EntityHuman && ((EntityHuman) target).abilities.isInvulnerable) {
            return false;
        }
        if (--timer <= 0) {
            return false;
        }
        return false;
        //return slime.canWander() && new SlimeTargetLivingEntityEvent((Slime) slime.getBukkitEntity(), (LivingEntity) target.getBukkitEntity()).callEvent();
    }

    // startExecuting
    @Override
    public void c() {
        timer = 300;
        super.c();
    }

    // resetTask
    @Override
    public void d() {
        timer = 0;
        slime.setGoalTarget(null);
    }

    // tick
    @Override
    public void e() {
        slime.a(slime.getGoalTarget(), 10.0F, 10.0F);
        ((CustomSlime.SlimeWASDController) slime.getControllerMove()).setDirection(slime.yaw, false);
    }
}
