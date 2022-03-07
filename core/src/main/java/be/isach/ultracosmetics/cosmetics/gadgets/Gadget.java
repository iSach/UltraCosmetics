package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.menu.CosmeticsInventoryHolder;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.UUID;

/**
 * Represents an instance of a Gadget summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public abstract class Gadget extends Cosmetic<GadgetType> implements Updatable {

    private static final DecimalFormatSymbols OTHER_SYMBOLS = new DecimalFormatSymbols(Locale.US);
    private static final DecimalFormat DECIMAL_FORMAT;

    static {
        OTHER_SYMBOLS.setDecimalSeparator('.');
        OTHER_SYMBOLS.setGroupingSeparator('.');
        OTHER_SYMBOLS.setPatternSeparator('.');
        DECIMAL_FORMAT = new DecimalFormat("0.0", OTHER_SYMBOLS);
    }

    /**
     * Page the user was on when trying to buy ammo.
     * Is used when player buys ammo from Gadget Menu.
     */
    public int lastPage = 1;

    /**
     * If it should open Gadget Menu after purchase.
     */
    public boolean openGadgetsInvAfterAmmo;

    /**
     * If true, it will differentiate left and right click.
     */
    protected boolean useTwoInteractMethods = false;

    /**
     * Gadget ItemStack.
     */
    protected ItemStack itemStack;

    /**
     * If true, will display cooldown left when fail on use
     * because cooldown active.
     */
    protected boolean displayCooldownMessage = true;

    /**
     * Last Clicked Block by the player.
     */
    protected Block lastClickedBlock;

    /**
     * If true, it will affect players (velocity).
     */
    protected boolean affectPlayers;

    /**
     * If Gadget interaction should tick asynchronously.
     */
    private boolean asynchronous = false;

    /**
     * The Ammo Purchase inventory.
     */
    private Inventory ammoInventory;

    public Gadget(UltraPlayer owner, GadgetType type, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.GADGETS, owner, type);

        this.affectPlayers = type.affectPlayers();
    }

    @Override
    protected void onEquip() {
        if (getUltraCosmetics().getPlayerManager().getUltraPlayer(getPlayer()).getCurrentGadget() != null) {
            getUltraCosmetics().getPlayerManager().getUltraPlayer(getPlayer()).removeGadget();
        }

        runTaskTimer(getUltraCosmetics(), 0, 1);

        if (getPlayer().getInventory().getItem(ConfigUtils.getGadgetSlot()) != null) {
            getPlayer().getWorld().dropItem(getPlayer().getLocation(),
                    getPlayer().getInventory().getItem(ConfigUtils.getGadgetSlot()));
            getPlayer().getInventory().setItem(ConfigUtils.getGadgetSlot(), null);
        }

        String ammo = "";
        if (UltraCosmeticsData.get().isAmmoEnabled() && getType().requiresAmmo()) {
            ammo = ChatColor.WHITE + "" + ChatColor.BOLD + getOwner().getAmmo(getType()) + " ";
        }

        itemStack = ItemFactory.create(getType().getMaterial(), ammo + getType().getName(),
                MessageManager.getMessage("Gadgets.Lore"));
        getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), itemStack);

        getUltraCosmetics().getPlayerManager().getUltraPlayer(getPlayer()).setCurrentGadget(this);
    }

    @Override
    public void run() {
        try {
            if (getOwner() == null || getPlayer() == null) {
                return;
            }

            if (getOwner().getCurrentGadget() != null && getOwner().getCurrentGadget().getType() == getType()) {
                onUpdate();
                try {
                    if (UltraCosmeticsData.get().displaysCooldownInBar()) {
                        // TODO: this is ugly
                        if (getPlayer().getItemInHand() != null && itemStack != null && getPlayer().getItemInHand()
                                .hasItemMeta() && getPlayer().getItemInHand().getType() == getItemStack().getType()
                                && getPlayer().getItemInHand().getData().getData() == getItemStack().getData().getData()
                                && getPlayer().getItemInHand().getItemMeta().hasDisplayName() && getPlayer()
                                .getItemInHand().getItemMeta().getDisplayName().endsWith(getType().getName())
                                && getUltraCosmetics().getPlayerManager().getUltraPlayer(getPlayer()).canUse(getType())
                                != -1) {
                            sendCooldownBar();
                        }
                    }
                } catch (NullPointerException ignored) {
                    // Caused by rapid item switching in inventory.
                }
                if (getOwner() == null || getPlayer() == null) {
                    return;
                }

                double left = getUltraCosmetics().getPlayerManager().getUltraPlayer(getPlayer()).canUse(getType());
                if (left >= 0) {
                    String leftRounded = DECIMAL_FORMAT.format(left);
                    double decimalRoundedValue = Double.parseDouble(leftRounded);
                    if (decimalRoundedValue == 0) {
                        String message = MessageManager.getMessage("Gadgets.Gadget-Ready-ActionBar");
                        message = message.replace("%gadgetname%",
                                TextUtil.filterPlaceHolder(getType().getName(), getUltraCosmetics()));
                        UltraCosmeticsData.get().getVersionManager().getAncientUtil().sendActionBarMessage(getPlayer(), message);
                        SoundUtil.playSound(getPlayer(), Sounds.NOTE_STICKS, 1.4f, 1.5f);
                    }
                }
            } else {
                clear();
            }
        } catch (NullPointerException exc) {
            clear();
            exc.printStackTrace();
        }
    }

    @Override
    public void clear() {
        removeItem();
        super.clear();
    }

    /**
     * Sends the current cooldown in action bar.
     */

    private void sendCooldownBar() {
        if (getOwner() == null)
            return;
        if (getPlayer() == null)
            return;

        StringBuilder stringBuilder = new StringBuilder();

        double currentCooldown = getUltraCosmetics().getPlayerManager().getUltraPlayer(getPlayer()).canUse(getType());
        double maxCooldown = getOwner().isBypassingCooldown() ? getType().getRunTime() : getType().getCountdown();

        int res = (int) (currentCooldown / maxCooldown * 50);
        ChatColor color;
        for (int i = 0; i < 50; i++) {
            color = ChatColor.RED;
            if (i < 50 - res) {
                color = ChatColor.GREEN;
            }
            //stringBuilder.append(color + "┃");
            stringBuilder.append(color + "|");
        }

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator('.');
        otherSymbols.setPatternSeparator('.');
        final DecimalFormat decimalFormat = new DecimalFormat("0.0", otherSymbols);
        String timeLeft = decimalFormat.format(currentCooldown) + "s";

        UltraCosmeticsData.get().getVersionManager().getAncientUtil().sendActionBarMessage(getPlayer(),
                getType().getName() + ChatColor.WHITE + " " + stringBuilder.toString() + ChatColor.WHITE + " " + timeLeft);

    }

    /**
     * Removes the item.
     */
    public void removeItem() {
        itemStack = null;
        getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), null);
    }

    /**
     * Gets the price for each ammo purchase.
     *
     * @return the price for each ammo purchase.
     */
    private int getPrice() {
        return SettingsManager.getConfig().getInt("Gadgets." + getType().getConfigName() + ".Ammo.Price");
    }

    /**
     * Gets the ammo it should give after a purchase.
     *
     * @return the ammo it should give after a purchase.
     */
    private int getResultAmmoAmount() {
        return SettingsManager.getConfig().getInt("Gadgets." + getType().getConfigName() + ".Ammo.Result-Amount");
    }

    /**
     * Gets the gadget current Item Stack.
     *
     * @return
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Opens Ammo Purchase Menu.
     */
    public void openAmmoPurchaseMenu() {
        Inventory inventory = Bukkit.createInventory(new CosmeticsInventoryHolder(), 54, MessageManager.getMessage("Menus.Buy-Ammo"));

        inventory.setItem(13, ItemFactory.create(getType().getMaterial(),
                MessageManager.getMessage("Buy-Ammo-Description").replace("%amount%", "" + getResultAmmoAmount())
                        .replace("%price%", "" + getPrice()).replaceAll("%gadgetname%", getType().getName())));

        for (int i = 27; i < 30; i++) {
            inventory.setItem(i,
                    ItemFactory.create(XMaterial.EMERALD_BLOCK, MessageManager.getMessage("Purchase")));
            inventory.setItem(i + 9,
                    ItemFactory.create(XMaterial.EMERALD_BLOCK, MessageManager.getMessage("Purchase")));
            inventory.setItem(i + 18,
                    ItemFactory.create(XMaterial.EMERALD_BLOCK, MessageManager.getMessage("Purchase")));
            inventory.setItem(i + 6,
                    ItemFactory.create(XMaterial.REDSTONE_BLOCK, MessageManager.getMessage("Cancel")));
            inventory.setItem(i + 9 + 6,
                    ItemFactory.create(XMaterial.REDSTONE_BLOCK, MessageManager.getMessage("Cancel")));
            inventory.setItem(i + 18 + 6,
                    ItemFactory.create(XMaterial.REDSTONE_BLOCK, MessageManager.getMessage("Cancel")));
        }
        ItemFactory.fillInventory(inventory);
        getPlayer().openInventory(inventory);
        this.ammoInventory = inventory;
    }

    protected boolean checkRequirements(PlayerInteractEvent event) {
        return true;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() == getPlayer() && ammoInventory != null && InventoryUtils
                .areSame(event.getInventory(), ammoInventory)) {
            ammoInventory = null;
            openGadgetsInvAfterAmmo = false;
        }
    }

    @EventHandler
    public void onInventoryClickAmmo(final InventoryClickEvent event) {
        if (getOwner() != null && getPlayer() != null && event.getWhoClicked() == getPlayer() && ammoInventory != null
                && InventoryUtils.areSame(event.getWhoClicked().getOpenInventory().getTopInventory(), ammoInventory)) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem()
                    .getItemMeta().hasDisplayName()) {
                String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
                String purchase = MessageManager.getMessage("Purchase");
                String cancel = MessageManager.getMessage("Cancel");
                if (displayName.equals(purchase)) {
                    if (getUltraCosmetics().getPlayerManager().getUltraPlayer((Player) event.getWhoClicked())
                            .getBalance() >= getPrice()) {
                        getUltraCosmetics().getEconomyHandler().withdraw((Player) event.getWhoClicked(), getPrice());
                        getUltraCosmetics().getPlayerManager().getUltraPlayer((Player) event.getWhoClicked())
                                .addAmmo(getType(), getResultAmmoAmount());
                        event.getWhoClicked().sendMessage(MessageManager.getMessage("Successful-Purchase"));
                        if (openGadgetsInvAfterAmmo)
                            Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
                                getUltraCosmetics().getMenus().getGadgetsMenu().open(getOwner(), lastPage);
                                openGadgetsInvAfterAmmo = false;
                                lastPage = 1;
                            }, 1);
                    } else {
                        getPlayer().sendMessage(MessageManager.getMessage("Not-Enough-Money"));
                    }
                    event.getWhoClicked().closeInventory();
                } else if (displayName.equals(cancel)) {
                    event.getWhoClicked().closeInventory();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (getOwner() == null || getPlayer() == null || event.getPlayer() != getPlayer() || !(event
                .getRightClicked() instanceof ItemFrame) || getItemStack() == null || itemStack == null || !itemStack
                .hasItemMeta() || itemStack.getType() != getItemStack().getType()
                || itemStack.getData().getData() != getItemStack().getData().getData() || !itemStack.getItemMeta()
                .getDisplayName().endsWith(getType().getName())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        UltraPlayer ultraPlayer = getUltraCosmetics().getPlayerManager().getUltraPlayer(event.getPlayer());
        if (!uuid.equals(getOwnerUniqueId()))
            return;
        ItemStack itemStack = player.getItemInHand();
        if (itemStack.getType() != getType().getMaterial().parseMaterial())
            return; // TODO advanced checks
        if (player.getInventory().getHeldItemSlot() != (int) SettingsManager.getConfig().get("Gadget-Slot"))
            return;
        if (ultraPlayer != getOwner())
            return;
        if (event.getAction() == Action.PHYSICAL)
            return;
        if (UltraCosmeticsData.get().getServerVersion().offhandAvailable()) {
            if (event.getHand() != null && event.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }
        }
        event.setCancelled(true);
        player.updateInventory();
        if (!ultraPlayer.hasGadgetsEnabled()) {
            getPlayer().sendMessage(MessageManager.getMessage("Gadgets-Enabled-Needed"));
            return;
        }
        if (ultraPlayer.getCurrentTreasureChest() != null) {
            return;
        }

        if (UltraCosmeticsData.get().isAmmoEnabled() && getType().requiresAmmo()) {
            if (ultraPlayer.getAmmo(getType()) < 1) {
                openAmmoPurchaseMenu();
                return;
            }
        }
        if (!checkRequirements(event)) {
            return;
        }

        double coolDown = ultraPlayer.canUse(getType());
        if (coolDown != -1) {
            String timeLeft = new DecimalFormat("#.#").format(coolDown);
            if (getType().getCountdown() > 1)
                getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Countdown-Message")
                        .replace("%gadgetname%", TextUtil.filterPlaceHolder(getType().getName(), getUltraCosmetics()))
                        .replace("%time%", timeLeft));
            return;
        } else {
            ultraPlayer.setCoolDown(getType());
        }
        if (UltraCosmeticsData.get().isAmmoEnabled() && getType().requiresAmmo()) {
            ultraPlayer.removeAmmo(getType());
            itemStack = ItemFactory.create(getType().getMaterial(),
                    ChatColor.WHITE + "" + ChatColor.BOLD + ultraPlayer.getAmmo(getType())
                            + " " + getType().getName(), MessageManager.getMessage("Gadgets.Lore"));
            this.itemStack = itemStack;
            getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), itemStack);
        }
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() != Material.AIR)
            lastClickedBlock = event.getClickedBlock();
        if (asynchronous) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (useTwoInteractMethods) {
                        if (event.getAction() == Action.RIGHT_CLICK_AIR
                                || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                            onRightClick();
                        else if (event.getAction() == Action.LEFT_CLICK_BLOCK
                                || event.getAction() == Action.LEFT_CLICK_AIR)
                            onLeftClick();
                    } else {
                        onRightClick();
                    }
                }
            }.runTaskAsynchronously(getUltraCosmetics());
        } else {
            if (useTwoInteractMethods) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                    onRightClick();
                else if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR)
                    onLeftClick();
            } else {
                onRightClick();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(getItemStack())) {
            if (SettingsManager.getConfig().getBoolean("Remove-Gadget-With-Drop")) {
                getUltraCosmetics().getPlayerManager().getUltraPlayer(getPlayer()).removeGadget();
                event.getItemDrop().remove();
            } else {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Cancel players from removing, picking the item in their inventory.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void cancelMove(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (player != getPlayer()) return;
        if ((event.getCurrentItem() != null && event.getCurrentItem().equals(getItemStack()))
                || (event.getClick() == ClickType.NUMBER_KEY && getItemStack().equals(player.getInventory().getItem(event.getHotbarButton())))) {
        	event.setCancelled(true);
            player.updateInventory();
        }
    }

    /**
     * Cancel players from removing, picking the item in their inventory.
     *
     * @param event
     */
    @EventHandler
    public void cancelMove(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (player != getPlayer()) return;
        for (ItemStack item : event.getNewItems().values()) {
            if (item != null && item.equals(itemStack)) {
                event.setCancelled(true);
                player.updateInventory();
                player.closeInventory();
                return;
            }
        }
    }

    /**
     * Cancel players from removing, picking the item in their inventory.
     *
     * @param event
     */
    @EventHandler
    public void cancelMove(InventoryCreativeEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item != null && player == getPlayer() && item.equals(itemStack)) {
            event.setCancelled(true);
            player.closeInventory(); // Close the inventory because clicking again results in the event being handled client side
        }
    }

    protected void setAsynchronous(boolean asynchronous) {
        this.asynchronous = asynchronous;
    }

    public boolean isAsynchronous() {
        return asynchronous;
    }

    /**
     * If useTwoInteractMethods is true,
     * called when only a right click is called.
     * <p/>
     * Otherwise, called when a right or left click
     * is performed.
     */
    abstract void onRightClick();

    /**
     * Called when a left click is done with gadget,
     * only called if useTwoInteractMethods is true.
     */
    void onLeftClick() {};

    /**
     * Called when gadget is cleared.
     */
    @Override
    public abstract void onClear();

}
