package be.isach.ultracosmetics.player;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.emotes.Emote;
import be.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import be.isach.ultracosmetics.cosmetics.hats.Hat;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.cosmetics.morphs.Morph;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffect;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.suits.Suit;
import be.isach.ultracosmetics.treasurechests.TreasureChest;
import be.isach.ultracosmetics.mysql.MySqlConnectionManager;
import be.isach.ultracosmetics.util.CacheValue;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.TextUtil;
import me.libraryaddict.disguise.DisguiseAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

/**
 * Package: be.isach.ultracosmetics
 * Created by: sacha
 * Date: 03/08/15
 * Project: UltraCosmetics
 * <p>
 * Description: Represents a player on the server.
 */
public class UltraPlayer {

    /**
     * Player UUID.
     */
    public UUID uuid;

    /**
     * Current Cosmetics.
     */
    private Gadget currentGadget;
    private Mount currentMount;
    private ParticleEffect currentParticleEffect;
    private Pet currentPet;
    private TreasureChest currentTreasureChest;
    private Morph currentMorph;
    private Hat currentHat;
    private Suit currentHelmet;
    private Suit currentChestplate;
    private Suit currentLeggings;
    private Suit currentBoots;
    private Emote currentEmote;

    /**
     * boolean to identify if player is loaded correctly
     */
    public boolean isLoaded = false;

    /**
     * Cooldown map storing all the current cooldowns for gadgets.
     */
    private HashMap<GadgetType, Long> gadgetCooldowns = null;

    /**
     * MySql Cache.
     */
    private CacheValue gadgetsEnabledCache = CacheValue.UNLOADED;
    private CacheValue morphSelfViewCache = CacheValue.UNLOADED;

    /**
     * MySql Index.
     */
    public int mySqlIndex = -1;

    private UltraCosmetics ultraCosmetics;

    /**
     * Allows to store custom data for each player easily.
     * <p/>
     * Created on join, and deleted on quit.
     *
     * @param uuid The player UUID.
     */
    public UltraPlayer(UUID uuid, UltraCosmetics ultraCosmetics) {
        try {
            this.uuid = uuid;

            gadgetCooldowns = new HashMap<>();

            if (UltraCosmeticsData.get().usingFileStorage())
                SettingsManager.getData(getPlayer()).addDefault("Keys", 0);

            if (UltraCosmeticsData.get().isAmmoEnabled()) {
                if (!UltraCosmeticsData.get().usingFileStorage())
                    ultraCosmetics.getMySqlConnectionManager().getSqlUtils().initStats(this);
                else {
                    GadgetType.values().stream().filter(GadgetType::isEnabled).forEachOrdered(type -> SettingsManager.getData(getPlayer()).addDefault("Ammo." + type.toString().toLowerCase(), 0));
                }
            }
            if (UltraCosmeticsData.get().usingFileStorage()) {
                SettingsManager.getData(getPlayer()).addDefault("Gadgets-Enabled", true);
                SettingsManager.getData(getPlayer()).addDefault("Third-Person-Morph-View", true);
            }


        } catch (Exception exc) {
            // Player couldn't be found.
            System.out.println("UltraCosmetics ERR -> " + "Couldn't find player with UUID: " + uuid);
            isLoaded = false;
            return;
        }
        // sql loader thread add player to pre-load
        if (!UltraCosmeticsData.get().usingFileStorage()) {
            try {
                ultraCosmetics.getMySqlConnectionManager().getSqlLoader().addPreloadPlayer(uuid);
            } catch (Exception e) {
                System.out.println("UltraCosmetics ERR -> " + "SQLLoader Fails to preload UUID: " + uuid);
            }
        } else {
            isLoaded = true;
        }

    }

    /**
     * Checks if a player can use a given gadget type.
     *
     * @param gadget The gadget type.
     * @return -1 if player can use, otherwise the time left (in seconds).
     */
    public double canUse(GadgetType gadget) {
        Object count = gadgetCooldowns.get(gadget);

        if (count == null || System.currentTimeMillis() > (long) count) {
            return -1;
        }

        double valueMillis = (long) count - System.currentTimeMillis();
        return valueMillis / 1000d;
    }

    /**
     * Sets the cooldown of a gadget.
     *
     * @param gadget    The gadget.
     * @param countdown The cooldown to set.
     */
    public void setCoolDown(GadgetType gadget, double countdown) {
        gadgetCooldowns.put(gadget, (long) (countdown * 1000 + System.currentTimeMillis()));
    }

    /**
     * Get the player owning the UltraPlayer.
     *
     * @return The player owning the UltraPlayer.
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Removes the current gadget.
     */
    public void removeGadget() {
        if (currentGadget != null) {
            if (getPlayer() != null) {
//                getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Unequip").replace("%gadgetname%", (UltraCosmeticsData.get().placeholdersHaveColor()) ? currentGadget.getName() : UltraCosmetics.filterColor(currentGadget.getName())));
            }
            currentGadget.removeItem();
            currentGadget.onClear();
            currentGadget.removeListener();
            currentGadget.unregisterListeners();
            currentGadget = null;
        }
    }

    /**
     * Removes the current emote.
     */
    public void removeEmote() {
        if (currentEmote != null) {
            if (getPlayer() != null)
                getPlayer().sendMessage(MessageManager.getMessage("Emotes.Unequip")
                        .replace("%emotename%", TextUtil.filterPlaceHolder(currentEmote.
                                getType().getName(), ultraCosmetics)));
            currentEmote.clear();
            currentEmote = null;
        }
    }


    /**
     * Removes the current Mount.
     */
    public void removeMount() {
        if (currentMount != null) {
            currentMount.clear();
            currentMount = null;
            getPlayer().removePotionEffect(PotionEffectType.CONFUSION);
        }
    }

    /**
     * Removes the current Pet.
     */
    public void removePet() {
        if (currentPet != null) {
            if (currentPet.armorStand != null)
                currentPet.armorStand.remove();
            currentPet.items.forEach(Entity::remove);
            currentPet.clear();
            currentPet = null;
        }
    }

    /**
     * Gives a key to the player.
     */
    public void addKey() {
        if (UltraCosmeticsData.get().usingFileStorage()) {
            SettingsManager.getData(getPlayer()).set("Keys", getKeys() + 1);
        } else {
            ultraCosmetics.getMySqlConnectionManager().getSqlUtils().addKey(getMySqlIndex());
        }
    }

    /**
     * Removes a key to the player.
     */
    public void removeKey() {
        if (UltraCosmeticsData.get().usingFileStorage())
            SettingsManager.getData(getPlayer()).set("Keys", getKeys() - 1);
        else
            ultraCosmetics.getMySqlConnectionManager().getSqlUtils().removeKey(getMySqlIndex());
    }

    /**
     * @return The amount of keys that the player owns.
     */
    public int getKeys() {
        return UltraCosmeticsData.get().usingFileStorage() ? (int) SettingsManager.getData(getPlayer()).get("Keys") : ultraCosmetics.getMySqlConnectionManager().getSqlUtils().getKeys(getMySqlIndex());
    }

    /**
     * Removes the current hat.
     */
    public void removeHat() {
        if (currentHat == null) return;
        getPlayer().getInventory().setHelmet(null);

        getPlayer().sendMessage(MessageManager.getMessage("Hats.Unequip")
                .replace("%hatname%", TextUtil.filterPlaceHolder(currentHat.getType().getName(), ultraCosmetics)));
        currentHat = null;
    }

    /**
     * Removes the current suit of armorSlot.
     *
     * @param armorSlot The ArmorSlot to remove.
     */
    public void removeSuit(ArmorSlot armorSlot) {
        switch (armorSlot) {
            case HELMET:
                if (currentHelmet != null)
                    currentHelmet.clear();
                break;
            case CHESTPLATE:
                if (currentChestplate != null)
                    currentChestplate.clear();
                break;
            case LEGGINGS:
                if (currentLeggings != null)
                    currentLeggings.clear();
                break;
            case BOOTS:
                if (currentBoots != null)
                    currentBoots.clear();
                break;
        }
    }

    public double getBalance() {
        try {
            if (ultraCosmetics.getEconomy() != null)
                return ultraCosmetics.getEconomy().getBalance(getPlayer());
        } catch (Exception exc) {
            ultraCosmetics.getSmartLogger().write("Error happened while getting a player's balance.");
            return 0;
        }
        return 0;
    }

    public boolean hasPermission(String permission) {
        return getPlayer().hasPermission(permission);
    }

    /**
     * @param armorSlot The armorslot to get.
     * @return The Suit from the armor slot.
     */
    public Suit getSuit(ArmorSlot armorSlot) {
        switch (armorSlot) {
            case HELMET:
                return currentHelmet;
            case CHESTPLATE:
                return currentChestplate;
            case LEGGINGS:
                return currentLeggings;
            case BOOTS:
                return currentBoots;
        }
        return null;
    }

    /**
     * Removes entire suit.
     */
    public void removeSuit() {
        for (ArmorSlot armorSlot : ArmorSlot.values())
            removeSuit(armorSlot);
    }

    /**
     * Sets current hat.
     *
     * @param hat The new hat.
     */
    public void setHat(Hat hat) {

        removeHat();

        if (getPlayer().getInventory().getHelmet() != null) {
            getPlayer().sendMessage(MessageManager.getMessage("Hats.Must-Remove-HatType"));
            return;
        }

        getPlayer().getInventory().setHelmet(hat.getItemStack());

        getPlayer().sendMessage(MessageManager.getMessage("Hats.Equip")
                .replace("%hatname%", TextUtil.filterPlaceHolder(hat.getType().getName(), ultraCosmetics)));
        currentHat = hat;
    }

    /**
     * Sets Emote.
     *
     * @param emote new Emote.
     */
    public void setEmote(Emote emote) {
        getPlayer().sendMessage(MessageManager.getMessage("Emotes.Equip")
                .replace("%emotename%", TextUtil.filterPlaceHolder(emote.getType().getName(), ultraCosmetics)));
        currentEmote = emote;
    }

    /**
     * Clears all gadgets.
     */
    public boolean clear() {
        boolean toReturn = currentGadget != null
                || currentParticleEffect != null
                || currentPet != null
                || currentMount != null
                || currentTreasureChest != null
                || currentHat != null
                || currentEmote != null;
        if (Category.MORPHS.isEnabled() && Bukkit.getPluginManager().isPluginEnabled("LibsDisguises")) {
            removeMorph();
            try {
                DisguiseAPI.undisguiseToAll(getPlayer());
            } catch (Exception e) {
            }
        }
        removeGadget();
        removeParticleEffect();
        removePet();
        removeMount();
        removeTreasureChest();
        removeHat();
        removeEmote();
        for (ArmorSlot armorSlot : ArmorSlot.values())
            removeSuit(armorSlot);
        return toReturn;
    }

    /**
     * Opens the Key Purchase Menu.
     */
    public void openKeyPurchaseMenu() {
        if (ultraCosmetics.getEconomy() == null) {
            return;
        }

        try {
            final Inventory inventory = Bukkit.createInventory(null, 54, MessageManager.getMessage("Buy-Treasure-Key"));

            for (int i = 27; i < 30; i++) {
                inventory.setItem(i, ItemFactory.create(Material.EMERALD_BLOCK, (byte) 0x0, MessageManager.getMessage("Purchase")));
                inventory.setItem(i + 9, ItemFactory.create(Material.EMERALD_BLOCK, (byte) 0x0, MessageManager.getMessage("Purchase")));
                inventory.setItem(i + 18, ItemFactory.create(Material.EMERALD_BLOCK, (byte) 0x0, MessageManager.getMessage("Purchase")));
                inventory.setItem(i + 6, ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Cancel")));
                inventory.setItem(i + 9 + 6, ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Cancel")));
                inventory.setItem(i + 18 + 6, ItemFactory.create(Material.REDSTONE_BLOCK, (byte) 0x0, MessageManager.getMessage("Cancel")));
            }
            ItemStack itemStack = ItemFactory.create(Material.TRIPWIRE_HOOK, (byte) 0, ChatColor.translateAlternateColorCodes('&', ((String) SettingsManager.getMessages().get("Buy-Treasure-Key-ItemName")).replace("%price%", "" + (int) SettingsManager.getConfig().get("TreasureChests.Key-Price"))));
            inventory.setItem(13, itemStack);

            ItemFactory.fillInventory(inventory);

            Bukkit.getScheduler().runTaskLater(UltraCosmeticsData.get().getPlugin(), () -> {
                getPlayer().openInventory(inventory);
            }, 3);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Removes current Particle Effect.
     */
    public void removeParticleEffect() {
        if (currentParticleEffect != null) {
            getPlayer().sendMessage(MessageManager.getMessage("Particle-Effects.Unsummon").replace("%effectname%", TextUtil.filterPlaceHolder(currentParticleEffect.getType().getName(), ultraCosmetics)));
            currentParticleEffect = null;
        }
    }

    /**
     * Removes current Morph.
     */
    public void removeMorph() {
        if (currentMorph != null) {
            DisguiseAPI.undisguiseToAll(getPlayer());
            currentMorph.clear();
            currentMorph = null;
        }
    }

    /**
     * Sets the name of a pet.
     *
     * @param petName The pet name.
     * @param name    The new name.
     */
    public void setPetName(String petName, String name) {
        if (UltraCosmeticsData.get().usingFileStorage())
            SettingsManager.getData(getPlayer()).set("Pet-Names." + petName, name);
        else ultraCosmetics.getMySqlConnectionManager().getSqlUtils().setName(getMySqlIndex(), petName, name);
    }

    /**
     * Gets the name of a pet.
     *
     * @param petName The pet.
     * @return The pet name.
     */
    public String getPetName(String petName) {
        try {
            if (UltraCosmeticsData.get().usingFileStorage()) {
                return SettingsManager.getData(getPlayer()).get("Pet-Names." + petName);
            } else {
                if (ultraCosmetics.getMySqlConnectionManager().getSqlUtils().getPetName(getMySqlIndex(), petName).equalsIgnoreCase("Unknown"))
                    return null;
                return ultraCosmetics.getMySqlConnectionManager().getSqlUtils().getPetName(getMySqlIndex(), petName);
            }
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Gives ammo to player.
     *
     * @param name   The gadget.
     * @param amount The ammo amount to give.
     */
    public void addAmmo(String name, int amount) {
        if (UltraCosmeticsData.get().isAmmoEnabled())
            if (UltraCosmeticsData.get().usingFileStorage())
                SettingsManager.getData(getPlayer()).set("Ammo." + name, getAmmo(name) + amount);
            else
                ultraCosmetics.getMySqlConnectionManager().getSqlUtils().addAmmo(getMySqlIndex(), name, amount);
        if (currentGadget != null)
            getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"),
                    ItemFactory.create(currentGadget.getType().getMaterial(), currentGadget.getType().getData(),
                            "§f§l" + getAmmo(currentGadget.getType().toString()
                                    .toLowerCase()) + " " + currentGadget.getType().getName(), MessageManager.getMessage("Gadgets.Lore")));
    }

    /**
     * Sets if player has gadgets enabled.
     *
     * @param enabled if player has gadgets enabled.
     */
    public void setGadgetsEnabled(Boolean enabled) {
        try {
            if (UltraCosmeticsData.get().usingFileStorage()) {
                SettingsManager.getData(getPlayer()).set("Gadgets-Enabled", enabled);
            } else {
                ultraCosmetics.getMySqlConnectionManager().getSqlUtils().setGadgetsEnabled(getMySqlIndex(), enabled);
            }
            if (enabled) {
                getPlayer().sendMessage(MessageManager.getMessage("Enabled-Gadgets"));
                this.gadgetsEnabledCache = CacheValue.ENABLED;
            } else {
                getPlayer().sendMessage(MessageManager.getMessage("Disabled-Gadgets"));
                this.gadgetsEnabledCache = CacheValue.DISABLED;
            }
        } catch (NullPointerException e) {
        }
    }

    /**
     * @return if the player has gadgets enabled or not.
     */
    public boolean hasGadgetsEnabled() {
        if (this.gadgetsEnabledCache != CacheValue.UNLOADED)
            return gadgetsEnabledCache != CacheValue.DISABLED;
        // Make sure it won't be affected before load finished, especially for SQL
        if (!isLoaded)
            return false;

        try {
            if (UltraCosmeticsData.get().usingFileStorage()) {
                return SettingsManager.getData(getPlayer()).get("Gadgets-Enabled");
            } else {
                if (ultraCosmetics.getMySqlConnectionManager().getSqlUtils().hasGadgetsEnabled(getMySqlIndex())) {
                    gadgetsEnabledCache = CacheValue.ENABLED;
                    return true;
                } else {
                    gadgetsEnabledCache = CacheValue.DISABLED;
                    return false;
                }
            }
        } catch (NullPointerException e) {
            return true;
        }
    }

    /**
     * Sets if a player can see his own morph or not.
     *
     * @param enabled if player should be able to see his own morph.
     */
    public void setSeeSelfMorph(Boolean enabled) {
        if (UltraCosmeticsData.get().usingFileStorage()) {
            SettingsManager.getData(getPlayer()).set("Third-Person-Morph-View", enabled);
        } else {
            ultraCosmetics.getMySqlConnectionManager().getSqlUtils().setSeeSelfMorph(getMySqlIndex(), enabled);
        }
        if (enabled) {
            getPlayer().sendMessage(MessageManager.getMessage("Enabled-SelfMorphView"));
            this.morphSelfViewCache = CacheValue.ENABLED;
        } else {
            getPlayer().sendMessage(MessageManager.getMessage("Disabled-SelfMorphView"));
            this.morphSelfViewCache = CacheValue.DISABLED;
        }
    }

    /**
     * @return if player should be able to see his own morph or not.
     */
    public boolean canSeeSelfMorph() {
        if (morphSelfViewCache != CacheValue.UNLOADED)
            return this.morphSelfViewCache != CacheValue.DISABLED;
        // Make sure it won't be affected before load finished, especially for SQL
        if (!isLoaded)
            return false;
        try {
            if (UltraCosmeticsData.get().usingFileStorage()) {
                return SettingsManager.getData(getPlayer()).get("Third-Person-Morph-View");
            } else {
                if (ultraCosmetics.getMySqlConnectionManager().getSqlUtils().canSeeSelfMorph(getMySqlIndex())) {
                    morphSelfViewCache = CacheValue.ENABLED;
                    return true;
                } else {
                    morphSelfViewCache = CacheValue.DISABLED;
                    return false;
                }

            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Gets the ammo of a gadget.
     *
     * @param name The gadget.
     * @return The ammo of the given gadget.
     */
    public int getAmmo(String name) {
        if (UltraCosmeticsData.get().isAmmoEnabled())
            if (UltraCosmeticsData.get().usingFileStorage())
                return (int) SettingsManager.getData(getPlayer()).get("Ammo." + name);
            else
                return ultraCosmetics.getMySqlConnectionManager().getSqlUtils().getAmmo(getMySqlIndex(), name);
        return 0;
    }

    /**
     * Clears current Treasure Chest.
     */
    public void removeTreasureChest() {
        if (currentTreasureChest == null) return;
        this.currentTreasureChest.clear();
        this.currentTreasureChest = null;
    }

    /**
     * Removes One Ammo of a gadget.
     *
     * @param name The gadget.
     */
    public void removeAmmo(String name) {
        if (UltraCosmeticsData.get().isAmmoEnabled()) {
            if (UltraCosmeticsData.get().usingFileStorage()) {
                SettingsManager.getData(getPlayer()).set("Ammo." + name, getAmmo(name) - 1);
            } else {
                ultraCosmetics.getMySqlConnectionManager().getSqlUtils().removeAmmo(getMySqlIndex(), name);
            }
        }
    }

    /**
     * Gives the Menu Item.
     */
    public void giveMenuItem() {
        if (getPlayer() == null)
            return;
        try {
            removeMenuItem();
        } catch (Exception e) {
        }
        int slot = SettingsManager.getConfig().getInt("Menu-Item.Slot");
        if (getPlayer().getInventory().getItem(slot) != null) {
            if (getPlayer().getInventory().getItem(slot).hasItemMeta()
                    && getPlayer().getInventory().getItem(slot).getItemMeta().hasDisplayName()
                    && getPlayer().getInventory().getItem(slot).getItemMeta().getDisplayName().equalsIgnoreCase((String) SettingsManager.getConfig().get("Menu-Item.Displayname"))) {
                getPlayer().getInventory().remove(slot);
                getPlayer().getInventory().setItem(slot, null);
            }
            getPlayer().getWorld().dropItemNaturally(getPlayer().getLocation(), getPlayer().getInventory().getItem(slot));
            getPlayer().getInventory().remove(slot);
        }
        String name = String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§");
        Material material = Material.valueOf((String) SettingsManager.getConfig().get("Menu-Item.Type"));
        byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Data")));
        getPlayer().getInventory().setItem(slot, ItemFactory.create(material, data, name));
    }

    /**
     * Removes the menu Item.
     */
    public void removeMenuItem() {
        if (getPlayer() == null)
            return;
        int slot = SettingsManager.getConfig().getInt("Menu-Item.Slot");
        if (getPlayer().getInventory().getItem(slot) != null
                && getPlayer().getInventory().getItem(slot).hasItemMeta()
                && getPlayer().getInventory().getItem(slot).getItemMeta().hasDisplayName()
                && getPlayer().getInventory().getItem(slot).getItemMeta().getDisplayName()
                .equals(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§")))
            getPlayer().getInventory().setItem(slot, null);
    }

    public void sendMessage(Object message) {
        getPlayer().sendMessage(message.toString());
    }

    /**
     * Gets the UUID.
     *
     * @return The UUID.
     */
    public UUID getUuid() {
        return uuid;
    }

    public int getMySqlIndex() {
        return MySqlConnectionManager.INDEXS.get(uuid) == null ? -1 : MySqlConnectionManager.INDEXS.get(uuid);
    }

    public Emote getCurrentEmote() {
        return currentEmote;
    }

    public Gadget getCurrentGadget() {
        return currentGadget;
    }

    public HashMap<GadgetType, Long> getGadgetCooldowns() {
        return gadgetCooldowns;
    }

    public Hat getCurrentHat() {
        return currentHat;
    }

    public Morph getCurrentMorph() {
        return currentMorph;
    }

    public Mount getCurrentMount() {
        return currentMount;
    }

    public ParticleEffect getCurrentParticleEffect() {
        return currentParticleEffect;
    }

    public Pet getCurrentPet() {
        return currentPet;
    }

    public Suit getCurrentBoots() {
        return currentBoots;
    }

    public Suit getCurrentChestplate() {
        return currentChestplate;
    }

    public Suit getCurrentHelmet() {
        return currentHelmet;
    }

    public Suit getCurrentLeggings() {
        return currentLeggings;
    }

    public TreasureChest getCurrentTreasureChest() {
        return currentTreasureChest;
    }

    public void setCurrentGadget(Gadget currentGadget) {
        this.currentGadget = currentGadget;
    }

    public void setGadgetCooldowns(HashMap<GadgetType, Long> gadgetCooldowns) {
        this.gadgetCooldowns = gadgetCooldowns;
    }

    public void setCurrentBoots(Suit currentBoots) {
        this.currentBoots = currentBoots;
    }

    public void setCurrentChestplate(Suit currentChestplate) {
        this.currentChestplate = currentChestplate;
    }

    public void setCurrentEmote(Emote currentEmote) {
        this.currentEmote = currentEmote;
    }

    public void setCurrentHat(Hat currentHat) {
        this.currentHat = currentHat;
    }

    public void setCurrentHelmet(Suit currentHelmet) {
        this.currentHelmet = currentHelmet;
    }

    public void setCurrentLeggings(Suit currentLeggings) {
        this.currentLeggings = currentLeggings;
    }

    public void setCurrentMorph(Morph currentMorph) {
        this.currentMorph = currentMorph;
    }

    public void setCurrentMount(Mount currentMount) {
        this.currentMount = currentMount;
    }

    public void setCurrentParticleEffect(ParticleEffect currentParticleEffect) {
        this.currentParticleEffect = currentParticleEffect;
    }

    public void setCurrentPet(Pet currentPet) {
        this.currentPet = currentPet;
    }

    public void setCurrentTreasureChest(TreasureChest currentTreasureChest) {
        this.currentTreasureChest = currentTreasureChest;
    }
}