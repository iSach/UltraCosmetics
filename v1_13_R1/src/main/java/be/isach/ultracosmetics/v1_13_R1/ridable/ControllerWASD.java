package be.isach.ultracosmetics.v1_13_R1.ridable;

import net.minecraft.server.v1_13_R1.AttributeRanged;
import net.minecraft.server.v1_13_R1.ControllerMove;
import net.minecraft.server.v1_13_R1.*;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;

/**
 * @author BillyGalbreath
 *
 * Author of plugin: "Ridables"
 * Thanks for authorizing using Ridables code to make UC work!
 */
public class ControllerWASD extends ControllerMove {

    public static final IAttribute RIDING_SPEED = (new AttributeRanged(null, "generic.rideSpeed", 1.0D, 0.0D, 1024.0D)).a("Ride Speed").a(true);
    public static final IAttribute RIDING_MAX_Y = (new AttributeRanged(null, "generic.rideMaxY", 256.0D, 0.0D, 1024.0D)).a("Ride Max Y").a(true);

    protected final RidableEntity ridable;
    public EntityPlayer rider;
    public boolean override;

    public ControllerWASD(RidableEntity ridable) {
        super((EntityInsentient) ridable);
        this.ridable = ridable;
    }

    private EntityPlayer updateRider() {
        if (a.passengers.isEmpty()) {
            return rider = null;
        }
        Entity entity = a.passengers.get(0);
        return rider = entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
    }

    // isUpdating
    @Override
    public boolean b() {
        return rider != null || super.b();
    }

    // tick
    @Override
    public void a() {
        if (updateRider() != null && !override) {
            tick(rider);
        } else {
            tick();
        }
    }

    public void tick() {
        super_tick();
    }

    public void super_tick() {
        super.a();
    }

    public void tick(EntityPlayer rider) {
        float forward = rider.bj * 0.5F;
        float strafe = rider.bh * 0.25F;
        if (forward <= 0.0F) {
            forward *= 0.5F;
        }

        float yaw = rider.yaw;
        if (strafe != 0) {
            if (forward == 0) {
                yaw += strafe > 0 ? -90 : 90;
                forward = Math.abs(strafe * 2);
            } else {
                yaw += strafe > 0 ? -30 : 30;
                strafe /= 2;
                if (forward < 0) {
                    yaw += strafe > 0 ? -110 : 110;
                    forward *= -1;
                }
            }
        } else if (forward < 0) {
            yaw -= 180;
            forward *= -1;
        }
        ((LookController) a.getControllerLook()).setOffsets(yaw - rider.yaw, 0);

        if (isJumping(rider)) {
            RidableSpacebarEvent event = new RidableSpacebarEvent(ridable);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled() && !event.isHandled() && !ridable.onSpacebar() && a.onGround) {
                a.getControllerJump().a();
            }
        }

        e = ((EntityInsentient) ridable).getAttributeInstance(RIDING_SPEED).getValue();

        a.o((float) e); // speed
        a.r(forward);

        f = a.bj; // forward
        g = a.bh; // strafe
    }

    private static Field jumping;

    static {
        try {
            jumping = EntityLiving.class.getDeclaredField("bg");
            jumping.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if entity has their jump flag toggled on
     * <p>
     * This is true for players when they are pressing the spacebar
     *
     * @param entity Living entity to check
     * @return True if jump flag is toggled on
     */
    public static boolean isJumping(EntityLiving entity) {
        try {
            return jumping.getBoolean(entity);
        } catch (IllegalAccessException ignore) {
            return false;
        }
    }
}
