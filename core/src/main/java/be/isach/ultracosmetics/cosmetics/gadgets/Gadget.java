package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.TextUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.ActionBar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Represents an instance of a Gadget summoned by a player.
 *
 * @author iSach
 * @since 08-03-2015
 */
public abstract class Gadget extends Cosmetic<GadgetType> {

    private static final DecimalFormatSymbols OTHER_SYMBOLS = new DecimalFormatSymbols(Locale.US);
    private static final DecimalFormat DECIMAL_FORMAT;

    static {
        OTHER_SYMBOLS.setDecimalSeparator('.');
        OTHER_SYMBOLS.setGroupingSeparator('.');
        OTHER_SYMBOLS.setPatternSeparator('.');
        DECIMAL_FORMAT = new DecimalFormat("0.0", OTHER_SYMBOLS);
    }

    /**
     * Gadget ItemStack.
     */
    protected ItemStack itemStack;

    /**
     * If true, will display cooldown left when fail on use because cooldown active.
     */
    protected boolean displayCooldownMessage = true;

    /**
     * Last Clicked Block by the player.
     */
    protected Block lastClickedBlock;

    /**
     * If Gadget interaction should tick asynchronously.
     */
    private boolean asynchronous = false;

    // Cache the actual material value so we don't have to keep calling parseMaterial
    private final Material material;

    public Gadget(UltraPlayer owner, GadgetType type, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.GADGETS, owner, type);
        material = type.getMaterial().parseMaterial();
    }

    @Override
    protected void onEquip() {
        int slot = SettingsManager.getConfig().getInt("Gadget-Slot");
        if (getPlayer().getInventory().getItem(slot) != null) {
            getPlayer().getWorld().dropItem(getPlayer().getLocation(), getPlayer().getInventory().getItem(slot));
            getPlayer().getInventory().setItem(slot, null);
        }

        String ammo = "";
        if (UltraCosmeticsData.get().isAmmoEnabled() && getType().requiresAmmo()) {
            ammo = ChatColor.WHITE + "" + ChatColor.BOLD + getOwner().getAmmo(getType()) + " ";
        }

        itemStack = ItemFactory.create(getType().getMaterial(), ammo + getType().getName(),
                MessageManager.getMessage("Gadgets.Lore"));
        getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), itemStack);
    }

    @Override
    public void run() {
        if (getOwner() == null || getPlayer() == null) return;

        if (getOwner().getCurrentGadget() != null && getOwner().getCurrentGadget().getType() == getType()) {
            ((Updatable) this).onUpdate();
            try {
                if (UltraCosmeticsData.get().displaysCooldownInBar()) {
                    @SuppressWarnings("deprecation")
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
            if (getOwner() == null || getPlayer() == null) return;

            double left = getUltraCosmetics().getPlayerManager().getUltraPlayer(getPlayer()).getCooldown(getType());
            if (left > 0) {
                String leftRounded = DECIMAL_FORMAT.format(left);
                double decimalRoundedValue = Double.parseDouble(leftRounded);
                if (decimalRoundedValue == 0) {
                    String message = MessageManager.getMessage("Gadgets.Gadget-Ready-ActionBar");
                    message = message.replace("%gadgetname%",
                            TextUtil.filterPlaceHolder(getType().getName()));
                    ActionBar.sendActionBar(getPlayer(), message);
                    play(XSound.BLOCK_NOTE_BLOCK_HAT, getPlayer(), 1.4f, 1.5f);
                }
            }
        } else {
            clear();
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
        if (getOwner() == null || getPlayer() == null) return;

        StringBuilder stringBuilder = new StringBuilder(ChatColor.GREEN.toString());

        double currentCooldown = getUltraCosmetics().getPlayerManager().getUltraPlayer(getPlayer()).getCooldown(getType());
        double maxCooldown = getOwner().isBypassingCooldown() ? getType().getRunTime() : getType().getCountdown();

        int res = (int) (currentCooldown / maxCooldown * 50);
        for (int i = 0; i < 50; i++) {
            if (i == 50 - res) {
                stringBuilder.append(ChatColor.RED);
            }
            stringBuilder.append("|");
        }

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator('.');
        otherSymbols.setPatternSeparator('.');
        final DecimalFormat decimalFormat = new DecimalFormat("0.0", otherSymbols);
        String timeLeft = decimalFormat.format(currentCooldown) + "s";

        ActionBar.sendActionBar(getPlayer(), getType().getName() + ChatColor.WHITE + " " + stringBuilder.toString() + ChatColor.WHITE + " " + timeLeft);

    }

    /**
     * Removes the item.
     */
    public void removeItem() {
        itemStack = null;
        getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), null);
    }

    /**
     * Gets the gadget current Item Stack.
     *
     * @return
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    protected boolean checkRequirements(PlayerInteractEvent event) {
        return true;
    }

    protected void play(XSound sound, Entity entity, float volume, float pitch) {
        if (!SettingsManager.getConfig().getBoolean("Gadgets-Are-Silent")) {
            sound.play(entity, volume, pitch);
        }
    }

    protected void play(XSound sound, Location loc, float volume, float pitch) {
        if (!SettingsManager.getConfig().getBoolean("Gadgets-Are-Silent")) {
            sound.play(loc, volume, pitch);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (getOwner() == null || getPlayer() == null || event.getPlayer() != getPlayer()
                || !(event.getRightClicked() instanceof ItemFrame) || getItemStack() == null
                || itemStack == null || !itemStack.hasItemMeta()
                || itemStack.getType() != getItemStack().getType()
                || !itemStack.getItemMeta().getDisplayName().endsWith(getType().getName())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) return;
        Player player = event.getPlayer();
        if (player != getPlayer()) return;
        @SuppressWarnings("deprecation")
        ItemStack itemStack = player.getItemInHand();
        if (itemStack.getType() != material) return;
        if (player.getInventory().getHeldItemSlot() != SettingsManager.getConfig().getInt("Gadget-Slot")) return;
        if (UltraCosmeticsData.get().getServerVersion().offhandAvailable()) {
            if (event.getHand() != EquipmentSlot.HAND) return;
        }
        event.setCancelled(true);
        // player.updateInventory();
        UltraPlayer ultraPlayer = getUltraCosmetics().getPlayerManager().getUltraPlayer(event.getPlayer());

        if (ultraPlayer.getCurrentTreasureChest() != null) return;

        if (player.hasMetadata("vanished") && SettingsManager.getConfig().getBoolean("Prevent-Cosmetics-In-Vanish")) {
            getOwner().clear();
            getPlayer().sendMessage(MessageManager.getMessage("Not-Allowed-In-Vanish"));
            return;
        }

        if (!ultraPlayer.hasGadgetsEnabled()) {
            getPlayer().sendMessage(MessageManager.getMessage("Gadgets-Enabled-Needed"));
            return;
        }

        if (UltraCosmeticsData.get().isAmmoEnabled() && getType().requiresAmmo()) {
            if (ultraPlayer.getAmmo(getType()) < 1) {
                UltraCosmeticsData.get().getPlugin().getMenus().openAmmoPurchaseMenu(getType(), getOwner());
                return;
            }
        }

        if (!checkRequirements(event)) return;

        double coolDown = ultraPlayer.getCooldown(getType());
        if (coolDown > 0) {
            String timeLeft = new DecimalFormat("#.#").format(coolDown);
            if (getType().getCountdown() > 1) {
                getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Countdown-Message")
                        .replace("%gadgetname%", TextUtil.filterPlaceHolder(getType().getName()))
                        .replace("%time%", timeLeft));
            }
            return;
        }
        ultraPlayer.setCoolDown(getType());
        if (UltraCosmeticsData.get().isAmmoEnabled() && getType().requiresAmmo()) {
            ultraPlayer.removeAmmo(getType());
            itemStack = ItemFactory.create(getType().getMaterial(),
                    ChatColor.WHITE + "" + ChatColor.BOLD + ultraPlayer.getAmmo(getType()) + " " + getType().getName(),
                    MessageManager.getMessage("Gadgets.Lore"));
            this.itemStack = itemStack;
            getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), itemStack);
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            lastClickedBlock = event.getClickedBlock();
        }
        boolean isLeft = event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK;
        Runnable callClick = () -> {
            if (isLeft) {
                onLeftClick();
            } else {
                onRightClick();
            }
        };
        if (asynchronous) {
            Bukkit.getScheduler().runTaskAsynchronously(getUltraCosmetics(), callClick);
        } else {
            callClick.run();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(getItemStack())) {
            if (SettingsManager.getConfig().getBoolean("Remove-Gadget-With-Drop")) {
                clear();
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
            player.closeInventory(); // Close the inventory because clicking again results in the event being handled
                                     // client side
        }
    }

    protected void setAsynchronous(boolean asynchronous) {
        this.asynchronous = asynchronous;
    }

    public boolean isAsynchronous() {
        return asynchronous;
    }

    /**
     * Called when a right-click is performed, and potentially when a left-click
     * is performed, depending on the implementation of onLeftClick()
     */
    protected abstract void onRightClick();

    /**
     * Called when a left click is done with gadget.
     */
    protected void onLeftClick() {
        onRightClick();
    }

}
