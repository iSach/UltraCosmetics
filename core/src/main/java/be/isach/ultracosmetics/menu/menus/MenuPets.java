package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.menu.ClickRunnable;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.menu.PurchaseData;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.version.AnvilGUI;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cryptomorin.xseries.XMaterial;

import java.util.List;

/**
 * Pet {@link be.isach.ultracosmetics.menu.Menu Menu}.
 *
 * @author iSach
 * @since 08-23-2016
 */
public class MenuPets extends CosmeticMenu<PetType> {

    public MenuPets(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.PETS);
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer ultraPlayer, int page) {
        addPetRenameItem(inventory, ultraPlayer);
    }

    private void addPetRenameItem(Inventory inventory, UltraPlayer player) {
        if (SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled")) {
            ItemStack stack;
            int slot = inventory.getSize() - (getCategory().hasGoBackArrow() ? 4 : 6);
            ClickRunnable run;
            if (SettingsManager.getConfig().getBoolean("Pets-Rename.Permission-Required") && !player.hasPermission("ultracosmetics.pets.rename")) {
                return;
            }
            if (player.getCurrentPet() != null) {
                stack = ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Rename-Pet-Item"), MessageManager.getMessage("Menu.Rename-Pet.Button.Name").replace("%petname%", player.getCurrentPet().getType().getName()));
                run = data -> renamePet(player);
            } else {
                stack = ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Rename-Pet-Item"), MessageManager.getMessage("Active-Pet-Needed"));
                run = data -> {
                    player.getBukkitPlayer().sendMessage(MessageManager.getMessage("Active-Pet-Needed"));
                    player.getBukkitPlayer().closeInventory();
                };
            }
            putItem(inventory, slot, stack, run);
        }
    }

    public void renamePet(final UltraPlayer ultraPlayer) {
        new AnvilGUI.Builder().plugin(ultraCosmetics)
                .itemLeft(XMaterial.PAPER.parseItem())
                .text(MessageManager.getMessage("Menu.Rename-Pet.Placeholder"))
                .title(MessageManager.getMessage("Menu.Rename-Pet.Title"))
                .onComplete((Player player, String text) -> {
                    String newName = ChatColor.translateAlternateColorCodes('&', text);
                    if (SettingsManager.getConfig().getBoolean("Pets-Rename.Requires-Money.Enabled") &&
                            ultraCosmetics.getEconomyHandler().isUsingEconomy()) {
                        return AnvilGUI.Response.openInventory(buyRenamePet(ultraPlayer, newName));
                    } else {
                        ultraPlayer.setPetName(ultraPlayer.getCurrentPet().getType(), newName);
                        return AnvilGUI.Response.close();
                    }
                }).open(ultraPlayer.getBukkitPlayer());
    }

    private Inventory buyRenamePet(UltraPlayer ultraPlayer, final String name) {
        final String formattedName = ChatColor.translateAlternateColorCodes('&', name);
        ItemStack showcaseItem = ItemFactory.create(XMaterial.NAME_TAG, MessageManager.getMessage("Menu.Purchase-Rename.Button.Showcase")
                .replace("%price%", "" + SettingsManager.getConfig().get("Pets-Rename.Requires-Money.Price")).replace("%name%", formattedName));

        PurchaseData purchaseData = new PurchaseData();
        purchaseData.setPrice(SettingsManager.getConfig().getInt("Pets-Rename.Requires-Money.Price"));
        purchaseData.setShowcaseItem(showcaseItem);
        purchaseData.setOnPurchase(() -> {
            ultraPlayer.setPetName(ultraPlayer.getCurrentPet().getType(), formattedName);
            this.open(ultraPlayer);
        });
        purchaseData.setOnCancel(() -> this.open(ultraPlayer));

        MenuPurchase menu = new MenuPurchase(getUltraCosmetics(), MessageManager.getMessage("Menu.Purchase-Rename.Title"), purchaseData);
        return menu.getInventory(ultraPlayer);
    }

    @Override
    protected void filterItem(ItemStack itemStack, PetType cosmeticType, UltraPlayer player) {
        if (player.getPetName(cosmeticType) != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(itemMeta.getDisplayName() + ChatColor.GRAY + " (" + player.getPetName(cosmeticType) + ChatColor.GRAY + ")");
            itemStack.setItemMeta(itemMeta);
        }
    }

    @Override
    public List<PetType> enabled() {
        return PetType.enabled();
    }

    @Override
    protected void toggleOn(UltraPlayer ultraPlayer, PetType petType, UltraCosmetics ultraCosmetics) {
        petType.equip(ultraPlayer, ultraCosmetics);
    }
}
