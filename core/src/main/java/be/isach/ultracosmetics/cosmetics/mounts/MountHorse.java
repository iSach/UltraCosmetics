package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by sacha on 11/01/17.
 */
public abstract class MountHorse extends Mount<Horse> {

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

        EntitySpawningManager.setBypass(true);
        this.entity = (Horse) getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), entityType);
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
