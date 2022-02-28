package be.isach.ultracosmetics.v1_18_R2.customentities;

import java.util.EnumSet;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;

public class CustomSlimeJumpGoal extends Goal {
    //private final CustomSlime slime;
	// there's no real reason it needs to be referenced as a custom slime internally
	// so it's Entity type for lest casting
	private final Entity slime;

    public CustomSlimeJumpGoal(CustomSlime slime) {
        this.slime = slime;
        ((Goal)this).setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (slime.getPassengers().size() > 0 && slime.getPassengers().get(0) != null) {
            return false;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

}
