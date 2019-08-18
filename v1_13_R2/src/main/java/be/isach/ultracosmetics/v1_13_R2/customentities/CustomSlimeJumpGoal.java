package be.isach.ultracosmetics.v1_13_R2.customentities;

import net.minecraft.server.v1_13_R2.PathfinderGoal;

public class CustomSlimeJumpGoal extends PathfinderGoal {
    private final CustomSlime slime;

    public CustomSlimeJumpGoal(CustomSlime slime) {
        this.slime = slime;
        a(5);
    }

    @Override
    public boolean a() {
        if (slime.passengers.size() > 0 && slime.passengers.get(0) != null) {
            return false;
        }
        return false;
    }

    @Override
    public boolean b() {
        return a();
    }
}
