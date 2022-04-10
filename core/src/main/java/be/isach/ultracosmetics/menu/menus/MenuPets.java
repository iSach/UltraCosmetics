package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.menu.ClickRunnable;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.PurchaseData;
import com.cryptomorin.xseries.XMaterial;
import be.isach.ultracosmetics.version.AnvilGUI;
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
            ItemStack stack;
            int slot = inventory.getSize() - (getCategory().hasGoBackArrow() ? 5 : 6);
            ClickRunnable run;
            if (SettingsManager.getConfig().getBoolean("Pets-Rename.Permission-Required")) {
                if (player.hasPermission("ultracosmetics.pets.rename")) {
                    if (player.getCurrentPet() != null) {
                        stack = ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Rename-Pet-Item"), MessageManager.getMessage("Rename-Pet").replace("%petname%", player.getCurrentPet().getType().getName()));
                        run = data -> renamePet(player);
                    } else {
                        stack = ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Rename-Pet-Item"), MessageManager.getMessage("Active-Pet-Needed"));
                        run = data -> {
                            player.getBukkitPlayer().sendMessage(MessageManager.getMessage("Active-Pet-Needed"));
                            player.getBukkitPlayer().closeInventory();
                        };
                    }
                } else {
                    stack = new ItemStack(Material.AIR);
                    run = data -> {
                    };
                }
            } else if (player.getCurrentPet() != null) {
                stack = ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Rename-Pet-Item"), MessageManager.getMessage("Rename-Pet").replace("%petname%", player.getCurrentPet().getType().getName()));
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
        .text(MessageManager.getMessage("Rename-Pet-Placeholder"))
        .title(MessageManager.getMessage("Rename-Pet-Title"))
        .onComplete((Player player, String text) -> {
            if (SettingsManager.getConfig().getBoolean("Pets-Rename.Requires-Money.Enabled") &&
                    ultraCosmetics.getEconomyHandler().isUsingEconomy()) {
                return AnvilGUI.Response.openInventory(buyRenamePet(ultraPlayer, text));
            } else {
                ultraPlayer.setPetName(ultraPlayer.getCurrentPet().getType(), text);
                return AnvilGUI.Response.close();
            }
        }).open(ultraPlayer.getBukkitPlayer());
    }

    private Inventory buyRenamePet(UltraPlayer ultraPlayer, final String name) {
        final String formattedName = ChatColor.translateAlternateColorCodes('&', name);
        ItemStack showcaseItem = ItemFactory.create(XMaterial.NAME_TAG, MessageManager.getMessage("Rename-Pet-Purchase")
                .replace("%price%", "" + SettingsManager.getConfig().get("Pets-Rename.Requires-Money.Price")).replace("%name%", formattedName));

        PurchaseData purchaseData = new PurchaseData();
        purchaseData.setPrice(SettingsManager.getConfig().getInt("Pets-Rename.Requires-Money.Price"));
        purchaseData.setShowcaseItem(showcaseItem);
        purchaseData.setOnPurchase(() -> {
            ultraPlayer.setPetName(ultraPlayer.getCurrentPet().getType(), formattedName);
        });

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

    @Override
    protected void toggleOff(UltraPlayer ultraPlayer, PetType type) {
        ultraPlayer.removePet();
    }

    @Override
    protected Pet getCosmetic(UltraPlayer ultraPlayer) {
        return ultraPlayer.getCurrentPet();
    }
}