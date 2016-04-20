package be.isach.ultracosmetics;

import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.gadgets.Gadget;
import be.isach.ultracosmetics.cosmetics.gadgets.GadgetType;
import be.isach.ultracosmetics.cosmetics.hats.Hat;
import be.isach.ultracosmetics.cosmetics.morphs.Morph;
import be.isach.ultracosmetics.cosmetics.mounts.Mount;
import be.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffect;
import be.isach.ultracosmetics.cosmetics.pets.Pet;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.suits.Suit;
import be.isach.ultracosmetics.cosmetics.treasurechests.TreasureChest;
import be.isach.ultracosmetics.util.ItemFactory;
import me.libraryaddict.disguise.DisguiseAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public class CustomPlayer {

    /**
     * Player UUID.
     */
    public UUID uuid;

    /**
     * Current Cosmetics.
     */
    public Gadget currentGadget = null;
    public Mount currentMount;
    public ParticleEffect currentParticleEffect;
    public Pet currentPet;
    public TreasureChest currentTreasureChest;
    public Morph currentMorph;
    public Hat currentHat;
    public Suit currentHelmet,
            currentChestplate,
            currentLeggings,
            currentBoots;
    /**
     * boolean to identify if player is loaded correctly
     */
    public boolean isLoaded = false;
    /**
     * Cooldown map storing all the current cooldowns for gadgets.
     */
    private HashMap<GadgetType, Long> gadgetCooldowns = null;
    /**
     * Cache boolean  for SQL to minimize SQL query
     * <p/>
     * -1 unload
     * 0 disable
     * 1 enable
     */

    private short cache_hasGadgetsEnable = -1;
    private short cache_canSeeSelfMorph = -1;


    /**
     * Allows to store custom data for each player easily.
     * <p/>
     * Created on join, and deleted on quit.
     *
     * @param uuid The player UUID.
     */
    public CustomPlayer(UUID uuid) {
        try {
            this.uuid = uuid;

            gadgetCooldowns = new HashMap<>();

            if (UltraCosmetics.getInstance().usingFileStorage())
                SettingsManager.getData(getPlayer()).addDefault("Keys", 0);

            if (UltraCosmetics.getInstance().isAmmoEnabled()) {
                if (!UltraCosmetics.getInstance().usingFileStorage())
                    UltraCosmetics.sqlUtils.initStats(getPlayer());
                else
                    for (GadgetType type : GadgetType.values())
                        if (type.isEnabled())
                            SettingsManager.getData(getPlayer()).addDefault("Ammo." + type.toString().toLowerCase(), 0);
            }
            if (UltraCosmetics.getInstance().usingFileStorage()) {
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
        if(!UltraCosmetics.getInstance().usingFileStorage()){
	        try{
	        	UltraCosmetics.getSQLLoader().addPreloadPlayer(uuid);
	        }catch(Exception e){
	            System.out.println("UltraCosmetics ERR -> " + "SQLLoader Fails to preload UUID: " + uuid);
	        }
        }else{
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
        if (count == null)
            return -1;
        if (System.currentTimeMillis() > (long) count)
            return -1;
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
     * Get the player owning the CustomPlayer.
     *
     * @return The player owning the CustomPlayer.
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Removes the current gadget.
     */
    public void removeGadget() {
        if (currentGadget != null) {
            if (getPlayer() != null)
                getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Unequip").replace("%gadgetname%", (UltraCosmetics.getInstance().placeholdersHaveColor()) ? currentGadget.getName() : UltraCosmetics.filterColor(currentGadget.getName())));
            currentGadget.removeItem();
            currentGadget.onClear();
            currentGadget.removeListener();
            currentGadget.unregisterListeners();
            currentGadget = null;
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
            for (Item item : currentPet.items)
                item.remove();
            currentPet.clear();
            currentPet = null;
        }
    }

    /**
     * Gives a key to the player.
     */
    public void addKey() {
        if (UltraCosmetics.getInstance().usingFileStorage())
            SettingsManager.getData(getPlayer()).set("Keys", getKeys() + 1);
        else
            UltraCosmetics.sqlUtils.addKey(getPlayer().getUniqueId());
    }

    /**
     * Removes a key to the player.
     */
    public void removeKey() {
        if (UltraCosmetics.getInstance().usingFileStorage())
            SettingsManager.getData(getPlayer()).set("Keys", getKeys() - 1);
        else
            UltraCosmetics.sqlUtils.removeKey(getPlayer().getUniqueId());
    }

    /**
     * @return The amount of keys that the player owns.
     */
    public int getKeys() {
        return UltraCosmetics.getInstance().usingFileStorage() ? (int) SettingsManager.getData(getPlayer()).get("Keys") : UltraCosmetics.sqlUtils.getKeys(getPlayer().getUniqueId());
    }

    /**
     * Removes the current hat.
     */
    public void removeHat() {
        if (currentHat == null) return;
        getPlayer().getInventory().setHelmet(null);

        getPlayer().sendMessage(MessageManager.getMessage("Hats.Unequip")
                .replace("%hatname%",
                        (UltraCosmetics.getInstance().placeholdersHaveColor()) ? currentHat.getName() : UltraCosmetics.filterColor(currentHat.getName())));
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
            if (UltraCosmetics.getInstance().isVaultLoaded() && UltraCosmetics.economy != null)
                return UltraCosmetics.economy.getBalance(getPlayer());
        } catch (Exception exc) {
            UltraCosmetics.log("Error happened while getting a player's balance.");
            return 0;
        }
        return 0;
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
            getPlayer().sendMessage(MessageManager.getMessage("Hats.Must-Remove-Hat"));
            return;
        }

        getPlayer().getInventory().setHelmet(hat.getItemStack());

        getPlayer().sendMessage(MessageManager.getMessage("Hats.Equip")
                .replace("%hatname%",
                        (UltraCosmetics.getInstance().placeholdersHaveColor()) ? hat.getName() : UltraCosmetics.filterColor(hat.getName())));
        currentHat = hat;
    }

    /**
     * Clears all gadgets.
     */
    public void clear() {
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
        for (ArmorSlot armorSlot : ArmorSlot.values())
            removeSuit(armorSlot);
    }

    /**
     * Opens the Key Purchase Menu.
     */
    public void openKeyPurchaseMenu() {
        if (!UltraCosmetics.getInstance().isVaultLoaded())
            return;
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

            Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
                @Override
                public void run() {
                    getPlayer().openInventory(inventory);
                }
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
            getPlayer().sendMessage(MessageManager.getMessage("Particle-Effects.Unsummon").replace("%effectname%", (UltraCosmetics.getInstance().placeholdersHaveColor()) ?
                    currentParticleEffect.getType().getName() : UltraCosmetics.filterColor(currentParticleEffect.getType().getName())));
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
        if (UltraCosmetics.getInstance().usingFileStorage()) SettingsManager.getData(getPlayer()).set("Pet-Names." + petName, name);
        else UltraCosmetics.sqlUtils.setName(getPlayer(), petName, name);
    }

    /**
     * Gets the name of a pet.
     *
     * @param petName The pet.
     * @return The pet name.
     */
    public String getPetName(String petName) {
        try {
            if (UltraCosmetics.getInstance().usingFileStorage()) {
                return SettingsManager.getData(getPlayer()).get("Pet-Names." + petName);
            } else {
                if (UltraCosmetics.sqlUtils.getPetName(getPlayer(), petName).equalsIgnoreCase("Unknown"))
                    return null;
                return UltraCosmetics.sqlUtils.getPetName(getPlayer(), petName);
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
        if (UltraCosmetics.getInstance().isAmmoEnabled())
            if (UltraCosmetics.getInstance().usingFileStorage())
                SettingsManager.getData(getPlayer()).set("Ammo." + name, getAmmo(name) + amount);
            else
                UltraCosmetics.sqlUtils.addAmmo(getPlayer().getUniqueId(), name, amount);
        if (currentGadget != null)
            getPlayer().getInventory().setItem((int) SettingsManager.getConfig().get("Gadget-Slot"),
                    ItemFactory.create(currentGadget.getMaterial(), currentGadget.getData(),
                            "§f§l" + UltraCosmetics.getCustomPlayer(getPlayer()).getAmmo(currentGadget.getType().toString()
                                    .toLowerCase()) + " " + currentGadget.getName(), MessageManager.getMessage("Gadgets.Lore")));
    }

    /**
     * Sets if player has gadgets enabled.
     *
     * @param enabled if player has gadgets enabled.
     */
    public void setGadgetsEnabled(Boolean enabled) {
        try {
            if (UltraCosmetics.getInstance().usingFileStorage()) {
                SettingsManager.getData(getPlayer()).set("Gadgets-Enabled", enabled);
            } else {
                UltraCosmetics.sqlUtils.setGadgetsEnabled(getPlayer(), enabled);
            }
            if (enabled) {
                getPlayer().sendMessage(MessageManager.getMessage("Enabled-Gadgets"));
                this.cache_hasGadgetsEnable = 1;
            } else {
                getPlayer().sendMessage(MessageManager.getMessage("Disabled-Gadgets"));
                this.cache_hasGadgetsEnable = 0;
            }
        } catch (NullPointerException e) {
        }
    }

    /**
     * @return if the player has gadgets enabled or not.
     */
    public boolean hasGadgetsEnabled() {
        if (this.cache_hasGadgetsEnable > -1)
            return cache_hasGadgetsEnable == 0 ? false : true;
        // Make sure it won't be affected before load finished, especially for SQL
        if(!isLoaded)
        	return false;

        try {
            if (UltraCosmetics.getInstance().usingFileStorage()) {
                return SettingsManager.getData(getPlayer()).get("Gadgets-Enabled");
            } else {
            	if(UltraCosmetics.sqlUtils.hasGadgetsEnabled(getPlayer())){
            		cache_hasGadgetsEnable = 1;
            		return true;
            	}else{
            		cache_hasGadgetsEnable = 0;
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
        if (UltraCosmetics.getInstance().usingFileStorage()) {
            SettingsManager.getData(getPlayer()).set("Third-Person-Morph-View", enabled);
        } else {
            UltraCosmetics.sqlUtils.setSeeSelfMorph(getPlayer(), enabled);
        }
        if (enabled) {
            getPlayer().sendMessage(MessageManager.getMessage("Enabled-SelfMorphView"));
            this.cache_canSeeSelfMorph = 1;
        } else {
            getPlayer().sendMessage(MessageManager.getMessage("Disabled-SelfMorphView"));
            this.cache_canSeeSelfMorph = 0;
        }
    }

    /**
     * @return if player should be able to see his own morph or not.
     */
    public boolean canSeeSelfMorph() {
        if (this.cache_canSeeSelfMorph > -1)
            return this.cache_canSeeSelfMorph == 0 ? false : true;
        // Make sure it won't be affected before load finished, especially for SQL
        if(!isLoaded)
        	return false;
        try {
            if (UltraCosmetics.getInstance().usingFileStorage()) {
                return SettingsManager.getData(getPlayer()).get("Third-Person-Morph-View");
            } else {
            	if(UltraCosmetics.sqlUtils.canSeeSelfMorph(getPlayer())){
            		cache_canSeeSelfMorph = 1;
            		return true;
            	}else{
            		cache_canSeeSelfMorph = 0;
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
        if (UltraCosmetics.getInstance().isAmmoEnabled())
            if (UltraCosmetics.getInstance().usingFileStorage())
                return (int) SettingsManager.getData(getPlayer()).get("Ammo." + name);
            else
                return UltraCosmetics.sqlUtils.getAmmo(getPlayer().getUniqueId(), name);
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
        if (UltraCosmetics.getInstance().isAmmoEnabled()) {
            if (UltraCosmetics.getInstance().usingFileStorage()) {
                SettingsManager.getData(getPlayer()).set("Ammo." + name, getAmmo(name) - 1);
            } else {
                UltraCosmetics.sqlUtils.removeAmmo(getPlayer().getUniqueId(), name);
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

    /**
     * Gets the UUID.
     *
     * @return The UUID.
     */
    public UUID getUuid() {
        return uuid;
    }
}