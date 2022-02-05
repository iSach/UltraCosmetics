package be.isach.ultracosmetics.v1_17_R1.customentities;

import be.isach.ultracosmetics.v1_17_R1.EntityBase;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * @author iSach
 */
public class CustomSlime extends Slime implements EntityBase {

    public CustomSlime(EntityType<? extends Slime> entitytypes, Level world) {
        super(entitytypes, world);
    }

    @Override
    public void travel(Vec3 vec3D) {
        if (!CustomEntities.customEntities.contains(this)) {
            super.tickHeadTurn((float) vec3D.x, (float) vec3D.y);
            return;
        }
        Player passenger = null;
        if (!getPassengers().isEmpty()) {
            passenger = (Player) getPassengers().get(0);
        }
        CustomEntities.ride((float) vec3D.x, (float) vec3D.y, passenger, this);
    }

    @Override
    public Component getName() {
        return new TextComponent(Language.getInstance().getOrDefault("entity.minecraft.slime"));
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(5, new CustomSlimeJumpGoal(this));
    }

    @Override
    public void g_(float sideMot, float forMot) {
        super.travel(new Vec3(sideMot, 0, forMot));
    }
}
