package be.isach.ultracosmetics.v1_13_R2.ridable;

import be.isach.ultracosmetics.v1_13_R2.customentities.CustomSlime;
import net.minecraft.server.v1_13_R2.MobEffects;
import net.minecraft.server.v1_13_R2.PathfinderGoal;
import org.bukkit.entity.Slime;
/**
 * @author BillyGalbreath
 *
 * Author of plugin: "Ridables"
 * Thanks for authorizing using Ridables code to make UC work!
 */
public class AISlimeFaceRandom extends PathfinderGoal {
    private final CustomSlime slime;
    private float chosenYaw;
    private int timer;

    public AISlimeFaceRandom(CustomSlime slime) {
        this.slime = slime;
        a(2); // setMutexBits
    }

    // shouldExecute
    @Override
    public boolean a() {
        if (slime.getRider() != null) {
            return false;
        }
        /*if (!slime.canWander()) {
            return false;
        }*/
        return slime.getGoalTarget() == null && (slime.onGround || slime.isInWater() || slime.ax() || slime.hasEffect(MobEffects.LEVITATION));
    }

    // shouldContinueExecuting
    @Override
    public boolean b() {
        return a();
    }

    // tick
    @Override
    public void e() {
        if (--timer <= 0) {
            timer = 40 + slime.getRandom().nextInt(60);
            /*if (!slime.canWander()) {
                return;
            }
            SlimeChangeDirectionEvent event = new SlimeChangeDirectionEvent((Slime) slime.getBukkitEntity(), slime.getRandom().nextInt(360));
            if (!event.callEvent()) {
                return;
            }
            chosenYaw = event.getNewYaw();*/
        }
        ((CustomSlime.SlimeWASDController) slime.getControllerMove()).setDirection(chosenYaw, false);
    }
}
