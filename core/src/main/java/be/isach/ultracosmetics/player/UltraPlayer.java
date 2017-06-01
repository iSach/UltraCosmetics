package be.isach.ultracosmetics.player;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.emotes.Emote;
import be.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import be.isach.ultracosmetics.cosmetics.hats.Hat;
import be.isach.ultracosmetics.cosmetics.morphs.Morph;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffect;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.suits.Suit;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.mysql.MySqlConnectionManager;
import be.isach.ultracosmetics.run.FallDamageManager;
import be.isach.ultracosmetics.treasurechests.TreasureChest;
import be.isach.ultracosmetics.util.CacheValue;
import be.isach.ultracosmetics.util.ItemFactory;
import me.libraryaddict.disguise.DisguiseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a player on the server.
 *
 * @author iSach
 * @since 08-03-2015
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
    private Map<ArmorSlot, Suit> suitMap = new HashMap<>();
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

    private volatile boolean moving;
    private volatile Location lastPos;

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
            this.ultraCosmetics = ultraCosmetics;

            gadgetCooldowns = new HashMap<>();

            if (UltraCosmeticsData.get().usingFileStorage())
                SettingsManager.getData(getBukkitPlayer()).addDefault("Keys", 0);

            if (UltraCosmeticsData.get().isAmmoEnabled()) {
                if (!UltraCosmeticsData.get().usingFileStorage())
                    ultraCosmetics.getMySqlConnectionManager().getSqlUtils().initStats(this);
                else {
                    GadgetType.values().stream().filter(GadgetType::isEnabled).forEachOrdered(type -> SettingsManager.getData(getBukkitPlayer()).addDefault("Ammo." + type.toString().toLowerCase(), 0));
                }
            }
            if (UltraCosmeticsData.get().usingFileStorage()) {
                SettingsManager.getData(getBukkitPlayer()).addDefault("Gadgets-Enabled", true);
                SettingsManager.getData(getBukkitPlayer()).addDefault("Third-Person-Morph-View", true);
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
    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Removes the current gadget.
     */
    public void removeGadget() {
        if (currentGadget == null) {
            return;
        }

        currentGadget.clear();
        currentGadget = null;
    }

    /**
     * Removes the current emote.
     */
    public void removeEmote() {
        if (currentEmote == null) {
            return;
        }
        currentEmote.clear();
        currentEmote = null;
    }


    /**
     * Removes the current Mount.
     */
    public void removeMount() {
        if (currentMount == null) {
            return;
        }
        currentMount.clear();
        currentMount = null;
    }

    /**
     * Removes the current Pet.
     */
    public void removePet() {
        if (currentPet == null) {
            return;
        }
        currentPet.clear();
        currentPet = null;
    }

    /**
     * Gives a key to the player.
     */
    public void addKey() {
        if (UltraCosmeticsData.get().usingFileStorage()) {
            SettingsManager.getData(getBukkitPlayer()).set("Keys", getKeys() + 1);
        } else {
            ultraCosmetics.getMySqlConnectionManager().getSqlUtils().addKey(getMySqlIndex());
        }
    }

    /**
     * Removes a key to the player.
     */
    public void removeKey() {
        if (UltraCosmeticsData.get().usingFileStorage())
            SettingsManager.getData(getBukkitPlayer()).set("Keys", getKeys() - 1);
        else
            ultraCosmetics.getMySqlConnectionManager().getSqlUtils().removeKey(getMySqlIndex());
    }

    /**
     * @return The amount of keys that the player owns.
     */
    public int getKeys() {
        return UltraCosmeticsData.get().usingFileStorage() ? (int) SettingsManager.getData(getBukkitPlayer()).get("Keys") : ultraCosmetics.getMySqlConnectionManager().getSqlUtils().getKeys(getMySqlIndex());
    }

    /**
     * Removes the current hat.
     */
    public void removeHat() {
        if (currentHat == null) {
            return;
        }

        currentHat.clear();
        currentHat = null;
    }

    public void setSuit(ArmorSlot armorSlot, Suit suit) {
        suitMap.put(armorSlot, suit);
    }

    /**
     * Removes the current suit of armorSlot.
     *
     * @param armorSlot The ArmorSlot to remove.
     */
    public void removeSuit(ArmorSlot armorSlot) {
        if (!suitMap.containsKey(armorSlot)) {
            suitMap.put(armorSlot, null);
            return;
        }

        if (suitMap.get(armorSlot) == null) {
            return;
        }

        suitMap.get(armorSlot).clear();
        suitMap.put(armorSlot, null);
    }

    public double getBalance() {
        try {
            if (ultraCosmetics.getEconomy() != null) {
                return ultraCosmetics.getEconomy().getBalance(getBukkitPlayer());
            }
        } catch (Exception exc) {
            ultraCosmetics.getSmartLogger().write("Error happened while getting a player's balance.");
            return 0;
        }
        return 0;
    }

    public boolean hasPermission(String permission) {
        return getBukkitPlayer().hasPermission(permission);
    }

    /**
     * @param armorSlot The armorslot to get.
     * @return The Suit from the armor slot.
     */
    public Suit getSuit(ArmorSlot armorSlot) {
        if (!suitMap.containsKey(armorSlot)) {
            suitMap.put(armorSlot, null);
        }

        return suitMap.get(armorSlot);
    }

    /**
     * Checks if this player has any suit piece on.
     *
     * @return True if this player has any suit piece on, false otherwise.
     */
    public boolean hasSuitOn() {
        for (ArmorSlot armorSlot : ArmorSlot.values()) {
            if (suitMap.get(armorSlot) != null)
                return true;
        }
        return false;
    }

    /**
     * Removes entire suit.
     */
    public void removeSuit() {
        for (ArmorSlot armorSlot : ArmorSlot.values()) {
            removeSuit(armorSlot);
        }
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
                DisguiseAPI.undisguiseToAll(getBukkitPlayer());
            } catch (Exception ignored) {
            }
        }
        removeGadget();
        removeParticleEffect();
        removePet();
        removeMount();
        removeTreasureChest();
        removeHat();
        removeEmote();
        removeSuit();
        return toReturn;
    }

    public <T extends Cosmetic> T getCosmetic(Category category) {
        switch (category) {
            case EFFECTS:
                return (T) getCurrentParticleEffect();
            case EMOTES:
                return (T) getCurrentEmote();
            case GADGETS:
                return (T) getCurrentGadget();
            case HATS:
                return (T) getCurrentHat();
            case MORPHS:
                return (T) getCurrentMorph();
            case MOUNTS:
                return (T) getCurrentMount();
            case PETS:
                return (T) getCurrentPet();
        }
        return null;
    }

    /**
     * Opens the Key Purchase Menu.
     */
    public void openKeyPurchaseMenu() {
        if (ultraCosmetics.getEconomy() == null) {
            return;
        }

        if (!ultraCosmetics.isVaultLoaded()) {
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
            Bukkit.getScheduler().runTaskLater(ultraCosmetics, () -> getBukkitPlayer().openInventory(inventory), 3);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Removes current Particle Effect.
     */
    public void removeParticleEffect() {
        if (currentParticleEffect == null) {
            return;
        }

        currentParticleEffect.clear();
        currentParticleEffect = null;
    }

    /**
     * Removes current Morph.
     */
    public void removeMorph() {
        if (currentMorph == null) {
            return;
        }

        currentMorph.clear();
        currentMorph = null;
    }

    /**
     * Sets the name of a pet.
     *
     * @param petType The pet name.
     * @param name    The new name.
     */
    public void setPetName(PetType petType, String name) {
        name = ChatColor.translateAlternateColorCodes('&', name.replaceAll("[^A-Za-z0-9 &&[^&]]", "").replace(" ", ""));
        if (currentPet != null) {
            if (currentPet.armorStand != null) {
                currentPet.armorStand.setCustomName(name);
            } else {
                currentPet.getEntity().setCustomName(name);
            }
        }
        if (UltraCosmeticsData.get().usingFileStorage()) {
            SettingsManager.getData(getBukkitPlayer()).set("Pet-Names." + petType.getConfigName(), name);
        } else {
            ultraCosmetics.getMySqlConnectionManager().getSqlUtils().setName(getMySqlIndex(), petType.getConfigName(), name);
        }
    }

    /**
     * Gets the name of a pet.
     *
     * @param petType The pet type.
     * @return The pet name.
     */
    public String getPetName(PetType petType) {
        try {
            if (UltraCosmeticsData.get().usingFileStorage()) {
                return SettingsManager.getData(getBukkitPlayer()).get("Pet-Names." + petType.getConfigName());
            } else {
                if (ultraCosmetics.getMySqlConnectionManager().getSqlUtils().getPetName(getMySqlIndex(), petType.getConfigName()).equalsIgnoreCase("Unknown")) {
                    return null;
                }
                return ultraCosmetics.getMySqlConnectionManager().getSqlUtils().getPetName(getMySqlIndex(), petType.getConfigName());
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
        if (UltraCosmeticsData.get().isAmmoEnabled()) {
            if (UltraCosmeticsData.get().usingFileStorage()) {
                SettingsManager.getData(getBukkitPlayer()).set("Ammo." + name, getAmmo(name) + amount);
            } else {
                ultraCosmetics.getMySqlConnectionManager().getSqlUtils().addAmmo(getMySqlIndex(), name, amount);
            }

            if (currentGadget != null) {
                getBukkitPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"),
                        ItemFactory.create(currentGadget.getType().getMaterial(), currentGadget.getType().getData(),
                                ChatColor.WHITE + "" + ChatColor.BOLD + getAmmo(currentGadget.getType().toString()
                                        .toLowerCase()) + " " + currentGadget.getType().getName(), MessageManager.getMessage("Gadgets.Lore")));
            }
        }
    }

    public void applyVelocity(Vector vector) {
        getBukkitPlayer().setVelocity(vector);
        Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmeticsData.get().getPlugin(), () ->
            FallDamageManager.addNoFall(getBukkitPlayer()), 2);
    }

    /**
     * Sets if player has gadgets enabled.
     *
     * @param enabled if player has gadgets enabled.
     */
    public void setGadgetsEnabled(Boolean enabled) {
        try {
            if (UltraCosmeticsData.get().usingFileStorage()) {
                SettingsManager.getData(getBukkitPlayer()).set("Gadgets-Enabled", enabled);
            } else {
                ultraCosmetics.getMySqlConnectionManager().getSqlUtils().setGadgetsEnabled(getMySqlIndex(), enabled);
            }

            if (enabled) {
                getBukkitPlayer().sendMessage(MessageManager.getMessage("Enabled-Gadgets"));
                this.gadgetsEnabledCache = CacheValue.ENABLED;
            } else {
                getBukkitPlayer().sendMessage(MessageManager.getMessage("Disabled-Gadgets"));
                this.gadgetsEnabledCache = CacheValue.DISABLED;
            }
        } catch (NullPointerException e) {
        }
    }

    /**
     * @return if the player has gadgets enabled or not.
     */
    public boolean hasGadgetsEnabled() {
        if (this.gadgetsEnabledCache != CacheValue.UNLOADED) {
            return gadgetsEnabledCache != CacheValue.DISABLED;
        }

        if (!isLoaded) {
            return false;
        }

        try {
            if (UltraCosmeticsData.get().usingFileStorage()) {
                return SettingsManager.getData(getBukkitPlayer()).get("Gadgets-Enabled");
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
            SettingsManager.getData(getBukkitPlayer()).set("Third-Person-Morph-View", enabled);
        } else {
            ultraCosmetics.getMySqlConnectionManager().getSqlUtils().setSeeSelfMorph(getMySqlIndex(), enabled);
        }
        if (enabled) {
            getBukkitPlayer().sendMessage(MessageManager.getMessage("Enabled-SelfMorphView"));
            this.morphSelfViewCache = CacheValue.ENABLED;
            DisguiseAPI.setViewDisguiseToggled(getBukkitPlayer(), true);
        } else {
            getBukkitPlayer().sendMessage(MessageManager.getMessage("Disabled-SelfMorphView"));
            this.morphSelfViewCache = CacheValue.DISABLED;
            DisguiseAPI.setViewDisguiseToggled(getBukkitPlayer(), false);
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
                return SettingsManager.getData(getBukkitPlayer()).get("Third-Person-Morph-View");
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
                return (int) SettingsManager.getData(getBukkitPlayer()).get("Ammo." + name);
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
                SettingsManager.getData(getBukkitPlayer()).set("Ammo." + name, getAmmo(name) - 1);
            } else {
                ultraCosmetics.getMySqlConnectionManager().getSqlUtils().removeAmmo(getMySqlIndex(), name);
            }
        }
    }

    /**
     * Gives the Menu Item.
     */
    public void giveMenuItem() {
        if (getBukkitPlayer() == null)
            return;
        try {
            removeMenuItem();
        } catch (Exception e) {
        }
        int slot = SettingsManager.getConfig().getInt("Menu-Item.Slot");
        if (getBukkitPlayer().getInventory().getItem(slot) != null) {
            if (getBukkitPlayer().getInventory().getItem(slot).hasItemMeta()
                    && getBukkitPlayer().getInventory().getItem(slot).getItemMeta().hasDisplayName()
                    && getBukkitPlayer().getInventory().getItem(slot).getItemMeta().getDisplayName().equalsIgnoreCase((String) SettingsManager.getConfig().get("Menu-Item.Displayname"))) {
                getBukkitPlayer().getInventory().remove(slot);
                getBukkitPlayer().getInventory().setItem(slot, null);
            }
            getBukkitPlayer().getWorld().dropItemNaturally(getBukkitPlayer().getLocation(), getBukkitPlayer().getInventory().getItem(slot));
            getBukkitPlayer().getInventory().remove(slot);
        }
        String name = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")));
        Material material = Material.valueOf((String) SettingsManager.getConfig().get("Menu-Item.Type"));
        byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Data")));
        getBukkitPlayer().getInventory().setItem(slot, ItemFactory.create(material, data, name));
    }

    /**
     * Removes the menu Item.
     */
    public void removeMenuItem() {
        if (getBukkitPlayer() == null)
            return;
        int slot = SettingsManager.getConfig().getInt("Menu-Item.Slot");
        if (getBukkitPlayer().getInventory().getItem(slot) != null
                && getBukkitPlayer().getInventory().getItem(slot).hasItemMeta()
                && getBukkitPlayer().getInventory().getItem(slot).getItemMeta().hasDisplayName()
                && getBukkitPlayer().getInventory().getItem(slot).getItemMeta().getDisplayName()
                .equals(ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")))))
            getBukkitPlayer().getInventory().setItem(slot, null);
    }

    public void sendMessage(Object message) {
        getBukkitPlayer().sendMessage(message.toString());
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

    public TreasureChest getCurrentTreasureChest() {
        return currentTreasureChest;
    }

    public void setCurrentGadget(Gadget currentGadget) {
        this.currentGadget = currentGadget;
    }

    public void setGadgetCooldowns(HashMap<GadgetType, Long> gadgetCooldowns) {
        this.gadgetCooldowns = gadgetCooldowns;
    }

    public void setCurrentEmote(Emote currentEmote) {
        this.currentEmote = currentEmote;
    }

    public void setCurrentHat(Hat currentHat) {
        this.currentHat = currentHat;
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

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public Location getLastPos() {
        return lastPos;
    }

    public void setLastPos(Location lastPos) {
        this.lastPos = lastPos;
    }

    public void removeCosmetic(Category category) {
        switch (category) {
            case EFFECTS:
                removeParticleEffect();
            case EMOTES:
                removeEmote();
            case GADGETS:
                removeGadget();
            case HATS:
                removeHat();
            case MORPHS:
                removeMorph();
            case MOUNTS:
                removeMount();
            case PETS:
                removePet();
        }
    }
}