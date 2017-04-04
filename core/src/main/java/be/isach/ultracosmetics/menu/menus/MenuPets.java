package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.menu.ClickRunnable;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.version.AAnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Pet {@link be.isach.ultracosmetics.menu.Menu Menu}.
 * 
 * @author 	iSach
 * @since 	08-23-2016
 */
public class MenuPets extends CosmeticMenu<PetType> {
    private UltraCosmetics ultraCosmetics;

    public MenuPets(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.PETS);
        this.ultraCosmetics = ultraCosmetics;
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer ultraPlayer, int page) {
        addPetRenameItem(inventory, ultraPlayer);
    }

    private void addPetRenameItem(Inventory inventory, UltraPlayer player) {
        ItemStack stack = null;
        int slot = inventory.getSize() - (getCategory().hasGoBackArrow() ? 5 : 6);
        ClickRunnable run = null;
        if (SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled")) {
            if (SettingsManager.getConfig().getBoolean("Pets-Rename.Permission-Required")) {
                if (player.hasPermission("ultracosmetics.pets.rename")) {
                    if (player.getCurrentPet() != null) {
                        stack = ItemFactory.create(ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getItemType(), ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getData(), MessageManager.getMessage("Rename-Pet").replace("%petname%", player.getCurrentPet().getType().getName()));
                        run = data -> {
                            player.getBukkitPlayer().closeInventory();
                            renamePet(player);
                        };
                    } else {
                        stack = ItemFactory.create(ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getItemType(), ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getData(), MessageManager.getMessage("Active-Pet-Needed"));
                        run = data -> {
                            player.getBukkitPlayer().closeInventory();
                            player.getBukkitPlayer().sendMessage(MessageManager.getMessage("Active-Pet-Needed"));
                        };
                    }
                }
            } else if (player.getCurrentPet() != null) {
                stack = ItemFactory.create(ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getItemType(), ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getData(), MessageManager.getMessage("Rename-Pet").replace("%petname%", player.getCurrentPet().getType().getName()));
                run = data -> {
                    player.getBukkitPlayer().closeInventory();
                    renamePet(player);
                };
            } else {
                stack = ItemFactory.create(ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getItemType(), ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getData(), MessageManager.getMessage("Active-Pet-Needed"));
                run = data -> {
                    player.getBukkitPlayer().sendMessage(MessageManager.getMessage("Active-Pet-Needed"));
                    player.getBukkitPlayer().closeInventory();
                };
            }
        }
        putItem(inventory, slot, stack, run);
    }

    private void renamePet(final UltraPlayer ultraPlayer) {
        Player p = ultraPlayer.getBukkitPlayer();
        AAnvilGUI gui = newAnvilGUI(ultraPlayer.getBukkitPlayer(), (AAnvilGUI.AnvilClickEvent event) -> {
            if (event.getSlot() == AAnvilGUI.AnvilSlot.OUTPUT) {
                event.setWillClose(true);
                event.setWillDestroy(true);
                if (event.getName() == null || event.getName().equals("")) {
                    return;
                }
                if (SettingsManager.getConfig().getBoolean("Pets-Rename.Requires-Money.Enabled") && UltraCosmeticsData.get().isUsingVaultEconomy()) {
                    buyRenamePet(p, ChatColor.translateAlternateColorCodes('&', event.getName().replaceAll("[^A-Za-z0-9 &&[^&]]", "").replace(" ", "")));
                } else {
                    if (ultraPlayer.getCurrentPet().getType() == PetType.WITHER || UltraCosmeticsData.get().getServerVersion() == ServerVersion.v1_11_R1) {
                        ultraPlayer.getCurrentPet().entity.setCustomName(ChatColor.translateAlternateColorCodes('&', event.getName().replaceAll("[^A-Za-z0-9 &&[^&]]", "").replace(" ", "")));
                    } else {
                        ultraPlayer.getCurrentPet().armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', event.getName().replaceAll("[^A-Za-z0-9 &&[^&]]", "").replace(" ", "")));
                    }
                    ultraPlayer.setPetName(ultraPlayer.getCurrentPet().getType().getConfigName(), ChatColor.translateAlternateColorCodes('&', event.getName().replaceAll("[^A-Za-z0-9 &&[^&]]", "").replace(" ", "")));
                }
            } else {
                event.setWillClose(false);
                event.setWillDestroy(false);
            }
        });
        gui.setSlot(AAnvilGUI.AnvilSlot.INPUT_LEFT, ItemFactory.create(Material.NAME_TAG, (byte) 0x0, ""));
        gui.open();
    }

    private AAnvilGUI newAnvilGUI(Player player, AAnvilGUI.AnvilClickEventHandler handler) {
        return UltraCosmeticsData.get().getVersionManager().newAnvilGUI(player, handler);
    }

    private void buyRenamePet(final Player p, final String name) {
        // TODO Add InventoryClick Listener for this.
        p.closeInventory();
        Inventory inventory = Bukkit.createInventory(null, 54, MessageManager.getMessage("Menus.Rename-Pet"));
        for (int i = 27; i < 30; i++) {
            inventory.setItem(i, ItemFactory.create(Material.EMERALD_BLOCK, (byte) 0x0, MessageManager.getMessage("Purchase")));
            inventory.setItem(i + 9, ItemFactory.create(Material.EMERALD_BLOCK, (byte) 0x0, MessageManager.getMessage("Purchase")));
            inventory.setItem(i + 18, ItemFactory.create(Material.EMERALD_BLOCK, (byte) 0x0, MessageManager.getMessage("Purchase")));
            inventory.setItem(i + 6, ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Cancel")));
            inventory.setItem(i + 9 + 6, ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Cancel")));
            inventory.setItem(i + 18 + 6, ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Cancel")));
        }
        inventory.setItem(13, ItemFactory.create(Material.NAME_TAG, (byte) 0x0, MessageManager.getMessage("Rename-Pet-Purchase")
                .replace("%price%", "" + SettingsManager.getConfig().getString("Pets-Rename.Requires-Money.Price")).replace("%name%", name)));
        ItemFactory.fillInventory(inventory);
        Bukkit.getScheduler().runTaskLater(ultraCosmetics, () -> p.openInventory(inventory), 5);
    }

    @Override
    public List<PetType> enabled() {
        return PetType.enabled();
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, String name, UltraCosmetics ultraCosmetics) {
        PetType.getByName(name).equip(ultraPlayer, ultraCosmetics);
    }

    @Override
    protected void toggleOff(UltraPlayer ultraPlayer) {
        ultraPlayer.removePet();
    }

    @Override
    protected Cosmetic getCosmetic(UltraPlayer ultraPlayer) {
        return ultraPlayer.getCurrentPet();
    }
}
