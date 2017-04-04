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
import be.isach.ultracosmetics.util.PurchaseData;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.version.AAnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Pet {@link be.isach.ultracosmetics.menu.Menu Menu}.
 *
 * @author iSach
 * @since 08-23-2016
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
        if (SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled")) {
            ItemStack stack = null;
            int slot = inventory.getSize() - (getCategory().hasGoBackArrow() ? 5 : 6);
            ClickRunnable run = null;
            if (SettingsManager.getConfig().getBoolean("Pets-Rename.Permission-Required")) {
                if (player.hasPermission("ultracosmetics.pets.rename")) {
                    if (player.getCurrentPet() != null) {
                        stack = ItemFactory.create(ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getItemType(), ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getData(), MessageManager.getMessage("Rename-Pet").replace("%petname%", player.getCurrentPet().getType().getName()));
                        run = data -> {
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
            } else if (player.getCurrentPet() != null) {
                stack = ItemFactory.create(ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getItemType(), ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getData(), MessageManager.getMessage("Rename-Pet").replace("%petname%", player.getCurrentPet().getType().getName()));
                run = data -> {
                    renamePet(player);
                };
            } else {
                stack = ItemFactory.create(ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getItemType(), ItemFactory.createFromConfig("Categories.Rename-Pet-Item").getData(), MessageManager.getMessage("Active-Pet-Needed"));
                run = data -> {
                    player.getBukkitPlayer().sendMessage(MessageManager.getMessage("Active-Pet-Needed"));
                    player.getBukkitPlayer().closeInventory();
                };
            }
            putItem(inventory, slot, stack, run);
        }
    }

    private void renamePet(final UltraPlayer ultraPlayer) {
        Player p = ultraPlayer.getBukkitPlayer();
        AAnvilGUI gui = newAnvilGUI(ultraPlayer.getBukkitPlayer(), (AAnvilGUI.AnvilClickEvent event) -> {
            if (event.getSlot() == AAnvilGUI.AnvilSlot.OUTPUT) {
                if (event.getName() == null) {
                    return;
                }
                if (SettingsManager.getConfig().getBoolean("Pets-Rename.Requires-Money.Enabled") && UltraCosmeticsData.get().getPlugin().isVaultLoaded()) {
                    event.setWillClose(false);
                    event.setWillDestroy(false);
                    buyRenamePet(ultraPlayer, ChatColor.translateAlternateColorCodes('&', event.getName().replaceAll("[^A-Za-z0-9 &&[^&]]", "").replace(" ", "")));
                } else {
                    ultraPlayer.setPetName(ultraPlayer.getCurrentPet().getType(), event.getName());
                }
            } else {
                event.setWillClose(false);
                event.setWillDestroy(false);
            }
        });
        gui.setSlot(AAnvilGUI.AnvilSlot.INPUT_LEFT, ItemFactory.create(Material.PAPER, (byte) 0x0, ""));
        gui.open();
    }

    private AAnvilGUI newAnvilGUI(Player player, AAnvilGUI.AnvilClickEventHandler handler) {
        return UltraCosmeticsData.get().getVersionManager().newAnvilGUI(player, handler);
    }

    private void buyRenamePet(UltraPlayer ultraPlayer, final String name) {
        ItemStack showcaseItem = ItemFactory.create(Material.NAME_TAG, (byte) 0x0, MessageManager.getMessage("Rename-Pet-Purchase")
                .replace("%price%", "" + SettingsManager.getConfig().get("Pets-Rename.Requires-Money.Price")).replace("%name%", name));

        PurchaseData purchaseData = new PurchaseData();
        purchaseData.setPrice(SettingsManager.getConfig().getInt("Pets-Rename.Requires-Money.Price"));
        purchaseData.setShowcaseItem(showcaseItem);
        purchaseData.setOnPurchase(() -> {
            ultraPlayer.setPetName(ultraPlayer.getCurrentPet().getType(), name);
        });

        MenuPurchase menu = new MenuPurchase(getUltraCosmetics(), MessageManager.getMessage("Menus.Rename-Pet"), purchaseData);
        menu.open(ultraPlayer);
    }

    @Override
    protected ItemStack filterItem(ItemStack itemStack, PetType cosmeticType, UltraPlayer player) {
        if(player.getPetName(cosmeticType) != null) {
            ItemStack item = itemStack.clone();
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(itemMeta.getDisplayName() + " §r§7(" + player.getPetName(cosmeticType) + ")");
            item.setItemMeta(itemMeta);
            return item;
        }
        return super.filterItem(itemStack, cosmeticType, player);
    }

    @Override
    public List<PetType> enabled() {
        return PetType.enabled();
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, PetType petType, UltraCosmetics ultraCosmetics) {
        petType.equip(ultraPlayer, ultraCosmetics);
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
