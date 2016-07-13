package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.treasurechests.TreasureChest;
import be.isach.ultracosmetics.cosmetics.treasurechests.TreasureChestDesign;
import be.isach.ultracosmetics.menu.MainMenuManager;
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

    private static void openTreasureChest(Player player) {
        String designPath = getRandomDesign();
        player.closeInventory();
        new TreasureChest(player.getUniqueId(), new TreasureChestDesign(designPath));
    }

    private static String getRandomDesign() {
        Set<String> set = UltraCosmetics.config.getConfigurationSection("TreasureChests.Designs").getKeys(false);
        List<String> list = new ArrayList<>();
        list.addAll(set);
        return list.get(random.nextInt(set.size()));
    }

    public static void tryOpenChest(Player player) {
        if (UltraCosmetics.getCustomPlayer(player).getKeys() > 0) {
            Cuboid c = new Cuboid(player.getLocation().add(-2, 0, -2), player.getLocation().add(2, 1, 2));
            if (!c.isEmpty()) {
                player.sendMessage(MessageManager.getMessage("Chest-Not-Enough-Space"));
                return;
            }
            for (Entity ent : player.getNearbyEntities(5, 5, 5)) {
                if (ent instanceof Player && UltraCosmetics.getCustomPlayer((Player) ent).currentTreasureChest != null) {
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
            UltraCosmetics.getCustomPlayer(player).removeKey();
            openTreasureChest(player);
        } else {
            player.closeInventory();
            UltraCosmetics.getCustomPlayer(player).openKeyPurchaseMenu();
        }
    }

    @EventHandler
    public void openChest(InventoryClickEvent event) {
        if (event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName()
                && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Treasure-Chests"))) {
            if (!UltraCosmetics.getInstance().isVaultLoaded() && UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).getKeys() == 0) {
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
            if (!UltraCosmetics.getInstance().isVaultLoaded() && UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).getKeys() == 0) {
                SoundUtil.playSound(event.getWhoClicked().getLocation(), Sounds.ANVIL_LAND, 0.2f, 1.2f);
                return;
            }
            event.getWhoClicked().closeInventory();
            UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).openKeyPurchaseMenu();
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
                if (UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).getBalance() >= (int) SettingsManager.getConfig().get("TreasureChests.Key-Price")) {
                    UltraCosmetics.economy.withdrawPlayer((Player) event.getWhoClicked(), (int) SettingsManager.getConfig().get("TreasureChests.Key-Price"));
                    UltraCosmetics.getCustomPlayer((Player) event.getWhoClicked()).addKey();
                    event.getWhoClicked().sendMessage(MessageManager.getMessage("Successful-Purchase"));
                    event.getWhoClicked().closeInventory();
                    MainMenuManager.openMenu((Player) event.getWhoClicked());
                } else {
                    event.getWhoClicked().sendMessage(MessageManager.getMessage("Not-Enough-Money"));
                    event.getWhoClicked().closeInventory();
                    return;
                }
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Cancel"))) {
                event.getWhoClicked().closeInventory();
                MainMenuManager.openMenu((Player) event.getWhoClicked());
            }
        }
    }

}
