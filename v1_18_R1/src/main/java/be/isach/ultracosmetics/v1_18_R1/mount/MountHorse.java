package be.isach.ultracosmetics.v1_18_R1.mount;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * @author RadBuilder
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
        }
        entity.setCustomNameVisible(true);
        entity.setCustomName(getType().getName(getPlayer()));
        entity.addPassenger(getPlayer());
        entity.setTamed(true);
        entity.setDomestication(1);
        if (entity instanceof Horse) {
            ((Horse) entity).getInventory().setSaddle(new ItemStack(Material.SADDLE));
            ((Horse) entity).setColor(getColor());
        }
        runTaskTimer(UltraCosmeticsData.get().getPlugin(), 0, getType().getRepeatDelay());
        entity.setMetadata("Mount", new FixedMetadataValue(UltraCosmeticsData.get().getPlugin(), "UltraCosmetics"));

        getOwner().setCurrentMount(this);
    }

    @Override
    public void onUpdate() {

    }

    abstract protected Horse.Variant getVariant();

    abstract protected Horse.Color getColor();
}
