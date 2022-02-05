package be.isach.ultracosmetics.v1_15_R1.customentities;

import be.isach.ultracosmetics.v1_15_R1.EntityBase;
import net.minecraft.server.v1_15_R1.*;

/**
 * @author iSach
 */
public class RideableSpider extends EntitySpider implements EntityBase {

    public RideableSpider(EntityTypes<? extends EntitySpider> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public void e(Vec3D vec3D) {
        if (!CustomEntities.customEntities.contains(this)) {
            super.e(vec3D);
            return;
        }

        super.e(vec3D);

        EntityHuman passenger = null;
        if (!getPassengers().isEmpty()) {
            passenger = (EntityHuman) getPassengers().get(0);
        }
        CustomEntities.ride((float) vec3D.x, (float) vec3D.y, passenger, this);
    }

    @Override
    public void g_(float sideMot, float forMot) {
        super.e(new Vec3D(sideMot, 0, forMot));
    }

    @Override
    public String getName() {
        return LocaleLanguage.a().a("entity.minecraft.spider");
    }
}
