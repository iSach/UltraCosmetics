package be.isach.ultracosmetics.v1_14_R1.customentities;

import be.isach.ultracosmetics.v1_14_R1.EntityBase;
import net.minecraft.server.v1_14_R1.*;

/**
 * @author iSach
 */
public class CustomSlime extends EntitySlime implements EntityBase {

    public CustomSlime(EntityTypes<? extends EntitySlime> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public void e(Vec3D vec3D) {
        if (!CustomEntities.customEntities.contains(this)) {
            super.e((float) vec3D.x, (float) vec3D.y);
            return;
        }
        EntityHuman passenger = null;
        if (!getPassengers().isEmpty()) {
            passenger = (EntityHuman) getPassengers().get(0);
        }
        CustomEntities.ride((float) vec3D.x, (float) vec3D.y, passenger, this);
    }

    @Override
    public String getName() {
        return LocaleLanguage.a().a("entity.minecraft.slime");
    }

    @Override
    protected void initPathfinder() {
        goalSelector.a(5, new CustomSlimeJumpGoal(this));
    }

    @Override
    public void g_(float sideMot, float forMot) {
        super.e(new Vec3D(sideMot, 0, forMot));
    }
}
