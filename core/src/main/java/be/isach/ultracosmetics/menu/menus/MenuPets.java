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
import be.isach.ultracosmetics.util.UCMaterial;
import be.isach.ultracosmetics.version.AAnvilGUI;
import be.isach.ultracosmetics.version.VersionManager;
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
        Player p = ultraPlayer.getBukkitPlayer();
        if (UltraCosmeticsData.get().getServerVersion().isAtLeast(ServerVersion.v1_14_R1)) {
            UltraCosmeticsData.get().getVersionManager().newAnvilGUI(p,
                    "",
                    (player1 -> {
                    }),
                    (player2, text) -> {
                        if (SettingsManager.getConfig().getBoolean("Pets-Rename.Requires-Money.Enabled") &&
                                ultraCosmetics.getEconomyHandler().isUsingEconomy()) {
                            buyRenamePet(ultraPlayer, text.replaceAll("[^A-Za-z0-9 &éèêë]", "")
                                    .replace("&", "§"));
                            return new AAnvilGUI.Response("BUY");
                        } else {
                            ultraPlayer.setPetName(ultraPlayer.getCurrentPet().getType(), text);
                            return AAnvilGUI.Response.close();
                        }
                    });
        } else {
            AAnvilGUI gui = newAnvilGUI(p, (AAnvilGUI.AnvilClickEvent event) -> {
                if (event.getSlot() == AAnvilGUI.AnvilSlot.OUTPUT) {
                    if (event.getName() == null) {
                        return;
                    }
                    if (SettingsManager.getConfig().getBoolean("Pets-Rename.Requires-Money.Enabled") && ultraCosmetics.getEconomyHandler().isUsingEconomy()) {
                        event.setWillClose(false);
                        event.setWillDestroy(false);
                        buyRenamePet(ultraPlayer, event.getName()
                                .replaceAll("[^A-Za-z0-9 &éèêë]", "")
                                .replace("&", "§"));
                    } else {
                        ultraPlayer.setPetName(ultraPlayer.getCurrentPet().getType(), event.getName());
                    }
                } else {
                    event.setWillClose(false);
                    event.setWillDestroy(false);
                }
            });
            gui.setSlot(AAnvilGUI.AnvilSlot.INPUT_LEFT, ItemFactory.create(UCMaterial.PAPER, ""));
            gui.open();
        }
    }

    private AAnvilGUI newAnvilGUI(Player player, AAnvilGUI.AnvilClickEventHandler handler) {
        VersionManager versionManager = UltraCosmeticsData.get().getVersionManager();
        return versionManager.newAnvilGUI(player, handler);
    }

    private void buyRenamePet(UltraPlayer ultraPlayer, final String name) {
        ItemStack showcaseItem = ItemFactory.create(UCMaterial.NAME_TAG, MessageManager.getMessage("Rename-Pet-Purchase")
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
        if (player.getPetName(cosmeticType) != null) {
            ItemStack item = itemStack.clone();
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(itemMeta.getDisplayName() + ChatColor.GRAY + " (" + player.getPetName(cosmeticType) + ChatColor.GRAY + ")");
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