package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 10/08/15.
 */
public class MountOfWater extends Mount {

    public MountOfWater(UUID owner) {
        super(owner, MountType.MOUNTOFWATER);
    }

    @Override
    protected void onEquip() {
        UltraCosmetics.getInstance().registerListener(this);
        Horse horse = (Horse) entity;
        horse.setColor(Horse.Color.BLACK);
        horse.setVariant(Horse.Variant.HORSE);
        color = Horse.Color.BLACK;
        variant = Horse.Variant.HORSE;
        horse.setJumpStrength(0.7);
        UltraCosmetics.getInstance().getEntityUtil().setHorseSpeed(horse, 0.4d);

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer() == getPlayer()
                && UltraCosmetics.getCustomPlayer(getPlayer()).currentMount == this
                && (boolean) SettingsManager.getConfig().get("Mounts-Block-Trails")) {
            List<Byte> datas = new ArrayList<>();
            datas.add((byte) 0x3);
            datas.add((byte) 0x9);
            datas.add((byte) 0xb);
            for (Block b : BlockUtils.getBlocksInRadius(event.getPlayer().getLocation(), 3, false))
                if (b.getLocation().getBlockY() == event.getPlayer().getLocation().getBlockY() - 1)
                    BlockUtils.setToRestore(b, Material.STAINED_CLAY, datas.get(new Random().nextInt(2)), 20);
        }
    }

    @Override
    void onUpdate() {
        UtilParticles.display(Particles.DRIP_WATER, 0.4f, 0.2f, 0.4f, entity.getLocation().clone().add(0, 1, 0), 5);
    }
}
