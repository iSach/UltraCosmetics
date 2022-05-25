package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.PlayerAffectingCosmetic;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.listeners.HammerPickupListener;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.ServerVersion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XMaterial;

/**
 * Represents an instance of a thor hammer gadget summoned by a player.
 *
 * @author iSach
 * @since 08-08-2015
 */
public class GadgetThorHammer extends Gadget implements PlayerAffectingCosmetic {
    // EntityPickupItemEvent didn't exist until 1.12
    private static final boolean USE_OTHER_LISTENER = UltraCosmeticsData.get().getServerVersion().isAtLeast(ServerVersion.v1_12_R1);
    private Item hammer = null;
    private HammerPickupListener listener;
    private Vector v;

    public GadgetThorHammer(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("thorhammer"), ultraCosmetics);
    }

    @Override
    void onRightClick() {
        // I think this can only happen if a player is bypassing cooldowns 
        if (hammer != null) {
            hammer.remove();
        }
        Vector velocity = getPlayer().getEyeLocation().getDirection().multiply(1.4);
        hammer = ItemFactory.spawnUnpickableItem(ItemFactory.create(XMaterial.IRON_AXE, MessageManager.getMessage("Gadgets.ThorHammer.name")), getPlayer().getEyeLocation(), velocity);
        getPlayer().getInventory().setItem(SettingsManager.getConfig().getInt("Gadget-Slot"), null);
        v = getPlayer().getEyeLocation().getDirection().multiply(1.4).add(new Vector(0, 1, 0));
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
            if (hammer == null) return;
            hammer.setVelocity(getPlayer().getEyeLocation().toVector().subtract(hammer.getLocation().toVector()).multiply(0.2).add(new Vector(0, 0, 0)));
            v = null;
            Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
                if (hammer == null) return;
                ItemStack is;
                if (UltraCosmeticsData.get().isAmmoEnabled()) {
                    is = ItemFactory.create(getType().getMaterial(), ChatColor.WHITE + "" + ChatColor.BOLD + getOwner().getAmmo(getType()) + " " + getType().getName(), ChatColor.BLUE + "Gadget");
                } else {
                    is = ItemFactory.create(getType().getMaterial(), getType().getName(), MessageManager.getMessage("Gadgets.Lore"));
                }
                itemStack = is;
                getPlayer().getInventory().setItem(SettingsManager.getConfig().getInt("Gadget-Slot"), is);
                hammer.remove();
                hammer = null;
            }, 40);
        }, 20);
    }

    @Override
    public void onEquip() {
        super.onEquip();
        if (!USE_OTHER_LISTENER) return;
        listener = new HammerPickupListener(this);
        Bukkit.getPluginManager().registerEvents(listener, getUltraCosmetics());
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onItemPickup(org.bukkit.event.player.PlayerPickupItemEvent event) {
        if (hammer != event.getItem()) return;
        event.setCancelled(true);

        if (event.getPlayer() != getPlayer()) {
            if (v != null && canAffect(event.getPlayer())) {
                MathUtils.applyVelocity(event.getPlayer(), v);
            }
            return;
        }

        if (hammer.getTicksLived() <= 5) return;

        ItemStack is;
        if (UltraCosmeticsData.get().isAmmoEnabled()) {
            is = ItemFactory.create(getType().getMaterial(), ChatColor.WHITE + "" + ChatColor.BOLD + getOwner().getAmmo(getType()) + " " + getType().getName(), ChatColor.BLUE + "Gadget");
        } else {
            is = ItemFactory.create(getType().getMaterial(), getType().getName(), MessageManager.getMessage("Gadgets.Lore"));
        }
        itemStack = is;
        getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), is);
        hammer.remove();
        hammer = null;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onDamEnt(EntityDamageByEntityEvent event) {
        if (getOwner() != null
                && getPlayer() != null
                && event.getDamager() == getPlayer()
                && getPlayer().getItemInHand().equals(getItemStack())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onClear() {
        if (hammer != null) {
            hammer.remove();
            hammer = null;
        }
        v = null;
        if (!USE_OTHER_LISTENER) return;
        HandlerList.unregisterAll(listener);
        listener = null;
    }

    public Item getHammer() {
        return hammer;
    }
}
