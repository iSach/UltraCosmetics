package be.isach.ultracosmetics.v1_16_R3.customentities;

import be.isach.ultracosmetics.v1_16_R3.EntityBase;
import net.minecraft.server.v1_16_R3.*;

/**
 * @author iSach
 */
public class RideableSpider extends EntitySpider implements EntityBase {

    public RideableSpider(EntityTypes<? extends EntitySpider> entitytypes, World world) {
        super(entitytypes, world);
    }

    // Corresponds to travel(Vec3D)
    @Override
    public void g(Vec3D vec3D) {
        if (!CustomEntities.isCustomEntity(this)) {
            // Corresponds to travel(Vec3D)
            super.g(vec3D);
            return;
        }

        super.a(vec3D);

        EntityHuman passenger = null;
        if (!getPassengers().isEmpty()) {
            passenger = (EntityHuman) getPassengers().get(0);
        }
        CustomEntities.ride((float) vec3D.x, (float) vec3D.y, passenger, this);
    }

    @Override
    public void g_(float sideMot, float forMot) {
        super.g(new Vec3D(sideMot, 0, forMot));
    }

    @Override
    public String getName() {
        return LocaleLanguage.a().a("entity.minecraft.spider");
    }
}
