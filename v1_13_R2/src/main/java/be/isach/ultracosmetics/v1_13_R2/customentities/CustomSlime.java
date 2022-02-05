package be.isach.ultracosmetics.v1_13_R2.customentities;

import be.isach.ultracosmetics.v1_13_R2.EntityBase;
import net.minecraft.server.v1_13_R2.*;

/**
 * @author iSach
 */
public class CustomSlime extends EntitySlime implements EntityBase {

    public CustomSlime(EntityTypes<? extends EntitySlime> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void n() {
        this.goalSelector.a(5, new CustomSlimeJumpGoal(this));
    }

    @Override
    public void a(float sideMot, float forMot, float f2) {
        //public void e(Vec3D vec3D) {
        if (!CustomEntities.customEntities.contains(this)) {
            super.a(sideMot, forMot, f2);
            return;
        }
        EntityHuman passenger = null;
        if (!passengers.isEmpty()) {
            passenger = (EntityHuman) passengers.get(0);
        }
        CustomEntities.ride(sideMot, forMot, passenger, this);
    }

    @Override
    public void g_(float sideMot, float forMot) {
        super.a(sideMot, 0, forMot);
    }

    @Override
    public String getName() {
        return LocaleLanguage.a().a("entity.minecraft.slime");
    }
}