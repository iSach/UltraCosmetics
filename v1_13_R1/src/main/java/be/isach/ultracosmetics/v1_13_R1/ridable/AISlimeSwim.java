package be.isach.ultracosmetics.v1_13_R1.ridable;

import be.isach.ultracosmetics.v1_13_R1.customentities.CustomSlime;
import net.minecraft.server.v1_13_R1.PathfinderGoal;

/**
 * @author BillyGalbreath
 * <p>
 * Author of plugin: "Ridables"
 * Thanks for authorizing using Ridables code to make UC work!
 */
public class AISlimeSwim extends PathfinderGoal {
    private final CustomSlime slime;

    public AISlimeSwim(CustomSlime slime) {
        this.slime = slime;
        a(5); // setMutexBits
        slime.getNavigation().d(true);
    }

    // shouldExecute
    @Override
    public boolean a() {
        if (slime.isInWater() || slime.ax()) {
            //return slime.canWander() && new SlimeSwimEvent((Slime) slime.getBukkitEntity()).callEvent();
        }
        return false;
    }

    // shouldContinueExecuting
    @Override
    public boolean b() {
        return a();
    }

    // tick
    @Override
    public void e() {
        if (slime.getRandom().nextFloat() < 0.8F) {
            slime.getControllerJump().a();
        }
        ((CustomSlime.SlimeWASDController) slime.getControllerMove()).setSpeed(1.2D);
    }
}
