package be.isach.ultracosmetics.v1_12_R1.customentities;

import be.isach.ultracosmetics.v1_12_R1.EntityBase;
import net.minecraft.server.v1_12_R1.*;

/**
 * @author RadBuilder
 */
public class RideableSpider extends EntitySpider implements EntityBase {

    public RideableSpider(World world) {
        super(world);
    }

    @Override
    public void a(float sideMot, float forMot, float f2) {
        if (!CustomEntities.customEntities.contains(this)) {
            super.g(sideMot, forMot);
            return;
        }

        EntityHuman passenger = null;
        if (!bF().isEmpty()) {
            passenger = (EntityHuman) bF().get(0);
        }
        CustomEntities.ride(sideMot, forMot, passenger, this);
    }

    @Override
    public void g_(float sideMot, float forMot) {
        super.g(sideMot, forMot);
    }

    @Override
    public String getName() {
        return LocaleI18n.get("entity.Spider.name");
    }
}
