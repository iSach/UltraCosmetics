package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by sacha on 11/01/17.
 */
public abstract class MountHorse<E extends AbstractHorse> extends Mount<E> {

    public MountHorse(UltraPlayer ultraPlayer, MountType type, UltraCosmetics ultraCosmetics) {
        super(ultraPlayer, type, ultraCosmetics);
    }

    /**
     * Equips the pet.
     */
    @Override
    public void onEquip() {
        if (getOwner().getCurrentMount() != null) {
            getOwner().removeMount();
        }

        EntityType entityType = getType().getEntityType();
        if (getVariant() == Horse.Variant.DONKEY) {
            entityType = EntityType.DONKEY;
        } else if (getVariant() == Horse.Variant.SKELETON_HORSE) {
            entityType = EntityType.SKELETON_HORSE;
        } else if (getVariant() == Horse.Variant.MULE) {
            entityType = EntityType.MULE;
        } else if (getVariant() == Horse.Variant.UNDEAD_HORSE) {
            entityType = EntityType.ZOMBIE_HORSE;
        }

        EntitySpawningManager.setBypass(true);
        this.entity = (E) getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), entityType);
        EntitySpawningManager.setBypass(false);
        if (entity instanceof Ageable) {
            entity.setAdult();
        } else {
            if (entity instanceof Slime) {
                ((Slime) entity).setSize(4);
            }
        }
        entity.setCustomNameVisible(true);
        entity.setCustomName(getType().getName(getPlayer()));
        entity.setPassenger(getPlayer());
        entity.setTamed(true);
        entity.setDomestication(1);
        if (entity instanceof Horse){
            ((Horse)entity).getInventory().setSaddle(new ItemStack(Material.SADDLE));
            ((Horse)entity).setColor(getColor());
        }
        runTaskTimerAsynchronously(UltraCosmeticsData.get().getPlugin(), 0, getType().getRepeatDelay());
        entity.setMetadata("Mount", new FixedMetadataValue(UltraCosmeticsData.get().getPlugin(), "UltraCosmetics"));
        getOwner().setCurrentMount(this);
    }

    @Override
    public void onUpdate() {

    }

    abstract protected Horse.Variant getVariant();

    abstract protected Horse.Color getColor();
}
