package be.isach.ultracosmetics.v1_14_R1.customentities;

import net.minecraft.server.v1_14_R1.PathfinderGoal;

import java.util.EnumSet;

/**
 * @author BillyGalbreath
 *
 * Author of plugin: "Ridables"
 * Thanks for authorizing using Ridables code!
 */
public class AISlimeHop extends PathfinderGoal {
    private final CustomSlime slime;

    public AISlimeHop(CustomSlime slime) {
        this.slime = slime;
       // a(5); // setMutexBits
        a(EnumSet.of(Type.MOVE));
    }

    // shouldExecute
    @Override
    public boolean a() {
        if (slime.getPassengers().size() > 0 && slime.getPassengers().get(0) != null) {
            return false;
        }
        return false/*slime.canWander() && new SlimeWanderEvent((Slime) slime.getBukkitEntity()).callEvent()*/;
    }

    // shouldContinueExecuting
    @Override
    public boolean b() {
        return a();
    }

    /*// tick
    @Override
    public void e() {
        (slime.getControllerMove()).setSpeed(1.0D);
    }*/
}
