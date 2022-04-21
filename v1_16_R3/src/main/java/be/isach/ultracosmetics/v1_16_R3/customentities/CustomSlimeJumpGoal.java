package be.isach.ultracosmetics.v1_16_R3.customentities;

import net.minecraft.server.v1_16_R3.PathfinderGoal;

import java.util.EnumSet;

public class CustomSlimeJumpGoal extends PathfinderGoal {
    public CustomSlimeJumpGoal() {
        a(EnumSet.of(Type.MOVE));
    }

    @Override
    public boolean a() {
        return false;
    }
}
