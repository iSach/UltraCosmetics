package be.isach.ultracosmetics.v1_9_R2;

import be.isach.ultracosmetics.v1_9_R2.nms.WrapperEntityHuman;
import be.isach.ultracosmetics.v1_9_R2.nms.WrapperEntityInsentient;
import net.minecraft.server.v1_9_R2.EntityHuman;
import net.minecraft.server.v1_9_R2.EntityInsentient;
import net.minecraft.server.v1_9_R2.MathHelper;

/**
 * By MinusKube, THANKS!
 */
public interface EntityBase {

    void g_(float sideMot, float forMot);

    float getSpeed();
    boolean canFly();

}
