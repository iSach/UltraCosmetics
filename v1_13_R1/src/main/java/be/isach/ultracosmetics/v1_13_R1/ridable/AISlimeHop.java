package be.isach.ultracosmetics.v1_13_R1.ridable;

import be.isach.ultracosmetics.v1_13_R1.customentities.CustomSlime;
import net.minecraft.server.v1_13_R1.PathfinderGoal;

/**
 * @author BillyGalbreath
 * <p>
 * Author of plugin: "Ridables"
 * Thanks for authorizing using Ridables code to make UC work!
 */
public class AISlimeHop extends PathfinderGoal {
    private final CustomSlime slime;

    public AISlimeHop(CustomSlime slime) {
        this.slime = slime;
        a(5); // setMutexBits
    }

    // shouldExecute
    @Override
    public boolean a() {
        if (slime.getRider() != null) {
            return false;
        }
        return false/*slime.canWander() && new SlimeWanderEvent((Slime) slime.getBukkitEntity()).callEvent()*/;
    }

    // shouldContinueExecuting
    @Override
    public boolean b() {
        return a();
    }

    // tick
    @Override
    public void e() {
        ((CustomSlime.SlimeWASDController) slime.getControllerMove()).setSpeed(1.0D);
    }
}
