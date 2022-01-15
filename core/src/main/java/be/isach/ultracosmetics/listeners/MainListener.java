package be.isach.ultracosmetics.listeners;

import be.isach.ultracosmetics.UltraCosmeticsData;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Main listener
 *
 * @author iSach
 * @since 12-25-2015
 */
public class MainListener implements Listener {

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().hasMetadata("NO_INTER"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onHopperPickup(InventoryPickupItemEvent event) {
        ItemStack stack = event.getItem().getItemStack();
        if (event.getItem().hasMetadata("UNPICKABLEUP")
                || (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()
                && stack.getItemMeta().getDisplayName().equals(UltraCosmeticsData.get().getItemNoPickupString()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        ItemStack stack = event.getItem().getItemStack();
        if (event.getItem().hasMetadata("UNPICKABLEUP")
                || (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()
                && stack.getItemMeta().getDisplayName().equals(UltraCosmeticsData.get().getItemNoPickupString()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSnowmanTrail(EntityBlockFormEvent event) {
        if (event.getEntity().getType() != EntityType.SNOWMAN) return;
        if (!event.getEntity().hasMetadata("Pet")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onWitherShoot(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Wither)) return;
        Wither wither = (Wither) event.getEntity().getShooter();
        if (wither.hasMetadata("Pet")) {
            event.setCancelled(true);
        }
    }
}
