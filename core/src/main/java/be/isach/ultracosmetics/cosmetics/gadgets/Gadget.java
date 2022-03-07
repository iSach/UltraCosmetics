package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.menu.menus.MenuPurchase;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

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

    public Gadget(UltraPlayer owner, GadgetType type, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.GADGETS, owner, type);

        this.affectPlayers = type.affectPlayersEnabled();
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
                        ItemStack hand = getPlayer().getItemInHand();
                        // TODO: this is ugly
                        if (hand != null && itemStack != null && hand.hasItemMeta() && hand.getType() == getItemStack().getType()
                                && hand.getItemMeta().hasDisplayName() && hand.getItemMeta().getDisplayName().endsWith(getType().getName())
                                && !getUltraCosmetics().getPlayerManager().getUltraPlayer(getPlayer()).canUse(getType())) {
                            sendCooldownBar();
                        }
                    }
                } catch (NullPointerException ignored) {
                    // Caused by rapid item switching in inventory.
                }
                if (getOwner() == null || getPlayer() == null) {
                    return;
                }

                double left = getUltraCosmetics().getPlayerManager().getUltraPlayer(getPlayer()).getCooldown(getType());
                if (left > 0) {
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

        StringBuilder stringBuilder = new StringBuilder(ChatColor.GREEN.toString());

        double currentCooldown = getUltraCosmetics().getPlayerManager().getUltraPlayer(getPlayer()).getCooldown(getType());
        double maxCooldown = getOwner().isBypassingCooldown() ? getType().getRunTime() : getType().getCountdown();

        int res = (int) (currentCooldown / maxCooldown * 50);
        for (int i = 0; i < 50; i++) {
            if (i == 50 - res) {
                stringBuilder.append(ChatColor.RED);
            }
            //stringBuilder.append("┃");
            stringBuilder.append("|");
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
    private int getAmmoPrice() {
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
     * TODO: I don't feel like this is a good place for this.
     */
    public void openAmmoPurchaseMenu() {
        String itemName = MessageManager.getMessage("Buy-Ammo-Description");
        itemName = itemName.replace("%amount%", String.valueOf(getResultAmmoAmount()));
        itemName = itemName.replace("%price%", String.valueOf(getAmmoPrice()));
        itemName = itemName.replace("%gadgetname%", getType().getName());
        ItemStack display = ItemFactory.create(getType().getMaterial(), itemName);
        PurchaseData pd = new PurchaseData();
        pd.setPrice(getAmmoPrice());
        pd.setShowcaseItem(display);
        pd.setOnPurchase(() -> {
            getOwner().addAmmo(getType(), getResultAmmoAmount());
            Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> {
                getUltraCosmetics().getMenus().getGadgetsMenu().open(getOwner(), lastPage);
                lastPage = 1;
            }, 1);
        });
        MenuPurchase mp = new MenuPurchase(getUltraCosmetics(), MessageManager.getMessage("Menus.Buy-Ammo"), pd);
        getPlayer().openInventory(mp.getInventory(getOwner()));
    }

    protected boolean checkRequirements(PlayerInteractEvent event) {
        return true;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (getOwner() == null || getPlayer() == null || event.getPlayer() != getPlayer() || !(event
                .getRightClicked() instanceof ItemFrame) || getItemStack() == null || itemStack == null || !itemStack
                .hasItemMeta() || itemStack.getType() != getItemStack().getType()
                || !itemStack.getItemMeta().getDisplayName().endsWith(getType().getName())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player != getPlayer()) return;
        if (event.getAction() == Action.PHYSICAL)
            return;
        ItemStack itemStack = player.getItemInHand();
        if (itemStack.getType() != getType().getMaterial().parseMaterial())
            return;
        if (player.getInventory().getHeldItemSlot() != SettingsManager.getConfig().getInt("Gadget-Slot"))
            return;
        if (UltraCosmeticsData.get().getServerVersion().offhandAvailable()) {
            if (event.getHand() != EquipmentSlot.HAND) {
                return;
            }
        }
        event.setCancelled(true);
        player.updateInventory();
        UltraPlayer ultraPlayer = getUltraCosmetics().getPlayerManager().getUltraPlayer(event.getPlayer());
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

        double coolDown = ultraPlayer.getCooldown(getType());
        if (coolDown > 0) {
            String timeLeft = new DecimalFormat("#.#").format(coolDown);
            if (getType().getCountdown() > 1)
                getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Countdown-Message")
                        .replace("%gadgetname%", TextUtil.filterPlaceHolder(getType().getName(), getUltraCosmetics()))
                        .replace("%time%", timeLeft));
            return;
        }
        ultraPlayer.setCoolDown(getType());
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
