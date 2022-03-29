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
import be.isach.ultracosmetics.menu.menus.MenuPurchase;
import be.isach.ultracosmetics.mysql.SqlCache;
import be.isach.ultracosmetics.player.profile.CosmeticsProfile;
import be.isach.ultracosmetics.player.profile.FileCosmeticsProfile;
import be.isach.ultracosmetics.run.FallDamageManager;
import be.isach.ultracosmetics.treasurechests.TreasureChest;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.PurchaseData;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.util.XMaterial;
import me.libraryaddict.disguise.DisguiseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    private UUID uuid;
    /**
     * Saves the username for logging usage.
     */
    private String username;
    /**
     * Current Cosmetics.
     */
    private Gadget currentGadget;
    private Mount<?> currentMount;
    private ParticleEffect currentParticleEffect;
    private Pet currentPet;
    private TreasureChest currentTreasureChest;
    private Morph currentMorph;
    private Hat currentHat;
    private Map<ArmorSlot, Suit> suitMap = new HashMap<>();
    private Emote currentEmote;
    /**
     * Stores enabled cosmetics, keys, pet names, ammo, etc.
     */
    private CosmeticsProfile cosmeticsProfile;
    /**
     * Specifies if the player can currently be hit by any other gadget.
     * Exemple: Get hit by a flesh hook.
     */
    private boolean canBeHitByOtherGadgets = true;
    /**
     * Cooldown map storing all the current cooldowns for gadgets.
     */
    private Map<GadgetType, Long> gadgetCooldowns = null;

    private UltraCosmetics ultraCosmetics;

    private volatile boolean moving;
    private volatile Location lastPos;

    /**
     * Indicates if the player is leaving the server.
     * Useful to differentiate for example when a player deactivates
     * a cosmetic on purpose or because they leave.
     */
    private boolean quitting = false;

    /**
     * Allows to store custom data for each player easily.
     * <p/>
     * Created on join, and deleted on quit.
     *
     * @param uuid The player UUID.
     */
    public UltraPlayer(UUID uuid, UltraCosmetics ultraCosmetics) {
        this.uuid = uuid;
        this.ultraCosmetics = ultraCosmetics;

        gadgetCooldowns = new HashMap<>();
        this.username = getBukkitPlayer().getDisplayName();

        if (UltraCosmeticsData.get().usingFileStorage()) {
            cosmeticsProfile = new FileCosmeticsProfile(this, ultraCosmetics);
        } else {
            // loads data from database async
            cosmeticsProfile = new SqlCache(this, ultraCosmetics); 
        }
    }

    /**
     * Checks if a player can use a given gadget type.
     *
     * @param gadget The gadget type.
     * @return 0 if player can use, otherwise the time left (in seconds).
     */
    public double getCooldown(GadgetType gadget) {
        Long count = gadgetCooldowns.get(gadget);

        if (count == null || System.currentTimeMillis() > count) {
            return 0;
        }

        double valueMillis = count - System.currentTimeMillis();
        return valueMillis / 1000d;
    }

    public boolean canUse(GadgetType gadget) {
        return getCooldown(gadget) == 0;
    }

    /**
     * Sets the cooldown of a gadget.
     *
     * @param gadget    The gadget.
     */
    public void setCoolDown(GadgetType gadget) {
        double cooldown = gadget.getCountdown();
        if (isBypassingCooldown()) {
            cooldown = gadget.getRunTime();
        }
        gadgetCooldowns.put(gadget, (long)(cooldown * 1000 + System.currentTimeMillis()));
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
        setCurrentGadget(null);
    }

    /**
     * Removes the current emote.
     */
    public void removeEmote() {
        if (currentEmote == null) {
            return;
        }
        currentEmote.clear();
        setCurrentEmote(null);
    }


    /**
     * Removes the current Mount.
     */
    public void removeMount() {
        if (currentMount == null) {
            return;
        }
        currentMount.clear();
        setCurrentMount(null);
    }

    /**
     * Removes the current Pet.
     */
    public void removePet() {
        if (currentPet == null) {
            return;
        }
        currentPet.clear();
        setCurrentPet(null);
    }

    public void addKeys(int amount) {
        cosmeticsProfile.addKeys(amount);
    }

    /**
     * Gives a key to the player.
     */
    public void addKey() {
        addKeys(1);
    }

    /**
     * Removes a key from the player.
     */
    public void removeKey() {
        addKeys(-1);
    }

    /**
     * @return The amount of keys that the player owns.
     */
    public int getKeys() {
        return cosmeticsProfile.getKeys();
    }

    public void saveCosmeticsProfile() {
        cosmeticsProfile.save();
    }

    /**
     * Removes the current hat.
     */
    public void removeHat() {
        if (currentHat == null) {
            return;
        }

        currentHat.clear();
        setCurrentHat(null);
    }

    public void setCurrentSuitPart(ArmorSlot armorSlot, Suit suit) {
        suitMap.put(armorSlot, suit);
        if (!isQuitting()) {
            cosmeticsProfile.setEnabledSuitPart(armorSlot, suit == null ? null : suit.getType());
        }
    }

    /**
     * Removes the current suit of armorSlot.
     *
     * @param armorSlot The ArmorSlot to remove.
     */
    public void removeSuit(ArmorSlot armorSlot) {
        if (suitMap.get(armorSlot) == null) {
            return;
        }

        suitMap.get(armorSlot).clear();
        setCurrentSuitPart(armorSlot, null);
    }

    public double getBalance() {
        if (ultraCosmetics.getEconomyHandler().isUsingEconomy()) {
            return ultraCosmetics.getEconomyHandler().balance(getBukkitPlayer());
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
     * Returns true if the player has any cosmetics equipped
     */
    public boolean hasCosmeticsEquipped() {
        return currentGadget != null
                || currentParticleEffect != null
                || currentPet != null
                || currentMount != null
                || currentTreasureChest != null
                || currentHat != null
                || currentEmote != null
                || currentMorph != null
                || hasSuitOn();
    }

    /**
     * Clears all gadgets.
     */
    public boolean clear() {
        boolean toReturn = hasCosmeticsEquipped();
        if (Category.MORPHS.isEnabled() && Bukkit.getPluginManager().isPluginEnabled("LibsDisguises")
                // Ensure disguises in non-enabled worlds (not from UC) aren't cleared on accident.
                // If player is "quitting", remove the disguise anyway. Player is marked as quitting
                // when changing worlds, making sure morphs get correctly unset.
                && (isQuitting() || SettingsManager.isAllowedWorld(getBukkitPlayer().getWorld()))) {
            removeMorph();
            DisguiseAPI.undisguiseToAll(getBukkitPlayer());
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

    @SuppressWarnings("unchecked")
    public <T extends Cosmetic<?>> T getCosmetic(Category category) {
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
            default:
                return null;
        }
    }

    /**
     * Opens the Key Purchase Menu.
     */
    public void openKeyPurchaseMenu() {
        if (!ultraCosmetics.getEconomyHandler().isUsingEconomy()) {
            return;
        }

        if (!getBukkitPlayer().hasPermission("ultracosmetics.treasurechests.buykey")) {
            getBukkitPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You don't have permission to buy Treasure Keys.");
            return;
        }

        ItemStack itemStack = ItemFactory.create(XMaterial.TRIPWIRE_HOOK, ChatColor.translateAlternateColorCodes('&', ((String) SettingsManager.getMessages().get("Buy-Treasure-Key-ItemName")).replace("%price%", "" + SettingsManager.getConfig().getInt("TreasureChests.Key-Price"))));

        PurchaseData pd = new PurchaseData();
        pd.setPrice(SettingsManager.getConfig().getInt("TreasureChests.Key-Price"));
        pd.setShowcaseItem(itemStack);
        pd.setOnPurchase(() -> {
            addKey();
            getBukkitPlayer().closeInventory();
            ultraCosmetics.getMenus().getMainMenu().open(this);
        });
        MenuPurchase mp = new MenuPurchase(ultraCosmetics, MessageManager.getMessage("Buy-Treasure-Key"), pd);
        Bukkit.getScheduler().runTaskLater(ultraCosmetics, () -> getBukkitPlayer().openInventory(mp.getInventory(this)), 1);
    }

    /**
     * Removes current Particle Effect.
     */
    public void removeParticleEffect() {
        if (currentParticleEffect == null) {
            return;
        }

        currentParticleEffect.clear();
        setCurrentParticleEffect(null);
    }

    /**
     * Removes current Morph.
     */
    public void removeMorph() {
        if (currentMorph == null) {
            return;
        }

        currentMorph.clear();
        setCurrentMorph(null);
    }

    /**
     * Sets the name of a pet.
     *
     * @param petType The pet name.
     * @param name    The new name.
     */
    public void setPetName(PetType petType, String name) {
        if (name.isEmpty()) {
            name = null;
        }
        cosmeticsProfile.setPetName(petType, name);
        if (currentPet != null) {
            currentPet.updateName();
        }
    }

    /**
     * Gets the name of a pet.
     *
     * @param petType The pet type.
     * @return The pet name.
     */
    public String getPetName(PetType petType) {
        return cosmeticsProfile.getPetName(petType);
    }

    /**
     * Gives ammo to player.
     *
     * @param type   The gadget type.
     * @param amount The ammo amount to give.
     */
    public void addAmmo(GadgetType type, int amount) {
        if (UltraCosmeticsData.get().isAmmoEnabled()) {
            cosmeticsProfile.addAmmo(type, amount);

            if (currentGadget != null) {
                getBukkitPlayer().getInventory().setItem(SettingsManager.getConfig().getInt("Gadget-Slot"),
                        ItemFactory.create(currentGadget.getType().getMaterial(),
                                ChatColor.WHITE + "" + ChatColor.BOLD + getAmmo(currentGadget.getType()) + " " + currentGadget.getType().getName(), MessageManager.getMessage("Gadgets.Lore")));
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
    public void setGadgetsEnabled(boolean enabled) {
        cosmeticsProfile.setGadgetsEnabled(enabled);

        if (enabled) {
            getBukkitPlayer().sendMessage(MessageManager.getMessage("Enabled-Gadgets"));
        } else {
            getBukkitPlayer().sendMessage(MessageManager.getMessage("Disabled-Gadgets"));
        }
    }

    /**
     * @return if the player has gadgets enabled or not.
     */
    public boolean hasGadgetsEnabled() {
        return cosmeticsProfile.hasGadgetsEnabled();
    }

    /**
     * Sets if a player can see his own morph or not.
     *
     * @param enabled if player should be able to see his own morph.
     */
    public void setSeeSelfMorph(boolean enabled) {
        cosmeticsProfile.setSeeSelfMorph(enabled);
        if (enabled) {
            getBukkitPlayer().sendMessage(MessageManager.getMessage("Enabled-SelfMorphView"));
        } else {
            getBukkitPlayer().sendMessage(MessageManager.getMessage("Disabled-SelfMorphView"));
        }
        if (currentMorph != null) {
            currentMorph.setSeeSelf(enabled);
        }
    }

    /**
     * @return if player should be able to see his own morph or not.
     */
    public boolean canSeeSelfMorph() {
        return cosmeticsProfile.canSeeSelfMorph();
    }

    /**
     * Gets the ammo of a gadget.
     *
     * @param type The gadget type.
     * @return The ammo of the given gadget.
     */
    public int getAmmo(GadgetType type) {
        if (!UltraCosmeticsData.get().isAmmoEnabled()) return 0;
        return cosmeticsProfile.getAmmo(type);
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
     * @param type The gadget.
     */
    public void removeAmmo(GadgetType type) {
        addAmmo(type, -1);
    }

    /**
     * Gives the Menu Item.
     */
    public void giveMenuItem() {
        if (getBukkitPlayer() == null) return;
        removeMenuItem();
        int slot = SettingsManager.getConfig().getInt("Menu-Item.Slot");
        if (getBukkitPlayer().getInventory().getItem(slot) != null) {
            if (getBukkitPlayer().getInventory().getItem(slot).hasItemMeta()
                    && getBukkitPlayer().getInventory().getItem(slot).getItemMeta().hasDisplayName()
                    && getBukkitPlayer().getInventory().getItem(slot).getItemMeta().getDisplayName().equalsIgnoreCase(SettingsManager.getConfig().getString("Menu-Item.Displayname"))) {
                // getBukkitPlayer().getInventory().remove(slot);
                getBukkitPlayer().getInventory().setItem(slot, null);
            }
            getBukkitPlayer().getWorld().dropItemNaturally(getBukkitPlayer().getLocation(), getBukkitPlayer().getInventory().getItem(slot));
            // getBukkitPlayer().getInventory().remove(slot);
            getBukkitPlayer().getInventory().setItem(slot, null);
        }
        String name = ChatColor.translateAlternateColorCodes('&', SettingsManager.getConfig().getString("Menu-Item.Displayname"));
        int model = SettingsManager.getConfig().getInt("Menu-Item.Custom-Model-Data");
        ItemStack stack = ItemFactory.rename(ItemFactory.getItemStackFromConfig("Menu-Item.Type"), name);

        if (UltraCosmeticsData.get().getServerVersion().isAtLeast(ServerVersion.v1_14_R1) && model != 0) {
            ItemMeta meta = stack.getItemMeta();
            meta.setCustomModelData(model);
            stack.setItemMeta(meta);
        }

        getBukkitPlayer().getInventory().setItem(slot, stack);
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
    public UUID getUUID() {
        return uuid;
    }

    public Emote getCurrentEmote() {
        return currentEmote;
    }

    public void setCurrentEmote(Emote currentEmote) {
        this.currentEmote = currentEmote;
        if (!isQuitting()) {
            cosmeticsProfile.setEnabledCosmetic(Category.EMOTES, currentEmote);
        }
    }

    public Gadget getCurrentGadget() {
        return currentGadget;
    }

    public void setCurrentGadget(Gadget currentGadget) {
        this.currentGadget = currentGadget;
        if (!isQuitting()) {
            cosmeticsProfile.setEnabledCosmetic(Category.GADGETS, currentGadget);
        }
    }

    public Map<GadgetType, Long> getGadgetCooldowns() {
        return gadgetCooldowns;
    }

    public void setGadgetCooldowns(HashMap<GadgetType, Long> gadgetCooldowns) {
        this.gadgetCooldowns = gadgetCooldowns;
    }

    public Hat getCurrentHat() {
        return currentHat;
    }

    public void setCurrentHat(Hat currentHat) {
        this.currentHat = currentHat;
        if (!isQuitting()) {
            cosmeticsProfile.setEnabledCosmetic(Category.HATS, currentHat);
        }
    }

    public Morph getCurrentMorph() {
        return currentMorph;
    }

    public void setCurrentMorph(Morph currentMorph) {
        this.currentMorph = currentMorph;
        if (!isQuitting()) {
            cosmeticsProfile.setEnabledCosmetic(Category.MORPHS, currentMorph);
        }
    }

    public Mount<?> getCurrentMount() {
        return currentMount;
    }

    public void setCurrentMount(Mount<?> currentMount) {
        this.currentMount = currentMount;
        if (!isQuitting()) {
            cosmeticsProfile.setEnabledCosmetic(Category.MOUNTS, currentMount);
        }
    }

    public ParticleEffect getCurrentParticleEffect() {
        return currentParticleEffect;
    }

    public void setCurrentParticleEffect(ParticleEffect currentParticleEffect) {
        this.currentParticleEffect = currentParticleEffect;
        if (!isQuitting()) {
            cosmeticsProfile.setEnabledCosmetic(Category.EFFECTS, currentParticleEffect);
        }
    }

    public Pet getCurrentPet() {
        return currentPet;
    }

    public void setCurrentPet(Pet currentPet) {
        this.currentPet = currentPet;
        if (!isQuitting()) {
            cosmeticsProfile.setEnabledCosmetic(Category.PETS, currentPet);
        }
    }

    public TreasureChest getCurrentTreasureChest() {
        return currentTreasureChest;
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

    public void setCanBeHitByOtherGadgets(boolean canBeHitByOtherGadgets) {
        this.canBeHitByOtherGadgets = canBeHitByOtherGadgets;
    }

    public boolean canBeHitByOtherGadgets() {
        return canBeHitByOtherGadgets;
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
            case SUITS:
                removeSuit();
            default:
                return;
        }
    }

    public boolean isOnline() {
        return Bukkit.getServer().getPlayer(uuid) != null;
    }

    public String getUsername() {
        return username;
    }

    public boolean isQuitting() {
        return quitting;
    }

    public void setQuitting(boolean quitting) {
        this.quitting = quitting;
    }

    public boolean isBypassingCooldown() {
        return getBukkitPlayer().hasPermission("ultracosmetics.bypass.cooldown");
    }

    public void equipProfile() {
        // enabled check is in the equip method
        cosmeticsProfile.equip();
    }

    public boolean isFilteringByOwned() {
        return cosmeticsProfile.isFilterByOwned();
    }

    public void setFilteringByOwned(boolean filterByOwned) {
        cosmeticsProfile.setFilterByOwned(filterByOwned);
    }

    public boolean isTreasureNotifying() {
        return cosmeticsProfile.isTreasureNotifications();
    }

    public void setTreasureNotifying(boolean treasureNotifications) {
        cosmeticsProfile.setTreasureNotifications(treasureNotifications);
    }
}