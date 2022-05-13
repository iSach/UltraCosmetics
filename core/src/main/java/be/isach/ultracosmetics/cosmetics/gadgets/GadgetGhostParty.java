package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.Particles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Bat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an instance of a ghost party gadget summoned by a player.
 *
 * @author iSach
 * @since 10-18-2015
 */
public class GadgetGhostParty extends Gadget implements Updatable {

    Map<Bat, ArmorStand> bats = new HashMap<>();

    public GadgetGhostParty(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("ghostparty"), ultraCosmetics);
    }

    @SuppressWarnings("deprecation")
    @Override
    void onRightClick() {
        for (int i = 0; i < 20; i++) {
            Bat bat = getPlayer().getWorld().spawn(getPlayer().getLocation().add(0, 1, 0), Bat.class);
            ArmorStand ghost = bat.getWorld().spawn(bat.getLocation(), ArmorStand.class);
            ghost.setSmall(true);
            ghost.setGravity(false);
            ghost.setVisible(false);
            ghost.setHelmet(ItemFactory.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhkMjE4MzY0MDIxOGFiMzMwYWM1NmQyYWFiN2UyOWE5NzkwYTU0NWY2OTE2MTllMzg1NzhlYTRhNjlhZTBiNiJ9fX0", ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Ghost"));
            ghost.setChestplate(ItemFactory.createColouredLeather(Material.LEATHER_CHESTPLATE, 255, 255, 255));
            ghost.setItemInHand(new ItemStack(Material.DIAMOND_HOE));
            bat.setPassenger(ghost);
            bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 160, 1));
            bats.put(bat, ghost);
        }
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), this::killBats, 160);
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
    public void onUpdate() {
        for (Bat bat : bats.keySet()) {
            Particles.CLOUD.display(0.05f, 0.05f, 0.05f, bat.getLocation().add(0, 1.5, 0), 1);                
        }
    }

    @Override
    public void onClear() {
        killBats();
    }
}
