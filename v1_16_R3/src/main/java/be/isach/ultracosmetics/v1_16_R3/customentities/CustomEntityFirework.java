package be.isach.ultracosmetics.v1_16_R3.customentities;

import net.minecraft.server.v1_16_R3.EntityFireworks;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @author RadBuilder
 */
public class CustomEntityFirework extends EntityFireworks {
    Player[] players = null;
    boolean gone = false;

    public CustomEntityFirework(World world, Player... p) {
        super(EntityTypes.FIREWORK_ROCKET, world);
        players = p;
        this.a(0.25F, 0.25F);
    }

    @Override
    public void tick() {
        if (gone) {
            return;
        }

        if (!this.world.isClientSide) {
            gone = true;

            if (players != null)
                if (players.length > 0)
                    for (Player player : players)
                        (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(new PacketPlayOutEntityStatus(this, (byte) 17));
                else
                    world.broadcastEntityEffect(this, (byte) 17);
            this.die();
        }
    }
}
