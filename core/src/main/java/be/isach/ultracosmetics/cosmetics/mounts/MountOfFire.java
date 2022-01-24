package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import be.isach.ultracosmetics.version.VersionManager;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents an instance of a mount of fire mount.
 *
 * @author iSach
 * @since 08-10-2015
 */
public class MountOfFire extends MountHorse {

    public MountOfFire(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, MountType.valueOf("mountoffire"), ultraCosmetics);
    }

    @Override
    public void onEquip() {
        super.onEquip();
        entity.setJumpStrength(0.7);
        UltraCosmeticsData.get().getVersionManager().getEntityUtil().setHorseSpeed(entity, 0.4d);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer() == getPlayer()
                && getOwner().getCurrentMount() == this
                && (boolean) SettingsManager.getConfig().get("Mounts-Block-Trails")) {
            List<Byte> datas = new ArrayList<>();
            datas.add((byte) 0x1);
            datas.add((byte) 0x4);
            datas.add((byte) 0xe);
            if (VersionManager.IS_VERSION_1_13) {
                BlockUtils.getBlocksInRadius(event.getPlayer().getLocation(), 3, false).stream().filter(b -> b.getLocation().getBlockY() == event.getPlayer().getLocation().getBlockY() - 1).forEachOrdered(b -> BlockUtils.setToRestore(b, BlockUtils.getBlockByColor("STAINED_CLAY", datas.get(new Random().nextInt(2))), datas.get(new Random().nextInt(2)), 20));
            } else {
                BlockUtils.getBlocksInRadius(event.getPlayer().getLocation(), 3, false).stream().filter(b -> b.getLocation().getBlockY() == event.getPlayer().getLocation().getBlockY() - 1).forEachOrdered(b -> BlockUtils.setToRestore(b, Material.valueOf("STAINED_CLAY"), datas.get(new Random().nextInt(2)), 20));
            }
        }
    }

    @Override
    public void onUpdate() {
        UtilParticles.display(Particles.FLAME, 0.4f, 0.2f, 0.4f, entity.getLocation().clone().add(0, 1, 0), 5);
    }

    @Override
    protected Horse.Color getColor() {
        return Horse.Color.CREAMY;
    }
}
