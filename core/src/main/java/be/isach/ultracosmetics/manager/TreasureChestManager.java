package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.treasurechests.TreasureChest;
import be.isach.ultracosmetics.treasurechests.TreasureChestDesign;
import be.isach.ultracosmetics.util.Cuboid;
import be.isach.ultracosmetics.util.SoundUtil;
import be.isach.ultracosmetics.util.Sounds;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by Sacha on 11/11/15.
 */
public class TreasureChestManager implements Listener {

    private static Random random = new Random();
    private UltraCosmetics ultraCosmetics;

    public TreasureChestManager(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
    }

    private void openTreasureChest(Player player) {
        String designPath = getRandomDesign();
        player.closeInventory();
        new TreasureChest(ultraCosmetics.getPlayerManager().getUltraPlayer(player), new TreasureChestDesign(designPath), ultraCosmetics);
    }

    private String getRandomDesign() {
        Set<String> set = SettingsManager.getConfig().getConfigurationSection("TreasureChests.Designs").getKeys(false);
        List<String> list = new ArrayList<>();
        list.addAll(set);
        return list.get(random.nextInt(set.size()));
    }

    public void tryOpenChest(Player player) {
        if (ultraCosmetics.getPlayerManager().getUltraPlayer(player).getKeys() > 0) {
            Cuboid c = new Cuboid(player.getLocation().add(-2, 0, -2), player.getLocation().add(2, 1, 2));
            if (!c.isEmpty()) {
                player.sendMessage(MessageManager.getMessage("Chest-Not-Enough-Space"));
                return;
            }
            for (Entity ent : player.getNearbyEntities(5, 5, 5)) {
                if (ent instanceof Player && ultraCosmetics.getPlayerManager().getUltraPlayer((Player) ent).getCurrentTreasureChest() != null) {
                    player.closeInventory();
                    player.sendMessage(MessageManager.getMessage("Too-Close-To-Other-Chest"));
                    return;
                }
            }
            if (player.getLocation().getBlock().getRelative(BlockFace.UP).getType() != Material.AIR
                    || !player.getLocation().getBlock().getType().isBlock()
                    || player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                player.sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-On-Ground"));
                return;
            }
            ultraCosmetics.getPlayerManager().getUltraPlayer(player).removeKey();
            openTreasureChest(player);
        } else {
            player.closeInventory();
            ultraCosmetics.getPlayerManager().getUltraPlayer(player).openKeyPurchaseMenu();
        }
    }

    @EventHandler
    public void openChest(InventoryClickEvent event) {
        if (event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName()
                && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Treasure-Chests"))) {
            if (ultraCosmetics.getEconomy() == null && ultraCosmetics.getPlayerManager().getUltraPlayer((Player) event.getWhoClicked()).getKeys() == 0) {
                SoundUtil.playSound(event.getWhoClicked().getLocation(), Sounds.ANVIL_LAND, 0.2f, 1.2f);
                return;
            }
            Player player = (Player) event.getWhoClicked();
            tryOpenChest(player);
        }
    }

    @EventHandler
    public void buyKeyOpenInv(InventoryClickEvent event) {
        if (event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName()
                && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Treasure-Keys"))) {
            if (ultraCosmetics.getEconomy() == null && ultraCosmetics.getPlayerManager().getUltraPlayer((Player) event.getWhoClicked()).getKeys() == 0) {
                SoundUtil.playSound(event.getWhoClicked().getLocation(), Sounds.ANVIL_LAND, 0.2f, 1.2f);
                return;
            }
            event.getWhoClicked().closeInventory();
            ultraCosmetics.getPlayerManager().getUltraPlayer((Player) event.getWhoClicked()).openKeyPurchaseMenu();
        }
    }

    @EventHandler
    public void buyKeyConfirm(InventoryClickEvent event) {
        if (!event.getInventory().getTitle().equalsIgnoreCase(MessageManager.getMessage("Buy-Treasure-Key"))) return;
        event.setCancelled(true);
        if (event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName()) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Purchase"))) {
                if (ultraCosmetics.getPlayerManager().getUltraPlayer((Player) event.getWhoClicked()).getBalance() >= (int) SettingsManager.getConfig().get("TreasureChests.Key-Price")) {
                    ultraCosmetics.getEconomy().withdrawPlayer((Player) event.getWhoClicked(), (int) SettingsManager.getConfig().get("TreasureChests.Key-Price"));
                    ultraCosmetics.getPlayerManager().getUltraPlayer((Player) event.getWhoClicked()).addKey();
                    event.getWhoClicked().sendMessage(MessageManager.getMessage("Successful-Purchase"));
                    event.getWhoClicked().closeInventory();
//                    MenuMain.openMenu((Player) event.getWhoClicked()); TODO
                } else {
                    event.getWhoClicked().sendMessage(MessageManager.getMessage("Not-Enough-Money"));
                    event.getWhoClicked().closeInventory();
                    return;
                }
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Cancel"))) {
                event.getWhoClicked().closeInventory();
//                MenuMain.openMenu((Player) event.getWhoClicked()); TODO
            }
        }
    }

}
