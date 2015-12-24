package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.CustomPlayer;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Created by Sacha on 20/12/15.
 */
public class Suit {

    /**
     * Suit Owner.
     */
    private UUID owner;

    /**
     * Armor Slot of the Suit.
     */
    private ArmorSlot armorSlot;

    /**
     * Type of the Suit.
     */
    private SuitType suitType;

    /**
     * ItemStack of the Suit.
     */
    protected ItemStack itemStack;

    public Suit(final UUID owner, ArmorSlot armorSlot, SuitType suitType) {
        this.owner = owner;
        this.armorSlot = armorSlot;
        this.suitType = suitType;

        if (owner == null) return;

        if (getCustomPlayer().currentHat != null
                && armorSlot == ArmorSlot.HELMET)
            getCustomPlayer().removeHat();

        getCustomPlayer().removeSuit(getArmorSlot());

        switch (getArmorSlot()) {
            case HELMET:
                if (getCustomPlayer().currentHat != null)
                    getCustomPlayer().removeHat();
                if (getPlayer().getInventory().getHelmet() != null) {
                    ItemStack itemStack = getPlayer().getInventory().getHelmet();
                    drop(itemStack);
                    getPlayer().getInventory().setHelmet(null);
                }
                getPlayer().getInventory().setHelmet(ItemFactory.create(getType().getHelmet(), (byte) 0, getType().getName(getArmorSlot()), "", "§9Suit Part"));
                itemStack = getPlayer().getInventory().getHelmet();
                getCustomPlayer().currentHelmet = this;
                break;
            case CHESTPLATE:
                if (getPlayer().getInventory().getChestplate() != null) {
                    ItemStack itemStack = getPlayer().getInventory().getChestplate();
                    drop(itemStack);
                    getPlayer().getInventory().setChestplate(null);
                }
                getPlayer().getInventory().setChestplate(ItemFactory.create(getType().getChestplate(), (byte) 0, getType().getName(getArmorSlot()), "", "§9Suit Part"));
                itemStack = getPlayer().getInventory().getChestplate();
                getCustomPlayer().currentChestplate = this;
                break;
            case LEGGINGS:
                if (getPlayer().getInventory().getLeggings() != null) {
                    ItemStack itemStack = getPlayer().getInventory().getLeggings();
                    drop(itemStack);
                    getPlayer().getInventory().setLeggings(null);
                }
                getPlayer().getInventory().setLeggings(ItemFactory.create(getType().getLeggings(), (byte) 0, getType().getName(getArmorSlot()), "", "§9Suit Part"));
                itemStack = getPlayer().getInventory().getLeggings();
                getCustomPlayer().currentLeggings = this;
                break;
            case BOOTS:
                if (getPlayer().getInventory().getBoots() != null) {
                    ItemStack itemStack = getPlayer().getInventory().getBoots();
                    drop(itemStack);
                    getPlayer().getInventory().setBoots(null);
                }
                getPlayer().getInventory().setBoots(ItemFactory.create(getType().getBoots(), (byte) 0, getType().getName(getArmorSlot()), "", "§9Suit Part"));
                itemStack = getPlayer().getInventory().getBoots();
                getCustomPlayer().currentBoots = this;
                break;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getOwner() == null
                        || getPlayer() == null) {
                    cancel();
                    return;
                }
                onUpdate();
            }
        }.runTaskTimerAsynchronously(Core.getPlugin(), 0, 1);

        getPlayer().sendMessage(MessageManager.getMessage("Suits.Equip").replace("%suitname%", (Core.placeHolderColor)
                ? getType().getName(getArmorSlot()) : Core.filterColor(getType().getName(getArmorSlot()))));
    }

    /**
     * Gets the owner as a UUID.
     *
     * @return The owner as a UUID.
     */
    public UUID getOwner() {
        return owner;
    }

    /**
     * Gets the owner as a Player.
     *
     * @return The owner as a player.
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(owner);
    }

    /**
     * Gets the CustomPlayer of the Owner.
     *
     * @return The CustomPlayer of the Owner.
     */
    public CustomPlayer getCustomPlayer() {
        return Core.getPlayerManager().getCustomPlayer(getPlayer());
    }

    /**
     * Clears the Suit.
     */
    public void clear() {
        switch (getArmorSlot()) {
            case HELMET:
                if (getCustomPlayer().currentHat != null)
                    getCustomPlayer().removeHat();
                getPlayer().getInventory().setHelmet(null);
                getCustomPlayer().currentHelmet = null;
                break;
            case CHESTPLATE:
                getPlayer().getInventory().setChestplate(null);
                getCustomPlayer().currentChestplate = null;
                break;
            case LEGGINGS:
                getPlayer().getInventory().setLeggings(null);
                getCustomPlayer().currentLeggings = null;
                break;
            case BOOTS:
                getPlayer().getInventory().setBoots(null);
                getCustomPlayer().currentBoots = null;
                break;
        }
        if (getPlayer() != null)
            getPlayer().sendMessage(MessageManager.getMessage("Suits.Unequip").replace("%suitname%", (Core.placeHolderColor)
                    ? getType().getName(getArmorSlot()) : Core.filterColor(getType().getName(getArmorSlot()))));
        owner = null;
        armorSlot = null;
        suitType = null;
        itemStack = null;
    }

    /**
     * The Suit ItemStack.
     *
     * @return The Suit ItemStack.
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Called each tick while suit active, async.
     */
    protected void onUpdate() {
    }

    /**
     * Get Suit Armor Slot.
     *
     * @return Suit Armor Slot.
     */
    public ArmorSlot getArmorSlot() {
        return armorSlot;
    }

    /**
     * Get Suit Type.
     *
     * @return Suit Type.
     */
    public SuitType getType() {
        return suitType;
    }

    /**
     * Drops an Item.
     *
     * @param itemStack The item to drop.
     */
    private void drop(ItemStack itemStack) {
        getPlayer().getWorld().dropItem(getPlayer().getLocation(), itemStack);
    }
}
