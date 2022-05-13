package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.PlayerAffectingCosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Effect;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Represents an instance of a melon thrower gadget summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public class GadgetMelonThrower extends Gadget implements PlayerAffectingCosmetic, Updatable {
    private Item melon = null;
    private final Set<Item> melonSlices = new HashSet<>();

    public GadgetMelonThrower(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("melonthrower"), ultraCosmetics);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onTakeUpMelon(org.bukkit.event.player.PlayerPickupItemEvent event) {
        if (melonSlices.contains(event.getItem())
                && event.getItem().getTicksLived() > 5
                && canAffect(event.getPlayer())) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2));
            XSound.ENTITY_PLAYER_BURP.play(getPlayer().getLocation(), 1.4f, 1.5f);
            event.getItem().remove();
            melonSlices.remove(event.getItem());
            // Should be done anyway by PlayerListener, but just to be safe
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {
        if (melonSlices.contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @Override
    protected boolean checkRequirements(PlayerInteractEvent event) {
        // Check if the current melon has finished exploding.
        if (melon != null) {
            event.getPlayer().sendMessage(MessageManager.getMessage("Gadgets.MelonThrower.Wait-For-Finish"));
            return false;
        }
        return true;
    }

    @Override
    void onRightClick() {
        XSound.ENTITY_GENERIC_EXPLODE.play(getPlayer().getLocation(), 1.4f, 1.5f);
        melon = ItemFactory.createUnpickableItemDirectional(XMaterial.MELON, getPlayer(), 1.3);
    }

    @Override
    public void onUpdate() {
        if (melon == null || !melon.isValid()) {
            return;
        }
        if (melon.isOnGround()) {
            melon.getWorld().playEffect(melon.getLocation(), Effect.STEP_SOUND, 103);
            for (int i = 0; i < 8; i++) {
                melonSlices.add(ItemFactory.createUnpickableItemVariance(XMaterial.MELON_SLICE, melon.getLocation(), RANDOM, 0.75));
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Item slice : melonSlices) {
                        slice.remove();
                    }
                    melonSlices.clear();
                }
            }.runTaskLater(getUltraCosmetics(), 100);
            melon.remove();
            melon = null;
        }
    }

    @Override
    public void onClear() {
        if (melon != null) {
            melon.remove();
        }

        for (Item slice : melonSlices) {
            slice.remove();
        }
        melonSlices.clear();
    }
}
