package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

/**
 * Created by sacha on 10/08/15.
 */
public class MountGlacialSteed extends Mount {
    public MountGlacialSteed(UUID owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.GLACIALSTEED, ultraCosmetics);
        if (entity instanceof Horse) {
            UltraCosmetics.getInstance().registerListener(this);
            Horse horse = (Horse) entity;

            horse.setColor(Horse.Color.WHITE);
            horse.setVariant(Horse.Variant.HORSE);
            color = Horse.Color.WHITE;
            variant = Horse.Variant.HORSE;
            horse.setJumpStrength(0.7);
            UltraCosmetics.getInstance().getEntityUtil().setHorseSpeed(horse, 0.4d);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer() == getPlayer()
                && UltraCosmetics.getCustomPlayer(getPlayer()).currentMount == this
                && (boolean) SettingsManager.getConfig().get("Mounts-Block-Trails")) {
            for (Block b : BlockUtils.getBlocksInRadius(event.getPlayer().getLocation(), 3, false))
                if (b.getLocation().getBlockY() == event.getPlayer().getLocation().getBlockY() - 1)
                    BlockUtils.setToRestore(b, Material.SNOW_BLOCK, (byte) 0x0, 20);
        }
    }

    @Override
    protected void onUpdate() {
        UtilParticles.display(Particles.SNOW_SHOVEL, 0.4f, 0.2f, 0.4f, entity.getLocation().clone().add(0, 1, 0), 5);
    }
}
