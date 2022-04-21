package be.isach.ultracosmetics.v1_18_R2.customentities;

import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal;

public class CustomSlimeJumpGoal extends Goal {
    public CustomSlimeJumpGoal() {
        ((Goal)this).setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // TODO: does this class even do anything?
        return false;
    }
}
