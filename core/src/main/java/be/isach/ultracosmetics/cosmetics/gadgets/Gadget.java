package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Package: be.isach.ultracosmetics.cosmetics.gadgets
 * Created by: sacha
 * Date: 03/08/15
 * Project: UltraCosmetics
 *
 * Represents an instance of a Gadget summoned by a player.
 *
 */
public abstract class Gadget extends Cosmetic<GadgetType> implements Updatable {

    /**
     * If true, it will differentiate left and right click.
     */
    public boolean useTwoInteractMethods;

    /**
     * If it should open Gadget Menu after purchase.
     */
    public boolean openGadgetsInvAfterAmmo;

    /**
     * If true, will display cooldown left when fail on use
     * because cooldown active.
     */
    public boolean displayCooldownMessage = true;

    /**
     * Last Clicked Block by the player.
     */
    protected Block lastClickedBlock;

    /**
     * Gadget ItemStack.
     */
    protected ItemStack itemStack;

    /**
     * If Gadget interaction should tick asynchronously.
     */
    boolean asynchronous = false;

    /**
     * If true, it will affect players (velocity).
     */
    boolean affectPlayers;

    /**
     * The Ammo Purchase inventory.
     */
    private Inventory ammoInventory;

    /**
     * Page the user was on when trying to buy ammo.
     * Is used when player buys ammo from Gadget Menu.
     */
    public int lastPage = 1;

    public Gadget(UltraPlayer owner, GadgetType type, UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.GADGETS, owner, type);

        this.affectPlayers = type.affectPlayers();
        this.useTwoInteractMethods = false;

        if (getUcInstance().getPlayerManager().getUltraPlayer(getPlayer()).getCurrentGadget() != null) {
            getUcInstance().getPlayerManager().getUltraPlayer(getPlayer()).removeGadget();
        }

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator('.');
        otherSymbols.setPatternSeparator('.');
        final DecimalFormat decimalFormat = new DecimalFormat("0.0", otherSymbols);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (owner.getCurrentGadget() != null &&
                            owner.getCurrentGadget().getCosmeticType() == type) {
                        onUpdate();
                        if (UltraCosmeticsData.get().displaysCooldownInBar()) {
                            if (getPlayer().getItemInHand() != null
                                    && itemStack != null
                                    && getPlayer().getItemInHand().hasItemMeta()
                                    && getPlayer().getItemInHand().getItemMeta().hasDisplayName()
                                    && getPlayer().getItemInHand().getItemMeta().getDisplayName().contains(getCosmeticType().getName())
                                    && getUcInstance().getPlayerManager().getUltraPlayer(getPlayer()).canUse(type) != -1)
                                sendCooldownBar();
                            double left = getUcInstance().getPlayerManager().getUltraPlayer(getPlayer()).canUse(type);
                            if (left > -0.1) {
                                String leftRounded = decimalFormat.format(left);
                                double decimalRoundedValue = Double.parseDouble(leftRounded);
                                if (decimalRoundedValue == 0) {
                                    PlayerUtils.sendInActionBar(getPlayer(),
                                            MessageManager.getMessage("Gadgets.Gadget-Ready-ActionBar").
                                                    replace("%gadgetname%", TextUtil.filterPlaceHolder(getCosmeticType().getName(), ultraCosmetics)));
                                    SoundUtil.playSound(getPlayer(), Sounds.NOTE_STICKS, 1.4f, 1.5f);
                                }
                            }
                        }
                    } else {
                        cancel();
                        unregisterListeners();
                    }
                } catch (NullPointerException exc) {
                    removeItem();
                    onClear();
                    removeListener();
                    getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Unequip").replace("%gadgetname%", TextUtil.filterPlaceHolder(getCosmeticType().getName(), getUcInstance())));
                    cancel();
                }
            }
        };
        runnable.runTaskTimerAsynchronously(getUcInstance(), 0, 1);

        if (getPlayer().getInventory().getItem((int) SettingsManager.getConfig().get("Gadget-Slot")) != null) {
            getPlayer().getWorld().dropItem(getPlayer().getLocation(), getPlayer().getInventory().getItem((int) SettingsManager.getConfig().get("Gadget-Slot")));
            getPlayer().getInventory().remove((int) SettingsManager.getConfig().get("Gadget-Slot"));
        }

        String d = UltraCosmeticsData.get().isAmmoEnabled() && getCosmeticType().requiresAmmo() ?
                "§f§l" + getUcInstance().getPlayerManager().getUltraPlayer(getPlayer()).getAmmo(type.toString().toLowerCase()) + " "
                : "";
        itemStack = ItemFactory.create(type.getMaterial(), type.getData(), d + getCosmeticType().getName(), MessageManager.getMessage("Gadgets.Lore"));
        getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), itemStack);
        getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Equip").replace("%gadgetname%", TextUtil.filterPlaceHolder(getCosmeticType().getName(), getUcInstance())));

        getUcInstance().getPlayerManager().getUltraPlayer(getPlayer()).setCurrentGadget(this);
    }

    /**
     * Unregister Listener.
     */
    public void removeListener() {
        HandlerList.unregisterAll(this);
    }

    /**
     * Sends the current cooldown in action bar.
     */
    private void sendCooldownBar() {
        if (getPlayer() == null) return;

        StringBuilder stringBuilder = new StringBuilder();

        double currentCooldown = getUcInstance().getPlayerManager().getUltraPlayer(getPlayer()).canUse(getCosmeticType());
        double maxCooldown = getCosmeticType().getCountdown();

        int res = (int) (currentCooldown / maxCooldown * 10);
        ChatColor color;
        for (int i = 0; i < 10; i++) {
            color = ChatColor.RED;
            if (i < 10 - res)
                color = ChatColor.GREEN;
            stringBuilder.append(color + "█");
        }

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator('.');
        otherSymbols.setPatternSeparator('.');
        final DecimalFormat decimalFormat = new DecimalFormat("0.0", otherSymbols);
        String timeLeft = decimalFormat.format(currentCooldown) + "s";

        PlayerUtils.sendInActionBar(getPlayer(),
                getCosmeticType().getName() + " §f" + stringBuilder.toString() + " §f" + timeLeft);

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
    abstract void onLeftClick();

    /**
     * Called when gadget is cleared.
     */
    public abstract void onClear();

    /**
     * unregister listeners.
     */
    public void unregisterListeners() {
        try {
            HandlerList.unregisterAll(this);
        } catch (Exception exc) {
        }
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
    public int getPrice() {
        return SettingsManager.getConfig().getInt("Gadgets." + getCosmeticType().getConfigName() + ".Ammo.Price");
    }

    /**
     * Gets the ammo it should give after a purchase.
     *
     * @return the ammo it should give after a purchase.
     */
    public int getResultAmmoAmount() {
        return SettingsManager.getConfig().getInt("Gadgets." + getCosmeticType().getConfigName() + ".Ammo.Result-Amount");
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

        Inventory inventory = Bukkit.createInventory(null, 54, MessageManager.getMessage("Menus.Buy-Ammo"));

        inventory.setItem(13, ItemFactory.create(getCosmeticType().getMaterial(), getCosmeticType().getData(), MessageManager.getMessage("Buy-Ammo-Description").replace("%amount%", "" + getResultAmmoAmount()).replace("%price%", "" + getPrice()).replaceAll("%gadgetname%", getCosmeticType().getName())));

        for (int i = 27; i < 30; i++) {
            inventory.setItem(i, ItemFactory.create(Material.EMERALD_BLOCK, (byte) 0x0, MessageManager.getMessage("Purchase")));
            inventory.setItem(i + 9, ItemFactory.create(Material.EMERALD_BLOCK, (byte) 0x0, MessageManager.getMessage("Purchase")));
            inventory.setItem(i + 18, ItemFactory.create(Material.EMERALD_BLOCK, (byte) 0x0, MessageManager.getMessage("Purchase")));
            inventory.setItem(i + 6, ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Cancel")));
            inventory.setItem(i + 9 + 6, ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Cancel")));
            inventory.setItem(i + 18 + 6, ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Cancel")));
        }
        ItemFactory.fillInventory(inventory);


        getPlayer().openInventory(inventory);

        this.ammoInventory = inventory;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() == getPlayer() && ammoInventory != null && isSameInventory(event.getInventory(), ammoInventory)) {
            ammoInventory = null;
            openGadgetsInvAfterAmmo = false;
        }
    }

    @EventHandler
    public void onInventoryClickAmmo(final InventoryClickEvent event) {
        if (event.getWhoClicked() == getPlayer() && ammoInventory != null && isSameInventory(event.getWhoClicked().getOpenInventory().getTopInventory(), ammoInventory)) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
                String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
                String purchase = MessageManager.getMessage("Purchase");
                String cancel = MessageManager.getMessage("Cancel");
                if (displayName.equals(purchase)) {
                    if (getUcInstance().getPlayerManager().getUltraPlayer((Player) event.getWhoClicked()).getBalance() >= getPrice()) {
                        getUcInstance().getEconomy().withdrawPlayer((Player) event.getWhoClicked(), getPrice());
                        getUcInstance().getPlayerManager().getUltraPlayer((Player) event.getWhoClicked()).addAmmo(getCosmeticType().toString().toLowerCase(), getResultAmmoAmount());
                        event.getWhoClicked().sendMessage(MessageManager.getMessage("Successful-Purchase"));
                        if (openGadgetsInvAfterAmmo)
                            Bukkit.getScheduler().runTaskLater(getUcInstance(), () -> {
//                                MenuGadgets_old.openMenu((Player) event.getWhoClicked(), lastPage);
                                // TODO Open Gadgets Menu at lastpage.
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

    public boolean isSameInventory(Inventory first, Inventory second) {
        return InventoryUtils.areSame(first, second);
    }

    @EventHandler
    protected void onPlayerInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        UltraPlayer ultraPlayer = getUcInstance().getPlayerManager().getUltraPlayer(event.getPlayer());
        if (!uuid.equals(getOwnerUniqueId())) return;
        ItemStack itemStack = player.getItemInHand();
        if (itemStack.getType() != getCosmeticType().getMaterial()) return;
        if (itemStack.getData().getData() != getCosmeticType().getData()) return;
        if (player.getInventory().getHeldItemSlot() != (int) SettingsManager.getConfig().get("Gadget-Slot")) return;
        if (ultraPlayer != getOwner()) return;
        if (event.getAction() == Action.PHYSICAL) return;
        event.setCancelled(true);
        player.updateInventory();
        if (!ultraPlayer.hasGadgetsEnabled()) {
            getPlayer().sendMessage(MessageManager.getMessage("Gadgets-Enabled-Needed"));
            return;
        }
        if (ultraPlayer.getCurrentTreasureChest() != null)
            return;

        if (UltraCosmeticsData.get().isAmmoEnabled() && getCosmeticType().requiresAmmo()) {
            if (ultraPlayer.getAmmo(getCosmeticType().toString().toLowerCase()) < 1) {
                openAmmoPurchaseMenu();
                return;
            }
        }
        if (getCosmeticType() == GadgetType.PORTALGUN) {
            if (getPlayer().getTargetBlock((Set<Material>) null, 20).getType() == Material.AIR) {
                getPlayer().sendMessage(MessageManager.getMessage("Gadgets.PortalGun.No-Block-Range"));
                return;
            }
        }
        if (getCosmeticType() == GadgetType.ROCKET) {
            Cuboid c = new Cuboid(getPlayer().getLocation().add(-1, 0, -1), getPlayer().getLocation().add(1, 75, 1));
            if (!c.isEmpty()) {
                getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-Enough-Space"));
                return;
            }
            if (!getPlayer().isOnGround()) {
                getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-On-Ground"));
                return;
            }
        }
        if (getCosmeticType() == GadgetType.DISCOBALL) {
            if (GadgetDiscoBall.DISCO_BALLS.size() > 0) {
                getPlayer().sendMessage(MessageManager.getMessage("Gadgets.DiscoBall.Already-Active"));
                return;
            }
            if (getPlayer().getLocation().add(0, 4, 0).getBlock() != null && getPlayer().getLocation().add(0, 4, 0).getBlock().getType() != Material.AIR) {
                getPlayer().sendMessage(MessageManager.getMessage("Gadgets.DiscoBall.Not-Space-Above"));
                return;
            }
        }
        if (getCosmeticType() == GadgetType.CHRISTMASTREE) {
            if (event.getClickedBlock() == null
                    || event.getClickedBlock().getType() == Material.AIR) {
                getPlayer().sendMessage(MessageManager.getMessage("Gadgets.ChristmasTree.Click-On-Block"));
                return;
            }
        }
        if (getCosmeticType() == GadgetType.TRAMPOLINE) {
            // Check BLOCKS above.
            Location loc1 = getPlayer().getLocation().add(2, 15, 2);
            Location loc2 = getPlayer().getLocation().clone().add(-2, 0, -2);
            Block block = loc1.getBlock().getRelative(3, 0, 0);
            Block block2 = loc1.getBlock().getRelative(3, 1, 0);
            Cuboid checkCuboid = new Cuboid(loc1, loc2);

            if (!checkCuboid.isEmpty()
                    || block.getType() != Material.AIR
                    || block2.getType() != Material.AIR) {
                getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-Enough-Space"));
                return;
            }
        }
        // Check for the parachute if there is space 30-40 BLOCKS above the player to avoid problems.
        if (getCosmeticType() == GadgetType.PARACHUTE) {
            // Check BLOCKS above.
            Location loc1 = getPlayer().getLocation().add(2, 28, 2);
            Location loc2 = getPlayer().getLocation().clone().add(-2, 40, -2);
            Cuboid checkCuboid = new Cuboid(loc1, loc2);

            if (!checkCuboid.isEmpty()) {
                getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-Enough-Space"));
                return;
            }
        }
        if (getCosmeticType() == GadgetType.EXPLOSIVESHEEP) {
            if (GadgetExplosiveSheep.EXPLOSIVE_SHEEP.size() > 0) {
                getPlayer().sendMessage(MessageManager.getMessage("Gadgets.ExplosiveSheep.Already-Active"));
                return;
            }
        }
        double coolDown = ultraPlayer.canUse(getCosmeticType());
        if (coolDown != -1) {
            String timeLeft = new DecimalFormat("#.#").format(coolDown);
            if (getCosmeticType().getCountdown() > 1)
                getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Countdown-Message").replace("%gadgetname%", TextUtil.filterPlaceHolder(getCosmeticType().getName(), getUcInstance())).replace("%time%", timeLeft));
            return;
        } else
            ultraPlayer.setCoolDown(getCosmeticType(), getCosmeticType().getCountdown());
        if (UltraCosmeticsData.get().isAmmoEnabled() && getCosmeticType().requiresAmmo()) {
            ultraPlayer.removeAmmo(getCosmeticType().toString().toLowerCase());
            itemStack = ItemFactory.create(getCosmeticType().getMaterial(), getCosmeticType().getData(), "§f§l" + ultraPlayer.getAmmo(getCosmeticType().toString().toLowerCase()) + " " + getCosmeticType().getName(), MessageManager.getMessage("Gadgets.Lore"));
            getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"), getItemStack());
        }
        if (event.getClickedBlock() != null
                && event.getClickedBlock().getType() != Material.AIR)
            lastClickedBlock = event.getClickedBlock();
        if (asynchronous) {
            Bukkit.getScheduler().runTaskAsynchronously(getUcInstance(), new BukkitRunnable() {
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
            });
        } else {
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

    }

    @EventHandler
    protected void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType() == getCosmeticType().getMaterial()
                && event.getItemDrop().getItemStack().getData().getData() == getCosmeticType().getData()
                && event.getItemDrop().getItemStack().getItemMeta().hasDisplayName()
                && event.getItemDrop().getItemStack().getItemMeta().getDisplayName().endsWith(getCosmeticType().getName())
                && SettingsManager.getConfig().getBoolean("Remove-Gadget-With-Drop")) {
            getUcInstance().getPlayerManager().getUltraPlayer(getPlayer()).removeGadget();
            event.getItemDrop().remove();
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
        if (player == getPlayer()
                && ((event.getCurrentItem() != null && event.getCurrentItem().equals(getItemStack())))
                || ((event.getCursor() != null && event.getCursor().equals(getItemStack())))) {
            if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT
                    || event.getClick() == ClickType.NUMBER_KEY || event.getClick() == ClickType.UNKNOWN) {
                event.setCancelled(true);
                player.updateInventory();
                return;
            }
            if (event.getCurrentItem() != null) {
                if (event.getCurrentItem().equals(itemStack)) {
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }
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
        for (ItemStack item : event.getNewItems().values()) {
            if (item != null
                    && player == getPlayer()
                    && item.equals(itemStack)) {
                event.setCancelled(true);
                ((Player) event.getWhoClicked()).updateInventory();
                return;
            }
        }
    }
}
