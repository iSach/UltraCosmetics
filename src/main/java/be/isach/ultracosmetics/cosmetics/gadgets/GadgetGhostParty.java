package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Bat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Sacha on 18/10/15.
 */
public class GadgetGhostParty extends Gadget {

    Map<Bat, ArmorStand> bats = new HashMap<>();

    public GadgetGhostParty(UUID owner) {
        super(Material.SKULL_ITEM, (byte) 0x0, "GhostParty", "ultracosmetics.gadgets.ghostparty", 45, owner, GadgetType.GHOSTPARTY, "&7&oWho Ya Gonna Call?\n&f&lGHOST &4&lBUSTERS");

        if (owner != null)
            Core.registerListener(this);
    }

    @Override
    void onInteractRightClick() {
        for (int i = 0; i < 20; i++) {
            Bat bat = getPlayer().getWorld().spawn(getPlayer().getLocation().add(0, 1, 0), Bat.class);
            ArmorStand ghost = bat.getWorld().spawn(bat.getLocation(), ArmorStand.class);
            ghost.setSmall(true);
            ghost.setGravity(false);
            ghost.setVisible(false);
            ghost.setHelmet(ItemFactory.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhkMjE4MzY0MDIxOGFiMzMwYWM1NmQyYWFiN2UyOWE5NzkwYTU0NWY2OTE2MTllMzg1NzhlYTRhNjlhZTBiNiJ9fX0"));
            ghost.setChestplate(ItemFactory.createColouredLeather(Material.LEATHER_CHESTPLATE, 255, 255, 255));
            ghost.setItemInHand(new ItemStack(Material.DIAMOND_HOE));
            bat.setPassenger(ghost);
            bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 160, 1));
            bats.put(bat, ghost);
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                killBats();
            }
        }, 160);
    }

    @EventHandler
    public void onPlayerInteractGhost(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() != null && event.getRightClicked().getVehicle() != null
                && bats.containsKey(event.getRightClicked().getVehicle()))
            event.setCancelled(true);
    }

    private void killBats() {
        for (Bat bat : bats.keySet()) {
            bats.get(bat).remove();
            bat.remove();
        }
        bats.clear();
    }

    @Override
    void onInteractLeftClick() {

    }

    @Override
    void onUpdate() {
        try {
            if (!bats.isEmpty()) {
                for (Bat bat : bats.keySet())
                    UtilParticles.play(Particles.CLOUD, 0.05f, 0.05f, 0.05f, bat.getLocation().add(0, 1.5, 0), 1);
            }
        } catch (Exception exc) {
        }
    }

    @Override
    public void onClear() {
        killBats();
        HandlerList.unregisterAll(this);
    }
}
