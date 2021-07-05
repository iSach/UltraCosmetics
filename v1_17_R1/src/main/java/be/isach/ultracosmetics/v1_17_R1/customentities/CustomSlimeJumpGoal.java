package be.isach.ultracosmetics.v1_17_R1.customentities;

import net.minecraft.server.v1_16_R3.PathfinderGoal;

import java.util.EnumSet;

public class CustomSlimeJumpGoal extends PathfinderGoal {
    private final CustomSlime slime;

    public CustomSlimeJumpGoal(CustomSlime slime) {
        this.slime = slime;
        a(EnumSet.of(Type.MOVE));
    }

    @Override
    public boolean a() {
        if (slime.getPassengers().size() > 0 && slime.getPassengers().get(0) != null) {
            return false;
        }
        return false;
    }

    @Override
    public boolean b() {
        return a();
    }

}
