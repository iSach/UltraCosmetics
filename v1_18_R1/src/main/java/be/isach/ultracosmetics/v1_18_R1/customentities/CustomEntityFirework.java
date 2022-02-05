package be.isach.ultracosmetics.v1_18_R1.customentities;

import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;

/**
 * @author RadBuilder
 */
public class CustomEntityFirework extends FireworkRocketEntity {
    private Player[] players = null;
    private boolean gone = false;

    public CustomEntityFirework(Level world, Player... p) {
        super(EntityType.FIREWORK_ROCKET, world);
        players = p;
        // this doesn't seem right but it's the same method used in v1_16_R3
        this.newFloatList(0.25F, 0.25F);
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
