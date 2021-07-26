package be.isach.ultracosmetics.v1_17_R1.customentities;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;

/**
 * @author RadBuilder
 */
public class CustomEntityFirework extends FireworkRocketEntity {
    Player[] players = null;
    boolean gone = false;

    public CustomEntityFirework(Level world, Player... p) {
        super(EntityType.FIREWORK_ROCKET, world);
        players = p;
        // this doesn't seem right but it's the same method used in v1_16_R3
        this.newFloatList(0.25F, 0.25F);
    }

    public static void spawn(Location location, FireworkEffect effect, Player... players) {
        try {
            CustomEntityFirework firework = new CustomEntityFirework(((CraftWorld) location.getWorld()).getHandle(), players);
            FireworkMeta meta = ((Firework) firework.getBukkitEntity()).getFireworkMeta();
            meta.addEffect(effect);
            ((Firework) firework.getBukkitEntity()).setFireworkMeta(meta);
            ((Entity)firework).setPos(location.getX(), location.getY(), location.getZ());

            if ((((CraftWorld) location.getWorld()).getHandle()).addFreshEntity(firework)) {
                ((Entity)firework).setInvisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tick() {
        if (gone) {
            return;
        }

        if (!this.level.isClientSide) {
            gone = true;

            if (players != null)
                if (players.length > 0)
                    for (Player player : players)
                        (((CraftPlayer) player).getHandle()).connection.send(new ClientboundEntityEventPacket(this, (byte) 17));
                else
                    level.broadcastEntityEvent(this, (byte) 17);
            ((Entity)this).discard();
        }
    }
}
